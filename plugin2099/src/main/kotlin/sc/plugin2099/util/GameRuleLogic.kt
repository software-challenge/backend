package sc.plugin2099.util

import sc.api.plugins.Coordinates
import sc.api.plugins.Team
import sc.plugin2099.Board
import sc.plugin2099.FieldState
import sc.plugin2099.Move
import sc.shared.IMoveMistake
import sc.shared.MoveMistake

object GameRuleLogic {
    
    /** Prüft, ob ein Zug gültig ist.
     * @team null wenn der Zug valide ist, sonst ein entsprechender [IMoveMistake]. */
    @JvmStatic
    fun checkMove(board: Board, move: Move): IMoveMistake? {
        val destination = board.getOrNull(move.field) ?: return MoveMistake.DESTINATION_OUT_OF_BOUNDS
        if (destination != FieldState.EMPTY) {return MoveMistake.DESTINATION_BLOCKED }
        return null
    }
    
    /** Valide Züge. */
    @JvmStatic
    fun possibleMoves(board: Board): Collection<Move> {
        val moves: MutableList<Move> = ArrayList()
        for (field in board.entries) {
            if (field.value == FieldState.EMPTY) {moves.add(Move(field.key))}
        }
        return moves
    }

    @JvmStatic
    fun checkWinner(board: Board): Team? {
        // Check rows and columns for a win
        for (i in 0 until 3) {
            if (board[Coordinates(i, 0)] != FieldState.EMPTY &&
                board[Coordinates(i, 0)] == board[Coordinates(i, 1)] &&
                board[Coordinates(i, 1)] == board[Coordinates(i, 2)]) {
                return board[Coordinates(i, 0)].team  // Return the winning team
            }

            if (board[Coordinates(0, i)] != FieldState.EMPTY &&
                board[Coordinates(0, i)] == board[Coordinates(1, i)] &&
                board[Coordinates(1, i)] == board[Coordinates(2, i)]) {
                return board[Coordinates(0, i)].team  // Return the winning team
            }
        }

        if (board[Coordinates(1, 1)] != FieldState.EMPTY) {
            if (board[Coordinates(0, 0)] == board[Coordinates(1, 1)] && board[Coordinates(1, 1)] == board[Coordinates(2, 2)]) {
                return board[Coordinates(1, 1)].team  // Return the winning team
            }
            if (board[Coordinates(0, 2)] == board[Coordinates(1, 1)] && board[Coordinates(1, 1)] == board[Coordinates(2, 0)]) {
                return board[Coordinates(1, 1)].team  // Return the winning team
            }
        }

        return null
    }
}