package sc.plugin2019

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.Direction
import sc.api.plugins.ITeam
import sc.api.plugins.Stat
import sc.api.plugins.Team
import sc.api.plugins.TwoPlayerGameState
import sc.plugin2026.Board
import sc.plugin2026.FieldState
import sc.plugin2026.Move
import sc.plugin2026.PiranhaMoveMistake
import sc.plugin2026.util.GameRuleLogic
import sc.plugin2026.util.PiranhaConstants
import sc.shared.IMoveMistake
import sc.shared.InvalidMoveException
import sc.shared.MoveMistake
import sc.shared.WinCondition

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
        GameRuleLogic.greatestSwarmSize(board, team) // TODO important
    
    // TODO test if one player is surrounded he loses
    override val isOver: Boolean
        get() = players.any { it.inGoal } && turn.mod(2) == 0 || turn / 2 >= PiranhaConstants.ROUND_LIMIT
    
    override val winCondition: WinCondition?
        get() = TODO("Not yet implemented")
    
    override fun performMoveDirectly(move: Move) {
        if (board.getTeam(move.from) != currentTeam) {
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
    
    override fun teamStats(team: ITeam): List<Stat> = listOf() // TODO
    
    
}