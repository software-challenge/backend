package sc.plugin2099

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.ITeam
import sc.api.plugins.Stat
import sc.api.plugins.Team
import sc.api.plugins.TwoPlayerGameState
import sc.plugin2099.util.GameRuleLogic
import sc.plugin2099.util.TicTacToeConstants
import sc.plugin2099.util.TicTacToeWinReason
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
    /** Die Anzahl an bereits getätigten Zügen. */
    @XStreamAsAttribute override var turn: Int = 0,
    /** Der zuletzt gespielte Zug. */
    override var lastMove: Move? = null,
    /** Das aktuelle Spielfeld. */
    override val board: Board = Board(),
): TwoPlayerGameState<Move>(Team.ONE) {

    override fun getPointsForTeam(team: ITeam): IntArray =
        intArrayOf(0)

    override val isOver: Boolean
        get() = (GameRuleLogic.checkWinner(board) != null) ||
                turn >= TicTacToeConstants.TURN_LIMIT
    
    override val winCondition: WinCondition?
        get() =
            if(GameRuleLogic.checkWinner(board) != null || turn >= TicTacToeConstants.TURN_LIMIT) {
                GameRuleLogic.checkWinner(board)
                           ?.let { WinCondition(it, TicTacToeWinReason.FIRST_THREE_IN_A_LINE) }
                       ?: WinCondition(null, WinReasonTie)
            } else {
                null
            }
    
    override fun performMoveDirectly(move: Move) {
        GameRuleLogic.checkMove(board, move)?.let { throw InvalidMoveException(it, move) }
        board[move.field] = FieldState.fromTeam(currentTeam)
        turn++
        lastMove = move
    }
    
    override fun getSensibleMoves(): List<Move> {
        val piranhas = board.filterValues { field -> field.team == currentTeam }
        val moves = ArrayList<Move>(piranhas.size * 2)
        moves.addAll(GameRuleLogic.possibleMoves(board))
        return moves
    }
    
    override fun moveIterator(): Iterator<Move> =
        getSensibleMoves().iterator()
    
    override fun clone(): GameState =
        copy(board = board.clone())

    override fun teamStats(team: ITeam): List<Stat> =
        listOf(
        )
    
}