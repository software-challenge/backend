package sc.framework.plugins;

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
import java.lang.NullPointerException
import java.util.*

abstract class RoundBasedGameInstance : IGameInstance {
    companion object {
        val logger = LoggerFactory.getLogger(RoundBasedGameInstance::class.java)
    }
    
    protected var activePlayer: Player? = null
    
    private var turn: Int = 0
    
    @XStreamOmitField
    private var paused = Optional.empty<Int>()
    
    @XStreamOmitField
    private var requestTimeout: ActionTimeout? = null
    
    @XStreamOmitField
    protected val listeners = mutableListOf<IGameListener>()
    
    @XStreamImplicit(itemFieldName = "player")
    override val players = mutableListOf<Player>()
    
    fun getRound(): Int = turn / 2
    
    /**
     * Called by the Server once an action was received.
     *
     * @param fromPlayer The player who invoked this action.
     * @param data       The plugin-specific data.
     *
     * @throws GameLogicException if any invalid action is done, i.e. game rule violation
     */
    override fun onAction(fromPlayer: Player, data: ProtocolMessage) {
        var errorMsg: String? = null
        if (fromPlayer == activePlayer) {
            if (requestTimeout != null) {
                requestTimeout!!.stop()
                
                if (requestTimeout!!.didTimeout()) {
                    logger.warn("Client hit soft-timeout.")
                    fromPlayer.softTimeout = true
                    onPlayerLeft(fromPlayer, ScoreCause.SOFT_TIMEOUT)
                } else {
                    onRoundBasedAction(fromPlayer, data)
                }
            } else {
                errorMsg = "We didn't request a move from you yet"
            }
        } else {
            errorMsg = "It's not your turn yet."
        }
        if (errorMsg != null) {
            fromPlayer.notifyListeners(ProtocolErrorMessage(data, errorMsg))
            throw GameLogicException(errorMsg)
        }
    }
    
    protected abstract fun onRoundBasedAction(fromPlayer: Player, data: ProtocolMessage)
    
    protected abstract fun checkWinCondition(): WinCondition?
    
    override fun destroy() {
        logger.info("Destroying Game")
        requestTimeout?.stop()
        requestTimeout = null
    }
    
    override fun start() {
        next(players.first(), true)
    }
    
    override fun onPlayerLeft(player: Player, cause: ScoreCause?) {
        if (cause == ScoreCause.REGULAR)
            return
        
        cause.let {
            when (player.violated) {
                true -> ScoreCause.RULE_VIOLATION
                false -> {
                    player.left = true
                    ScoreCause.LEFT
                }
            }
        }
        val scores = generateScoreMap()
        scores.getValue(player).cause = cause
        notifyOnGameOver(scores)
    }
    
    fun next(nextPlayer: Player, firstTurn: Boolean = false) {
        logger.debug("next round (${getRound()}) for player $nextPlayer")
        if (!firstTurn)
            turn++
        activePlayer = nextPlayer
        // don't notify on new state if game is paused or client may begin to calculate something
        if (!isPaused())
            notifyOnNewState(getCurrentState())
    }
    
    abstract fun getScoreFor(player: Player): PlayerScore
    
    protected abstract fun getCurrentState(): IGameState
    
    protected fun notifyActivePlayer() {
        try {
            requestMove(activePlayer!!)
        } catch (e: NullPointerException) {
            logger.error("Can't notify active player because there is no player")
            throw e
        }
    }
    
    protected fun requestMove(player: Player) {
        val timeout =
                if (player.isCanTimeout) getTimeoutFor(player)
                else ActionTimeout(false)
        
        val logger = logger
        val playerToTimeout = player
        
        // Signal the JVM to do a GC run now and lower the probability that the GC
        // runs when the players sends back its move, resulting in disqualification
        // because of soft timeout
        System.gc()
        
        requestTimeout = timeout
        timeout.start {
            logger.warn("Player $playerToTimeout reached the timeout of ${timeout.hardTimeout}ms")
            playerToTimeout.hardTimeout = true
            onPlayerLeft(playerToTimeout, ScoreCause.HARD_TIMEOUT)
        }
        player.requestMove()
    }
    
    protected open fun getTimeoutFor(player: Player) = ActionTimeout(true)
    
    fun isPaused():Boolean =
            paused.map { it <= turn }.orElse(false)
    
    fun afterPause() {
        logger.info("Sending MoveRequest to player $activePlayer")
        notifyOnNewState(getCurrentState())
        notifyActivePlayer()
    }
    
    fun setPauseMode(pause: Boolean) {
        paused = if (pause) {
            if (requestTimeout != null) Optional.of(turn + 1)
            else Optional.of(turn)
        } else Optional.empty()
    }
    
    fun generateScoreMap(): Map<Player, PlayerScore> =
            players.map {
                it to getScoreFor(it)
            }.toMap()
    
    override fun addGameListener(listener: IGameListener) {
        listeners.add(listener)
    }
    
    override fun removeGameListener(listener: IGameListener) {
        listeners.remove(listener)
    }
    
    protected fun notifyOnGameOver(map: Map<Player, PlayerScore>) {
        for (listener in listeners) {
            try {
                listener.onGameOver(map)
            } catch (e: Exception) {
                logger.error("GameOver Notification caused an exception.", e)
            }
        }
    }
    
    protected fun notifyOnNewState(mementoState: IGameState) {
        for (listener in listeners) {
            logger.debug("notifying $listener about new game state")
            try {
                listener.onStateChanged(mementoState)
            } catch (e: Exception) {
                logger.error("NewState Notification caused an exception.", e)
            }
        }
    }
    
    fun catchInvalidMove(e: InvalidMoveException, author: Player) {
        author.violated = true
        val errorMsg = "Ungueltiger Zug von ${author.displayName}'.\n$e"
        author.violationReason = e.message
        logger.error(errorMsg)
        author.notifyListeners(ProtocolErrorMessage(e.move, errorMsg))
        throw e
    }
    
    fun addPlayer(player: Player) {
        players.add(player)
    }
}
