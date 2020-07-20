package sc.plugin2021.util

import sc.plugin2021.Color
import sc.plugin2021.GameState
import sc.plugin2021.Move
import sc.plugin2021.Piece
import sc.shared.InvalidMoveException

object GameRuleLogic {
    // TODO: Add all the needed logic as static (@JvmStatic) functions here
    @JvmStatic
    fun getPointsFromDeployedPieces(deployed: List<Piece>): Int {
        // TODO: Look up correct calculation of points
        return 1;
    }
    
    @JvmStatic
    fun performMove(gameState: GameState, move: Move) {
        validateMove(gameState, move)

        //TODO: Perform the move
    }

    @JvmStatic
    fun validateMove(gameState: GameState, move: Move) {
        // Check if colors match
        if (move.piece.color != gameState.currentColor)
            throw InvalidMoveException("The given Piece isn't from the active color: $move")
    }
}