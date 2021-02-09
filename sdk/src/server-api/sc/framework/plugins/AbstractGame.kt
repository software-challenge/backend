package sc.framework.plugins

import org.slf4j.LoggerFactory
import sc.api.plugins.IGameInstance
import sc.api.plugins.IGameState
import sc.api.plugins.exceptions.GameLogicException
import sc.api.plugins.host.IGameListener
import sc.protocol.responses.ProtocolErrorMessage
import sc.protocol.responses.ProtocolMessage
import sc.shared.*

abstract class AbstractGame<P : Player>(override val pluginUUID: String) : IGameInstance {
    companion object {
        val logger = LoggerFactory.getLogger(AbstractGame::class.java)
    }
    
    override val players = mutableListOf<P>()
    
    var activePlayer: P? = null
        protected set
    
    protected val listeners = mutableListOf<IGameListener>()
    
    private var moveRequestTimeout: ActionTimeout? = null
    
    var isPaused = false
    
    fun afterPause() {
        logger.info("Sending MoveRequest to player $activePlayer")
        notifyOnNewState(currentState, false)
        notifyActivePlayer()
    }
    
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
        var error: String? = null
        if (fromPlayer == activePlayer) {
            moveRequestTimeout?.let { timer ->
                timer.stop()
                logger.info("Time needed for move:" + timer.timeDiff)
                if (timer.didTimeout()) {
                    logger.warn("Client hit soft-timeout.")
                    fromPlayer.softTimeout = true
                    onPlayerLeft(fromPlayer, ScoreCause.SOFT_TIMEOUT)
                } else {
                    onRoundBasedAction(fromPlayer, data)
                }
            } ?: run {
                error = "We didn't request a move from you yet."
            }
        } else {
            error = "It's not your turn yet; expected: $activePlayer, got $fromPlayer (msg was $data)."
        }
        error?.let {
            fromPlayer.notifyListeners(ProtocolErrorMessage(data, it))
            throw GameLogicException(it)
        }
    }

    /** Called by [onAction] to execute a move from a Player. */
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
        moveRequestTimeout?.stop()
        moveRequestTimeout = null
    }
    
    /** Starts the game by sending a [WelcomeMessage] to all players and calling [next]. */
    override fun start() {
        players.forEach { it.notifyListeners(WelcomeMessage(it.color)) }
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
        val scores = HashMap(generateScoreMap())
        scores[player]?.let { score ->
            scores[player] = PlayerScore(newCause, score.reason, score.parts)
        }
        notifyOnGameOver(scores)
    }

    /** Advances the Game to [nextPlayer].
     * - sends out a state update
     * - invokes [notifyOnGameOver] if the game is over
     * - updates [activePlayer] to [nextPlayer]
     * - requests a new move if [isPaused] is false
     */
    protected fun next(nextPlayer: P?) {
        // if paused, notify observers only (e.g. to update the GUI)
        notifyOnNewState(currentState, isPaused)
        
        if (checkWinCondition() != null) {
            logger.debug("Game over")
            notifyOnGameOver(generateScoreMap())
        } else {
            logger.debug("Next player: $nextPlayer")
    
            activePlayer = nextPlayer
            if (!isPaused) {
                notifyActivePlayer()
            }
        }
    }

    abstract fun getScoreFor(player: P): PlayerScore

    /** @return the current state representation. */
    abstract val currentState: IGameState

    /** Notifies the active player that it's their time to make a move. */
    protected fun notifyActivePlayer() {
        activePlayer?.let { requestMove(it) } ?:
            throw IllegalStateException("Trying to notify active player but it is null")
    }

    /** Sends a MoveRequest directly to the given player.
     * Does not consider the pause state. */
    protected fun requestMove(player: P) {
        val timeout: ActionTimeout = if (player.canTimeout) getTimeoutFor(player) else ActionTimeout(false)

        // Signal the JVM to do a GC run now and lower the propability that the GC
        // runs when the player sends back its move, resulting in disqualification
        // because of soft timeout.
        System.gc()

        moveRequestTimeout = timeout
        timeout.start {
            logger.warn("Player $player reached the timeout of ${timeout.hardTimeout}ms")
            player.hardTimeout = true
            onPlayerLeft(player, ScoreCause.HARD_TIMEOUT)
        }

        player.requestMove()
    }

    protected open fun getTimeoutFor(player: P) = ActionTimeout(true)

    fun generateScoreMap(): Map<Player, PlayerScore> =
            players.associate { it to getScoreFor(it) }

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
                logger.error("GameOver notification caused an exception, scores: $map", e)
            }
        }
    }

    protected fun notifyOnNewState(mementoState: IGameState, observersOnly: Boolean) {
        listeners.forEach {
            logger.debug("Notifying $it about new game state")
            try {
                it.onStateChanged(mementoState, observersOnly)
            } catch (e: Exception) {
                logger.error("NewState Notification caused an exception", e)
            }
        }
    }

    /**
     * For use in a catch block when an invalid move was performed.
     * Logs and notifies listeners about the violation.
     *
     * @param e      catched Exception, rethrown at the end
     * @param author player who caused the exception
     *
     * @throws InvalidMoveException Always thrown
     */
    @Throws(InvalidMoveException::class)
    fun handleInvalidMove(e: InvalidMoveException, author: Player): Nothing {
        val error = "Ungueltiger Zug von '${author.displayName}'.\n$e"
        logger.error(error)
        author.violationReason = e.message
        author.notifyListeners(ProtocolErrorMessage(e.move, error))
        throw e
    }
}
