package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias
import org.slf4j.LoggerFactory
import sc.api.plugins.IGameState
import sc.framework.plugins.ActionTimeout
import sc.framework.plugins.RoundBasedGameInstance
import sc.framework.plugins.Player
import sc.plugin2021.util.Constants
import sc.plugin2021.util.GameRuleLogic
import sc.plugin2021.util.WinReason
import sc.protocol.responses.ProtocolMessage
import sc.shared.*

@XStreamAlias(value = "game")
class Game: RoundBasedGameInstance<Player>(GamePlugin.PLUGIN_UUID) {
    companion object {
        val logger = LoggerFactory.getLogger(Game::class.java)
    }
    
    private val availableTeams = mutableListOf(Team.ONE, Team.TWO)
    val gameState = GameState()
    
    override fun start() {
        players.forEach {it.notifyListeners(WelcomeMessage(it.color)) }
//        next(players.first(), true)
        super.start()
    }
   
    override fun onPlayerJoined(): Player {
        val player = gameState.getPlayer(availableTeams.removeAt(0))
                ?: throw IllegalStateException("Too many players joined the game!")
        
        players.add(player)
        playerMap[player.color as Team] = player
        gameState.addPlayer(player)
        return player
    }
    
    override fun getWinners(): MutableList<Player> {
        if (players.first().hasViolated()) {
            if (players.last().hasViolated())
                return mutableListOf()
            return players.subList(1, 2)
        }
        if (players.last().hasViolated())
            return players.subList(0, 1)
        
        val first = gameState.getPointsForPlayer(players.first().color)
        val second = gameState.getPointsForPlayer(players.last().color)
        
        if (first > second)
            return players.subList(0, 1)
        if (first < second)
            return players.subList(1, 2)
        return players
    }
    
    override fun getPlayerScores(): MutableList<PlayerScore> =
            getPlayers().map { getScoreFor(it) }.toMutableList()
    
    override fun getPlayers(): MutableList<Player> = players
    
    private val playerMap = mutableMapOf<Team, Player>()
    
    override fun getRound(): Int = gameState.round
    
    /**
     * Checks if any player can still make moves.
     * If so, returns null; otherwise returns
     * the player with the highest cumulative score of its colors
     */
    override fun checkWinCondition(): WinCondition? {
        if (!checkGameOver()) return null

        val scores: Map<Team, Int> = Team.values().map {
            it to gameState.getPointsForPlayer(it)
        }.toMap()

        return when {
            scores.getValue(Team.ONE) > scores.getValue(Team.TWO) -> WinCondition(Team.ONE, WinReason.DIFFERING_SCORES)
            scores.getValue(Team.ONE) < scores.getValue(Team.TWO) -> WinCondition(Team.TWO, WinReason.DIFFERING_SCORES)
            else -> WinCondition(null, WinReason.EQUAL_SCORE)
        }
    }
    
    override fun loadGameInfo(gameInfo: Any?) {
        TODO("Not yet implemented")
    }
    
    override fun loadFromFile(file: String?) {
        TODO("Not yet implemented")
    }
    
    override fun loadFromFile(file: String?, turn: Int) {
        TODO("Not yet implemented")
    }
    
    override fun getScoreFor(player: Player): PlayerScore {
        val team = player.color as Team
        logger.debug("Get score for player $team (violated: ${if(player.hasViolated()) "yes" else "no"})")
        val opponent = gameState.getOpponent(player)!!
        val winCondition = checkWinCondition()
        
        var cause: ScoreCause = ScoreCause.REGULAR
        var reason = ""
        var score: Int = Constants.LOSE_SCORE
        val points = gameState.getPointsForPlayer(team)
        
        // Is the game already finished?
        if (winCondition?.reason == WinReason.EQUAL_SCORE)
            score = Constants.DRAW_SCORE
        if (winCondition?.reason == WinReason.DIFFERING_SCORES)
            if (winCondition.winner == team)
                score = Constants.WIN_SCORE
        
        // Opponent did something wrong
        if (opponent.hasViolated() && !player.hasViolated() ||
                opponent.hasLeft() && !player.hasLeft() ||
                opponent.hasSoftTimeout() ||
                opponent.hasHardTimeout())
            score = Constants.WIN_SCORE
        else
            when {
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
                    reason = "Der Spieler hat das Spiel verlassen"
                }
            }
        return PlayerScore(cause, reason, score, points)
    }
    
    override fun getTimeoutFor(player: Player): ActionTimeout =
            ActionTimeout(true, Constants.HARD_TIMEOUT, Constants.SOFT_TIMEOUT)
    
    @Throws(InvalidMoveException::class)
    override fun onRoundBasedAction(fromPlayer: Player, data: ProtocolMessage?) {
        try {
            if (data !is Move)
                throw InvalidMoveException("${fromPlayer.displayName} did not send a proper move.")
            
            logger.debug("Current State: $gameState")
            logger.debug("Performing Move $data")
            GameRuleLogic.performMove(gameState, data)
            next(if (checkGameOver()) null else gameState.currentPlayer)
            logger.debug("Current Board:\n${gameState.board}")
        } catch(e: InvalidMoveException) {
            super.catchInvalidMove(e, fromPlayer)
        }
    }
    
    override fun getCurrentState(): IGameState = gameState
    
    val isGameOver: Boolean
        get() = gameState.validColors.isEmpty() || round > Constants.ROUND_LIMIT

    fun checkGameOver(): Boolean {
        if (round > Constants.ROUND_LIMIT) {
            gameState.validColors.clear()
        }
        GameRuleLogic.removeInvalidColors(gameState)
        return isGameOver
    }
}
