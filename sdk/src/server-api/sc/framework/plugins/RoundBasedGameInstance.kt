package sc.framework.plugins

import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamImplicit
import com.thoughtworks.xstream.annotations.XStreamOmitField
import org.slf4j.LoggerFactory
import sc.api.plugins.IGameInstance
import sc.api.plugins.IGameState
import sc.api.plugins.exceptions.GameLogicException
import sc.api.plugins.host.IGameListener
import sc.protocol.responses.ProtocolErrorMessage
import sc.protocol.responses.ProtocolMessage
import sc.shared.InvalidMoveException
import sc.shared.PlayerScore
import sc.shared.ScoreCause
import sc.shared.WinCondition
import java.util.*
import kotlin.jvm.Throws

abstract class RoundBasedGameInstance<P : Player>(@XStreamOmitField override val pluginUUID: String) : IGameInstance {
    companion object {
        val logger = LoggerFactory.getLogger(RoundBasedGameInstance::class.java)
    }

    protected var activePlayer: P? = null

    @XStreamAsAttribute
    var turn = 0
        private set
    open val round: Int
        get() = turn / 2

    @XStreamOmitField
    private var paused = Optional.empty<Int>()

    @XStreamOmitField
    private var requestTimeout: ActionTimeout? = null

    @XStreamOmitField
    protected val listeners = mutableListOf<IGameListener>()

    @XStreamImplicit(itemFieldName = "player")
    override val players = mutableListOf<P>()

    /**
     * Called by the Server once an action was received.
     *
     * @param fromPlayer The player who invoked this action.
     * @param data       The plugin-specific data.
     *
     * @throws GameLogicException if any invalid action is done, i.e. game rule violation
     */
    @Throws(GameLogicException::class)
    override fun onAction(fromPlayer: Player, data: ProtocolMessage) {
        var error = Optional.empty<String>()
        if (fromPlayer == activePlayer) {
            if (wasMoveRequested()) {
                requestTimeout!!.stop()

                if (requestTimeout!!.didTimeout()) {
                    logger.warn("Client hit soft-timeout.")
                    fromPlayer.softTimeout = true
                    onPlayerLeft(fromPlayer, ScoreCause.SOFT_TIMEOUT)
                } else {
                    onRoundBasedAction(fromPlayer, data)
                }
            } else {
                error = Optional.of("We didn't request a move from you yet.")
            }
        } else {
            error = Optional.of("It's not your turn yet; expected: $activePlayer, got $fromPlayer (msg was $data).")
        }
        if (error.isPresent) {
            fromPlayer.notifyListeners(ProtocolErrorMessage(data, error.get()))
            throw GameLogicException(error.get())
        }
    }

    private fun wasMoveRequested(): Boolean = requestTimeout != null

    @Throws(GameLogicException::class, InvalidMoveException::class)
    abstract fun onRoundBasedAction(fromPlayer: Player, data: ProtocolMessage)

    /**
     * Checks if a win condition in the current game state is met.
     * Checks round limit and end of round (and playerStats).
     * Checks if goal is reached.
     *
     * @return WinCondition with winner and reason or null, if no win condition is yet met.
     */
    abstract fun checkWinCondition(): WinCondition?

    /**
     * At any time this method might be invoked by the server.
     * Any open handles should be removed.
     * No events should be sent out (GameOver etc) after this method has been called.
     */
    override fun destroy() {
        logger.info("Destroying Game")
        requestTimeout?.stop()
        requestTimeout = null
    }

    /** Server or an administrator requests the game to start now. */
    override fun start() {
        next(players.first())
    }

    /**
     * Handle leave of a player.
     *
     * @param player the player that left.
     * @param cause  the cause for the leave. If none is provided, then it will either be {@link ScoreCause#RULE_VIOLATION} or {$link ScoreCause#LEFT}, depending on whether the player has {@link Player#hasViolated()}.
     */
    override fun onPlayerLeft(player: Player, cause: ScoreCause?) {
        if (cause == ScoreCause.REGULAR) return
        val newCause = cause ?:
            if (!player.hasViolated()) {
                player.left = true
                ScoreCause.LEFT
            } else {
                ScoreCause.RULE_VIOLATION
            }
        val scores = generateScoreMap().toMutableMap()
        scores.forEach {
            if (it.key == player) {
                val score = it.value
                scores[it.key] = PlayerScore(newCause, score.reason, score.parts)
            }
        }

        notifyOnGameOver(scores.toMap())
    }

    protected fun next(nextPlayer: P?, firstTurn: Boolean = false) {
        logger.debug("next round ($round) for player $nextPlayer")
        if (!firstTurn)
            turn++

        activePlayer = nextPlayer
        // if paused, notify observers only (so they can update the GUI appropriately)
        notifyOnNewState(currentState, isPaused)

        if (checkWinCondition() != null) {
            notifyOnNewState(currentState, isPaused)
        } else {
            if (!isPaused) {
                notifyActivePlayer()
            }
        }
    }

    abstract fun getScoreFor(p: P): PlayerScore

    /** @return the current state representation. */
    abstract val currentState: IGameState

    /** Notifies the active player that it's their time to make a move. */
    protected fun notifyActivePlayer() {
        requestMove(activePlayer!!)
    }

    /**
     * Sends a MoveRequest directly to the player (does not take PAUSE into account).
     *
     * @param player player to make a move
     */
    protected fun requestMove(player: P) {
        val timeout: ActionTimeout = if (player.canTimeout) getTimeoutFor(player) else ActionTimeout(false)

        // Signal the JVM to do a GC run now and lower the propability that the GC
        // runs when the player sends back its move, resulting in disqualification
        // because of soft timeout.
        System.gc()

        requestTimeout = timeout
        timeout.start {
            logger.warn("Player $player reached the timeout of ${timeout.hardTimeout}ms")
            player.hardTimeout = true
            onPlayerLeft(player, ScoreCause.HARD_TIMEOUT)
        }

        player.requestMove()
    }

    protected open fun getTimeoutFor(player: P) = ActionTimeout(true)

    val isPaused: Boolean
        get() = paused.map { turn > it }.orElse(false)

    fun afterPause() {
        logger.info("Sending MoveRequest to player $activePlayer")
        notifyOnNewState(currentState, false)
        notifyActivePlayer()
    }

    /**
     * Pauses game
     *
     * @param pause true if game should be paused
     */
    fun setPauseMode(pause: Boolean) {
        paused = when {
            !pause -> Optional.empty()
            wasMoveRequested() -> Optional.of(turn + 1)
            else -> Optional.of(turn)
        }
    }

    fun generateScoreMap(): Map<Player, PlayerScore> =
            players.map { it to getScoreFor(it) }.toMap()


    /**
     * Extends the set of listeners.
     *
     * @param listener GameListener to be added
     */
    override fun addGameListener(listener: IGameListener) {
        listeners.add(listener)
    }

    /**
     * Removes listener
     *
     * @param listener GameListener to be removed
     */
    override fun removeGameListener(listener: IGameListener) {
        listeners.remove(listener)
    }

    protected fun notifyOnGameOver(map: Map<Player, PlayerScore>) {
        listeners.forEach {
            try {
                it.onGameOver(map)
            } catch (e: Exception) {
                logger.error("GameOver Notification caused an exception.", e)
            }
        }
    }

    protected fun notifyOnNewState(mementoState: IGameState, observersOnly: Boolean) {
        listeners.forEach {
            logger.debug("notifying $it about new game state", it)
            try {
                it.onStateChanged(mementoState, observersOnly)
            } catch (e: Exception) {
                logger.error("NewState Notification caused an exception.", e)
            }
        }
    }

    /**
     * Catch block, after an invalid move was performed
     *
     * @param e      catched Exception, rethrown at the end
     * @param author player, that caused the exception
     *
     * @throws InvalidMoveException Always thrown
     */
    @Throws(InvalidMoveException::class)
    fun catchInvalidMove(e: InvalidMoveException, author: Player) {
        val error = "Ungueltiger Zug von '${author.displayName}'.\n$e"
        logger.error(error)
        author.violationReason = e.message
        author.notifyListeners(ProtocolErrorMessage(e.move, error))
        throw e
    }
}
