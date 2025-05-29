package sc.plugin2026

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.ITeam
import sc.api.plugins.Stat
import sc.api.plugins.Team
import sc.api.plugins.TwoPlayerGameState
import sc.framework.plugins.maxByNoEqual
import sc.plugin2026.util.GameRuleLogic
import sc.plugin2026.util.PiranhaConstants
import sc.plugin2026.util.PiranhasWinReason
import sc.shared.InvalidMoveException
import sc.shared.WinCondition
import sc.shared.WinReasonTie

/**
 * The GameState class represents the current state of the game.
 *
 * It holds all the information about the current round,
 * to provide all information needed to make the next move.
 *
 * @property board The current game board.
 * @property turn The number of turns already made in the game.
 * @property lastMove The last move made in the game.
 */
@XStreamAlias(value = "state")
data class GameState @JvmOverloads constructor(
    /** Das aktuelle Spielfeld. */
    override val board: Board = Board(),
    /** Die Anzahl an bereits getätigten Zügen. */
    @XStreamAsAttribute override var turn: Int = 0,
    /** Der zuletzt gespielte Zug. */
    override var lastMove: Move? = null,
): TwoPlayerGameState<Move>(Team.ONE) {
    
    override fun getPointsForTeam(team: ITeam): IntArray =
        intArrayOf(GameRuleLogic.greatestSwarmSize(board, team))
    
    // TODO test if one player is surrounded he loses
    override val isOver: Boolean
        get() = Team.values().any { GameRuleLogic.isSwarmConnected(board, it) } && turn.mod(2) == 0 ||
                turn / 2 >= PiranhaConstants.ROUND_LIMIT
    
    override val winCondition: WinCondition?
        get() {
            val winners = Team.values().filter { team -> GameRuleLogic.isSwarmConnected(board, team) }
            return when(winners.size) {
                0 -> null
                1 -> WinCondition(winners.single(), PiranhasWinReason.SOLE_SWARM)
                else ->
                    winners.maxByNoEqual { team -> GameRuleLogic.greatestSwarmSize(board, team) }
                        ?.let {
                            WinCondition(it, PiranhasWinReason.BIGGER_SWARM)
                        } ?: WinCondition(null, WinReasonTie)
            }
        }
    
    override fun performMoveDirectly(move: Move) {
        if(board.getTeam(move.from) != currentTeam) {
            throw InvalidMoveException(PiranhaMoveMistake.WRONG_START, move)
        }
        GameRuleLogic.checkMove(board, move)?.let { throw InvalidMoveException(it, move) }
        val distance = GameRuleLogic.movementDistance(board, move)
        board[move.from].state = FieldState.EMPTY
        board[move.from + move.direction.vector * distance].state = FieldState.from(currentTeam)
    }
    
    override fun getSensibleMoves(): List<Move> {
        val piranhas = board.filterValues { field -> field.state.team == currentTeam }
        val moves = ArrayList<Move>(piranhas.size * 2)
        for(piranha in piranhas) {
            moves.addAll(GameRuleLogic.possibleMovesFor(board, piranha.key))
        }
        return moves
    }
    
    override fun moveIterator(): Iterator<Move> =
        getSensibleMoves().iterator()
    
    override fun clone(): TwoPlayerGameState<Move> =
        copy(board = board.clone())
    
    override fun teamStats(team: ITeam): List<Stat> =
        listOf(
            Stat("Fische", board.fieldsForTeam(team).size),
            Stat("Schwarmgröße", GameRuleLogic.greatestSwarmSize(board, team))
        )
    
}