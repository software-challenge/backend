package sc.framework.plugins

import org.slf4j.LoggerFactory
import sc.api.plugins.*
import sc.api.plugins.exceptions.GameLogicException
import sc.api.plugins.exceptions.NotYourTurnException
import sc.api.plugins.exceptions.TooManyPlayersException
import sc.api.plugins.host.IGameListener
import sc.protocol.room.WelcomeMessage
import sc.shared.*

fun <T> Iterable<T>.maxByNoEqual(selector: (T) -> Int): T? =
        fold(Int.MIN_VALUE to (null as T?)) { acc, pos ->
            val value = selector(pos)
            when {
                value > acc.first -> value to pos
                value == acc.first -> value to null
                else -> acc
            }
        }.second


class TwoPlayerGame<Move : IMove, GameState: TwoPlayerGameState<Move>>(plugin: IGamePlugin<Move>, override val currentState: GameState): AbstractGame<Move>(plugin) {
    override fun onRoundBasedAction(move: IMove) {
        if(!plugin.moveClass.isInstance(move))
            throw InvalidMoveException(MoveMistake.INVALID_FORMAT)
        
        logger.debug("Performing {}", move)
        currentState.performMoveDirectly(plugin.moveClass.cast(move))
        logger.debug("Current State: ${currentState.longString()}")
    }
}

abstract class AbstractGame<M : IMove>(protected val plugin: IGamePlugin<M>): IGameInstance, Pausable {
    protected val logger = LoggerFactory.getLogger(this::class.java)
    
    override val pluginUUID: String = plugin.id
    
    abstract val currentState: IGameState
    
    override val players = mutableListOf<Player>()
    
    val activePlayer
        get() = players[currentState.currentTeam.index]
    
    protected val listeners = mutableListOf<IGameListener>()
    
    private var moveRequestTimeout: ActionTimeout? = null
    
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
            logger.info("Time needed for move: " + timer.timeDiff + "ms")
            if(timer.didTimeout()) {
                logger.warn("Client hit soft-timeout.")
                fromPlayer.violation = Violation.SOFT_TIMEOUT(getTimeoutFor(fromPlayer).softTimeout / 1000)
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
    
    /** Stops pending MoveRequests and invokes [notifyOnGameOver]. */
    override fun stop() {
        logger.info("Stopping {}", this)
        moveRequestTimeout?.stop()
        moveRequestTimeout = null
        notifyOnGameOver(getResult())
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
    
    /** Notifies the active player that it's their time to make a move. */
    protected fun notifyActivePlayer() {
        requestMove(activePlayer)
    }
    
    /** Sends a MoveRequest directly to the given player.
     * Does not consider the pause state. */
    protected fun requestMove(player: Player) {
        val timeout: ActionTimeout = if(player.canTimeout) getTimeoutFor(player) else ActionTimeout(false)
        
        // Signal the JVM to do a GC run now and lower the probability
        // that the GC runs when the player sends back its move,
        // potentially causing a disqualification because of soft timeout.
        System.gc()
        
        moveRequestTimeout = timeout
        timeout.start {
            logger.warn("Player $player reached the timeout of ${timeout.hardTimeout}ms")
            player.violation = Violation.HARD_TIMEOUT(getTimeoutFor(player).softTimeout / 1000)
            stop()
        }
        
        logger.info("Sending MoveRequest to player $activePlayer")
        player.requestMove()
    }
    
    protected open fun getTimeoutFor(player: Player) =
            ActionTimeout(true, Constants.HARD_TIMEOUT, Constants.SOFT_TIMEOUT)
    
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
    
    protected fun notifyOnGameOver(result: GameResult) {
        listeners.forEach {
            try {
                it.onGameOver(result)
            } catch(e: Exception) {
                logger.error("GameOver notification caused an exception, scores: $result", e)
            }
        }
    }
    
    protected fun notifyOnNewState(mementoState: IGameState, observersOnly: Boolean) {
        listeners.forEach {
            if(logger.isDebugEnabled)
                logger.debug("Notifying $it about new game state with hash ${mementoState.hashCode()}")
            try {
                it.onStateChanged(mementoState, observersOnly)
            } catch(e: Exception) {
                logger.error("NewState Notification caused an exception", e)
            }
        }
    }
    
    override val winner: ITeam?
        get() = players.singleOrNull { it.violation == null }?.team ?: checkWinCondition()?.winner
    
    /**
     * Returns a WinCondition if the Game is over.
     * Checks:
     * - if a win condition in the current game state is met
     * - round limit and end of round (and playerStats)
     * - whether goal is reached
     *
     * @return WinCondition, or null if game is not regularly over yet
     */
    fun checkWinCondition(): WinCondition? =
            currentState.winCondition
    
    // TODO this function is useless cause it is only used when the game is over
    //      in which case currentState.winCondition is not null anyway
    fun currentWinner(): WinCondition {
        val teams = Team.values()
        val scores = teams.map { currentState.getPointsForTeam(it) }
        return currentState.winCondition ?: plugin.scoreDefinition.asSequence()
                .drop(1) // drop victory points definition
                .withIndex().filter { it.value.relevantForRanking }
                .map { (index, scoreFragment) ->
                    WinCondition(
                        teams.withIndex().maxByNoEqual { team -> (if(scoreFragment.invert) -1 else 1) * scores[team.index][index] }?.value,
                        scoreFragment.explanation
                    )
                }
                .firstOrNull { it.winner != null } ?: WinCondition(null, WinReasonTie)
    }
    
    /** Gets the [GameResult] if the Game where to end at the current state. */
    fun getResult(): GameResult {
        val winCondition: WinCondition
        val violator = players.find { it.violation != null }
        val scores =
                if(violator != null) {
                    winCondition = WinCondition(players.find { it.violation == null }?.team, violator.violation!!)
                    players.associateWith {
                        if(it.violation == null) {
                            PlayerScore(Constants.WIN_SCORE, *currentState.getPointsForTeam(it.team))
                        } else {
                            plugin.scoreDefinition.emptyScore()
                        }
                    }
                } else {
                    winCondition = currentWinner()
                    players.associateWith {
                        PlayerScore(
                                when(winCondition.winner) {
                                    it.team -> Constants.WIN_SCORE
                                    null -> Constants.DRAW_SCORE
                                    else -> Constants.LOSE_SCORE
                                }, *currentState.getPointsForTeam(it.team))
                    }
                }
        return GameResult(plugin.scoreDefinition, scores, winCondition)
    }
    
    override fun toString(): String =
        "Game(${
            when {
                currentState.isOver -> "OVER, "
                isPaused -> "PAUSED, "
                else -> ""
            }
        }players=$players, gameState=$currentState)"
    
}