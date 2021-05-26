package sc.plugin2021

import org.slf4j.LoggerFactory
import sc.api.plugins.IMove
import sc.api.plugins.exceptions.TooManyPlayersException
import sc.framework.plugins.AbstractGame
import sc.framework.plugins.ActionTimeout
import sc.framework.plugins.Player
import sc.plugin2021.util.Constants
import sc.plugin2021.util.GameRuleLogic
import sc.plugin2021.util.MoveMistake
import sc.plugin2021.util.WinReason
import sc.shared.InvalidMoveException
import sc.shared.PlayerScore
import sc.shared.ScoreCause
import sc.shared.WinCondition

class Game(override val currentState: GameState = GameState()): AbstractGame<Player>(GamePlugin.PLUGIN_ID) {
    companion object {
        val logger = LoggerFactory.getLogger(Game::class.java)
    }
    
    private val availableTeams = mutableListOf(Team.ONE, Team.TWO)
    override fun onPlayerJoined(): Player {
        if (availableTeams.isEmpty())
            throw TooManyPlayersException(this)
        val player = currentState.getPlayer(availableTeams.removeAt(0))
        
        players.add(player)
        currentState.addPlayer(player)
        return player
    }
    
    override val winners: List<Player>
        get() {
            val compliant = players.filter { !it.hasViolated() && !it.hasLeft() }
            if (compliant.size < players.size)
                return compliant
            
            val first = currentState.getPointsForPlayer(players.first().color)
            val second = currentState.getPointsForPlayer(players.last().color)
            
            if (first > second)
                return players.subList(0, 1)
            if (first < second)
                return players.subList(1, 2)
            return players
        }
    
    override val playerScores: MutableList<PlayerScore>
        get() = players.mapTo(ArrayList(players.size)) { getScoreFor(it) }
    
    val isGameOver: Boolean
        get() = !currentState.hasValidColors() || currentState.round > Constants.ROUND_LIMIT
    
    /**
     * Checks whether and why the game is over.
     *
     * @return null if any player can still move, otherwise a WinCondition with the winner and reason.
     */
    override fun checkWinCondition(): WinCondition? {
        if (!isGameOver) return null
        
        val scores: Map<Team, Int> = Team.values().map {
            it to currentState.getPointsForPlayer(it)
        }.toMap()
        
        return when {
            scores.getValue(Team.ONE) > scores.getValue(Team.TWO) -> WinCondition(Team.ONE, WinReason.DIFFERING_SCORES)
            scores.getValue(Team.ONE) < scores.getValue(Team.TWO) -> WinCondition(Team.TWO, WinReason.DIFFERING_SCORES)
            else -> WinCondition(null, WinReason.EQUAL_SCORE)
        }
    }
    
    override fun getScoreFor(player: Player): PlayerScore {
        val team = player.color as Team
        logger.debug("Get score for player $team (violated: ${if (player.hasViolated()) "yes" else "no"})")
        val opponent = currentState.getOpponent(player)
        val winCondition = checkWinCondition()
        
        var cause: ScoreCause = ScoreCause.REGULAR
        var reason = ""
        var score: Int = Constants.LOSE_SCORE
        val points = currentState.getPointsForPlayer(team)
        
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
    override fun onRoundBasedAction(fromPlayer: Player, move: IMove) {
        if (move !is Move)
            throw InvalidMoveException(MoveMistake.INVALID_FORMAT)
        
        logger.debug("Current State: $currentState")
        logger.debug("Performing Move $move")
        GameRuleLogic.performMove(currentState, move)
        GameRuleLogic.removeInvalidColors(currentState)
        next(if (isGameOver) null else currentState.currentPlayer)
        logger.debug("Current Board:\n${currentState.board}")
    }
    
    override fun toString(): String =
            "Game(${
                when {
                    isGameOver -> "OVER, "
                    isPaused -> "PAUSED, "
                    else -> ""
                }
            }players=$players, gameState=$currentState)"
    
}
