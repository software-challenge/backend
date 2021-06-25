package sc.plugin2022

import org.slf4j.LoggerFactory
import sc.api.plugins.IMove
import sc.api.plugins.ITeam
import sc.api.plugins.Team
import sc.api.plugins.exceptions.TooManyPlayersException
import sc.framework.plugins.AbstractGame
import sc.framework.plugins.ActionTimeout
import sc.framework.plugins.Player
import sc.plugin2022.util.Constants
import sc.plugin2022.util.MoveMistake
import sc.plugin2022.util.WinReason
import sc.shared.InvalidMoveException
import sc.shared.PlayerScore
import sc.shared.ScoreCause
import sc.shared.WinCondition
import kotlin.math.abs

fun <T> Collection<T>.maxByNoEqual(selector: (T) -> Int): T? =
        fold(Int.MIN_VALUE to (null as T?)) { acc, pos ->
            val value = selector(pos)
            when {
                value > acc.first -> value to pos
                value == acc.first -> value to null
                else -> acc
            }
        }.second

class Game(override val currentState: GameState = GameState()): AbstractGame<Player>(GamePlugin.PLUGIN_ID) {
    companion object {
        val logger = LoggerFactory.getLogger(Game::class.java)
    }
    
    private val availableTeams = ArrayDeque<ITeam>().also { it.addAll(Team.values()) }
    override fun onPlayerJoined(): Player {
        if (availableTeams.isEmpty())
            throw TooManyPlayersException(this)
        
        val player = Player(availableTeams.removeFirst())
        players.add(player)
        return player
    }
    
    override val winners: List<Player>
        get() {
            val compliant = players.filter { !it.hasViolated() && !it.hasLeft() }
            if (compliant.size < players.size)
                return compliant
            return checkWinCondition().let { cond -> players.firstOrNull { it.team == cond?.winner } }
                           ?.let { listOf(it) } ?: emptyList()
        }
    
    override val playerScores: MutableList<PlayerScore>
        get() = players.mapTo(ArrayList(players.size)) { getScoreFor(it) }
    
    val isGameOver: Boolean
        get() = currentState.isOver
    
    /**
     * Checks whether and why the game is over.
     *
     * @return null if any player can still move, otherwise a WinCondition with the winner and reason.
     */
    override fun checkWinCondition(): WinCondition? {
        if (!isGameOver) return null
        
        return Team.values().toList().maxByNoEqual(currentState::getPointsForTeam)?.let {
            WinCondition(it, WinReason.DIFFERING_SCORES)
        } ?: run {
            val lightPieces = currentState.board.filterValues { it.type.isLight }
            val teamPositions = Team.values().associateWith { team ->
                fun dist(c: Coordinates) = abs(c.x - team.startLine)
                lightPieces.filterValues { it.team == team }.keys.map(::dist).sortedDescending()
            }
            (0 until teamPositions.values.minOf { it.size }).mapNotNull { index ->
                teamPositions.entries.maxByNoEqual { it.value[index] }?.key
            }.firstOrNull().let {
                WinCondition(it, if (it == null) WinReason.EQUAL_SCORE else WinReason.DIFFERING_POSITIONS)
            }
        }
    }
    
    override fun getScoreFor(player: Player): PlayerScore {
        logger.debug("Calculating score for $player")
        val team = player.team as Team
        val opponent = players[team.opponent().index]
        val winCondition = checkWinCondition()
        
        var cause: ScoreCause = ScoreCause.REGULAR
        var reason = ""
        var score: Int = Constants.LOSE_SCORE
    
        // Is the game already finished?
        if (winCondition?.reason == WinReason.EQUAL_SCORE)
            // TODO something is going wrong on draw scores
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
        return PlayerScore(cause,
                reason,
                score,
                currentState.getPointsForTeam(team),
                currentState.board.filterValues { it.team == team && it.type.isLight }.maxOf { it.key.y }
        )
    }
    
    override fun getTimeoutFor(player: Player): ActionTimeout =
            ActionTimeout(true, Constants.HARD_TIMEOUT, Constants.SOFT_TIMEOUT)
    
    @Throws(InvalidMoveException::class)
    override fun onRoundBasedAction(move: IMove) {
        if (move !is Move)
            throw InvalidMoveException(MoveMistake.INVALID_FORMAT)
        
        logger.debug("Performing $move")
        currentState.performMove(move)
        next()
        logger.debug("Current State: ${currentState.longString()}")
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
