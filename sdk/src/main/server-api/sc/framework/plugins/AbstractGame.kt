package sc.framework.plugins

import org.slf4j.LoggerFactory
import sc.api.plugins.*
import sc.api.plugins.exceptions.GameLogicException
import sc.api.plugins.exceptions.NotYourTurnException
import sc.api.plugins.exceptions.TooManyPlayersException
import sc.api.plugins.host.IGameListener
import sc.protocol.room.WelcomeMessage
import sc.shared.*

abstract class AbstractGame(override val pluginUUID: String): IGameInstance, Pausable {
    companion object {
        val logger = LoggerFactory.getLogger(AbstractGame::class.java)
    }
    
    override val players = mutableListOf<Player>()
    
    val activePlayer
        get() = players[currentState.currentTeam.index]
    
    protected val listeners = mutableListOf<IGameListener>()
    
    private var moveRequestTimeout: ActionTimeout? = null
    
    override val winner: ITeam?
        get() = players.singleOrNull { !it.hasViolated() && !it.hasLeft() }?.team
                ?: checkWinCondition()?.also { logger.debug("No Winner via violation, WinCondition: {}", it) }?.winner
    
    /** Pause the game after current turn has finished or continue playing. */
    override var isPaused = false
        set(value) {
            if(!value && moveRequestTimeout == null)
                step()
            field = value
        }
    
    override fun step() {
        if(!isPaused) {
            logger.warn("Cannot step while unpaused")
            return
        }
        logger.debug("Stepping {}", this)
        notifyOnNewState(currentState, false)
        notifyActivePlayer()
    }
    
    /**
     * Called by the Server upon receiving a Move.
     *
     * @throws GameLogicException if no move has been requested from the given [Player]
     * @throws InvalidMoveException when the given Move is not possible
     */
    @Throws(GameLogicException::class, InvalidMoveException::class)
    override fun onAction(fromPlayer: Player, move: IMove) {
        if(fromPlayer != activePlayer)
            throw NotYourTurnException(activePlayer, fromPlayer, move)
        moveRequestTimeout?.let { timer ->
            moveRequestTimeout = null
            timer.stop()
            logger.info("Time needed for move: " + timer.timeDiff)
            if(timer.didTimeout()) {
                logger.warn("Client hit soft-timeout.")
                fromPlayer.softTimeout = true
                stop()
            } else {
                onRoundBasedAction(move)
                next()
            }
        } ?: throw GameLogicException("Move from $fromPlayer has not been requested.")
    }
    
    /** Called by [onAction] to execute a move of a Player. */
    @Throws(InvalidMoveException::class)
    abstract fun onRoundBasedAction(move: IMove)
    
    /**
     * Returns a WinCondition if the Game is over.
     * Checks:
     * - if a win condition in the current game state is met
     * - round limit and end of round (and playerStats)
     * - whether goal is reached
     *
     * @return WinCondition, or null if no win condition is met yet.
     */
    abstract fun checkWinCondition(): WinCondition?
    
    /** Stops pending MoveRequests and invokes [notifyOnGameOver]. */
    override fun stop() {
        logger.info("Stopping {}", this)
        moveRequestTimeout?.stop()
        moveRequestTimeout = null
        notifyOnGameOver(generateScoreMap())
    }
    
    /** Starts the game by sending a [WelcomeMessage] to all players and calling [next]. */
    override fun start() {
        players.forEach { it.notifyListeners(WelcomeMessage(it.team)) }
        next()
    }
    
    /** Advances the Game.
     * - sends out a state update
     * - invokes [notifyOnGameOver] if the game is over
     * - requests a new move if [isPaused] is false
     */
    protected fun next() {
        // if paused, notify observers only (e.g. to update the GUI)
        notifyOnNewState(currentState, isPaused)
        
        if(checkWinCondition() != null) {
            logger.debug("Game over")
            stop()
        } else if(!isPaused) {
            notifyActivePlayer()
        }
    }
    
    private val availableTeams = ArrayDeque<ITeam>().also { it.addAll(Team.values()) }
    override fun onPlayerJoined(): Player {
        if(availableTeams.isEmpty())
            throw TooManyPlayersException(this)
        
        val player = Player(availableTeams.removeFirst())
        players.add(player)
        return player
    }
    
    override val playerScores: List<PlayerScore>
        get() = players.mapTo(ArrayList(players.size)) { getScoreFor(it) }
    fun getScoreFor(player: Player): PlayerScore {
        logger.debug("Calculating score for $player")
        val team = player.team as Team
        val winCondition = checkWinCondition()
        
        var cause: ScoreCause = ScoreCause.REGULAR
        var reason = ""
        var score: Int = Constants.LOSE_SCORE
        
        if(winCondition != null) {
            // Game is already finished
            score = if(winCondition.winner == null)
                Constants.DRAW_SCORE
            else {
                if(winCondition.winner == team) Constants.WIN_SCORE else Constants.LOSE_SCORE
            }
        }
        
        when {
            players.getOrNull(team.opponent().index)?.let { opponent ->
                opponent.hasViolated() && !player.hasViolated() ||
                opponent.hasLeft() && !player.hasLeft() ||
                opponent.hasSoftTimeout() ||
                opponent.hasHardTimeout()
            } == true -> {
                // Opponent did something wrong and we did not
                score = Constants.WIN_SCORE
            }
            player.hasSoftTimeout() -> {
                cause = ScoreCause.SOFT_TIMEOUT
                reason = "Der Spieler hat innerhalb von ${getTimeoutFor(player).softTimeout / 1000} Sekunden nach Aufforderung keinen Zug gesendet"
            }
            player.hasHardTimeout() -> {
                cause = ScoreCause.SOFT_TIMEOUT
                reason = "Der Spieler hat innerhalb von ${getTimeoutFor(player).hardTimeout / 1000} Sekunden nach Aufforderung keinen Zug gesendet"
            }
            player.hasViolated() -> {
                cause = ScoreCause.RULE_VIOLATION
                reason = player.violationReason!!
            }
            player.hasLeft() -> {
                cause = ScoreCause.LEFT
                reason = "Der Spieler hat das Spiel verlassen: ${player.left}"
            }
        }
        return PlayerScore(cause, reason, score, *currentState.getPointsForTeam(team))
    }
    
    /** @return the current state representation. */
    abstract val currentState: IGameState
    
    /** Notifies the active player that it's their time to make a move. */
    protected fun notifyActivePlayer() {
        requestMove(activePlayer)
    }
    
    /** Sends a MoveRequest directly to the given player.
     * Does not consider the pause state. */
    protected fun requestMove(player: Player) {
        val timeout: ActionTimeout = if(player.canTimeout) getTimeoutFor(player) else ActionTimeout(false)
        
        // Signal the JVM to do a GC run now and lower the propability that the GC
        // runs when the player sends back its move, resulting in disqualification
        // because of soft timeout.
        System.gc()
        
        moveRequestTimeout = timeout
        timeout.start {
            logger.warn("Player $player reached the timeout of ${timeout.hardTimeout}ms")
            player.hardTimeout = true
            stop()
        }
        
        logger.info("Sending MoveRequest to player $activePlayer")
        player.requestMove()
    }
    
    protected open fun getTimeoutFor(player: Player) =
            ActionTimeout(true, Constants.HARD_TIMEOUT, Constants.SOFT_TIMEOUT)
    
    @Suppress("ReplaceAssociateFunction")
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
            } catch(e: Exception) {
                logger.error("GameOver notification caused an exception, scores: $map", e)
            }
        }
    }
    
    protected fun notifyOnNewState(mementoState: IGameState, observersOnly: Boolean) {
        listeners.forEach {
            logger.debug("Notifying $it about new game state")
            try {
                it.onStateChanged(mementoState, observersOnly)
            } catch(e: Exception) {
                logger.error("NewState Notification caused an exception", e)
            }
        }
    }
}
