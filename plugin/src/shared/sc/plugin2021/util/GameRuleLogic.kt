package sc.plugin2021.util

import com.sun.org.apache.xpath.internal.operations.Bool
import sc.plugin2021.*
import sc.shared.InvalidMoveException

object GameRuleLogic {
    // TODO: Add all the needed logic as static (@JvmStatic) functions here
    @JvmStatic
    fun getPointsFromDeployedPieces(deployed: List<Piece>): Int {
        // TODO: Look up correct calculation of points
        return 1;
    }
    
    /** Performs the given [move] on the [gameState] if possible. */
    @JvmStatic
    fun performMove(gameState: GameState, move: Move) {
        validateMove(gameState, move)

        //TODO: Perform the move
    }

    /** Checks if the given [move] is able to be performed for the given [gameState]. */
    @JvmStatic
    fun validateMove(gameState: GameState, move: Move) {
        // Check if colors match
        if (move.piece.color != gameState.currentColor)
            throw InvalidMoveException("The given Piece isn't from the active color: $move")
        
        move.piece.coordinates.forEach {
            try { gameState.board[it] }
            catch (e: ArrayIndexOutOfBoundsException) {
                throw InvalidMoveException("Field $it is out of bounds", move)
            }
            // Checks if a part of the piece is obstructed
            if (isObstructed(gameState.board, it))
                throw InvalidMoveException("Field $it already belongs to ${gameState.board[it].color}", move)
            // Checks if a part of the piece would border on another piece of same color
            if (bordersOnColor(gameState.board, it, move.piece.color))
                throw InvalidMoveException("Field $it already borders on ${move.piece.color}", move)
        }
        // Checks if the piece is connected to at least one tile of same color by corner
        if (move.piece.coordinates.none { cornersOnColor(gameState.board, it, move.piece.color) })
            throw InvalidMoveException("${move.piece} shares no corner with another piece of same color", move)
    }
    
    /** Checks if the given [position] is already obstructed by another piece. */
    @JvmStatic
    fun isObstructed(board: Board, position: Coordinates): Boolean =
            board[position].color != Color.NONE
    
    /** Checks if the given [position] already borders on another piece of same [color]. */
    @JvmStatic
    fun bordersOnColor(board: Board, position: Coordinates, color: Color): Boolean =
            listOf( Vector(1, 0),
                    Vector(0, 1),
                    Vector(-1, 0),
                    Vector(0, -1)).any {
                try {
                    board[position + it].color == color
                } catch (e: ArrayIndexOutOfBoundsException) {
                    false
                }
            }
    
    @JvmStatic
    fun cornersOnColor(board: Board, position: Coordinates, color: Color): Boolean =
            listOf( Vector(1, 1),
                    Vector(1, -1),
                    Vector(-1, -1),
                    Vector(-1, 1)).any {
                try {
                    board[position + it].color == color
                } catch (e: ArrayIndexOutOfBoundsException) {
                    false
                }
            }
}