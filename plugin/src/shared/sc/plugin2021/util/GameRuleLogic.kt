package sc.plugin2021.util

import org.slf4j.LoggerFactory
import sc.plugin2021.*
import sc.shared.InvalidMoveException
import kotlin.contracts.contract
import kotlin.properties.Delegates

object GameRuleLogic {
    val logger = LoggerFactory.getLogger(GameRuleLogic::class.java)
    
    const val SMALLEST_SCORE_POSSIBLE = -89
    
    // TODO: Add all the needed logic as static (@JvmStatic) functions here
    /** Calculates the score for the given list in pieces.
     *  Assumes the game has ended and the pieces are in order of placement.
     */
    @JvmStatic
    fun getPointsFromDeployedPieces(deployed: List<Piece>): Int {
        if (deployed.size == Constants.TOTAL_PIECE_SHAPES) {
            // Perfect score: 15 Points completion + 5 Points for solitary block last
            return if (deployed.last().kind == PieceShape.MONO) 20
            // Placed each piece: 15 Points completion bonus
            else 15
        }
        // One malus point per block per piece not placed
        return SMALLEST_SCORE_POSSIBLE + deployed.map { it.coordinates.size }.sum()
    }
    
    /** Performs the given [move] on the [gameState] if possible. */
    @JvmStatic
    fun performMove(gameState: GameState, move: Move) {
        if (Constants.VALIDATE_MOVE)
            validateMoveColor(gameState, move)
        
        when (move) {
            is PassMove -> {
                gameState.removeActiveColor()
            }
            is SetMove -> {
                if (Constants.VALIDATE_MOVE)
                    validateSetMove(gameState, move)
                
                move.piece.coordinates.forEach {
                    gameState.board[it] = +move.color
                }
                gameState.undeployedPieceShapes.getValue(move.color).remove(move.piece.kind)
                gameState.deployedPieces.getValue(move.color).add(move.piece)
                
                // If it was the last piece for this color, remove him from the turn queue
                if (gameState.undeployedPieceShapes.getValue(move.color).isEmpty())
                    gameState.removeActiveColor()
            }
        }
        if (gameState.orderedColors.isNotEmpty())
            gameState.turn++
        gameState.lastMove = move
    }
    
    /** Checks if the given [move] has the right [Color]. */
    @JvmStatic
    fun validateMoveColor(gameState: GameState, move: Move) {
        if (move.color != gameState.currentColor)
            throw InvalidMoveException("Expected move from ${gameState.currentColor}", move)
    }
    
    /** Checks if the given [move] is able to be performed for the given [gameState]. */
    @JvmStatic
    fun validateSetMove(gameState: GameState, move: SetMove) {
        // Check if piece has already been placed
        gameState.undeployedPieceShapes.getValue(move.color).find { it == move.piece.kind }
                ?: throw InvalidMoveException("Piece #${move.piece.kind} has already been placed before", move)
        
        move.piece.coordinates.forEach {
            try {
                gameState.board[it]
            } catch (e: ArrayIndexOutOfBoundsException) {
                throw InvalidMoveException("Field $it is out of bounds", move)
            }
            // Checks if a part of the piece is obstructed
            if (isObstructed(gameState.board, it))
                throw InvalidMoveException("Field $it already belongs to ${gameState.board[it].content}", move)
            // Checks if a part of the piece would border on another piece of same color
            if (bordersOnColor(gameState.board, it, move.color))
                throw InvalidMoveException("Field $it already borders on ${move.color}", move)
        }
        if (gameState.deployedPieces[move.color].isNullOrEmpty()) {
            // Check if it's the requested shape
            if (move.piece.kind != gameState.startPiece)
                throw InvalidMoveException("Expected the predetermined staring piece, ${gameState.startPiece}", move)
            // Check if it is placed correctly in a corner
            if (move.piece.coordinates.none { isOnCorner(it)})
                throw InvalidMoveException("The Piece isn't located in a corner", move)
        } else {
            // Check if the piece is connected to at least one tile of same color by corner
            if (move.piece.coordinates.none { cornersOnColor(gameState.board, it, move.color) })
                throw InvalidMoveException("${move.piece} shares no corner with another piece of same color", move)
        }
    }
    
    /** Checks if the given [position] is already obstructed by another piece. */
    @JvmStatic
    fun isObstructed(board: Board, position: Coordinates): Boolean =
            board[position].content != FieldContent.EMPTY
    
    /** Checks if the given [position] already borders on another piece of same [color]. */
    @JvmStatic
    fun bordersOnColor(board: Board, position: Coordinates, color: Color): Boolean = listOf(
            Vector(1, 0),
            Vector(0, 1),
            Vector(-1, 0),
            Vector(0, -1)).any {
        try {
            board[position + it].content == +color
        } catch (e: ArrayIndexOutOfBoundsException) { false }
    }
    
    @JvmStatic
    fun cornersOnColor(board: Board, position: Coordinates, color: Color): Boolean = listOf(
            Vector(1, 1),
            Vector(1, -1),
            Vector(-1, -1),
            Vector(-1, 1)).any {
        try {
            board[position + it].content == +color
        } catch (e: ArrayIndexOutOfBoundsException) { false }
    }
    
    @JvmStatic
    fun isOnCorner(position: Coordinates): Boolean = listOf(
            Coordinates(0, 0),
            Coordinates(Constants.BOARD_SIZE - 1, 0),
            Coordinates(Constants.BOARD_SIZE - 1, Constants.BOARD_SIZE - 1),
            Coordinates(0, Constants.BOARD_SIZE - 1)).contains(position)
    
    /** Returns a random pentomino which is not the `x` one (Used to get a valid starting piece). */
    @JvmStatic
    fun getRandomPentomino() =
            PieceShape.values()
                    .filter{ it.size == 5 && it != PieceShape.PENTO_X }
                    .random()
    
    /** Returns a list of all possible SetMoves. */
    @JvmStatic
    fun getPossibleMoves(gameState: GameState): Set<SetMove> {
//        if (gameState.round == 1) return getPossibleStartMoves(gameState)
//        val color = gameState.currentColor
//
//        return emptySet()
        
        return getAllMoves(gameState).filter {
            try {
                validateMoveColor(gameState, it)
                validateSetMove(gameState, it)
                true
            } catch (e: InvalidMoveException) {
                false
            }
        }.toSet()
    }
    
//    /** Returns a list of possible SetMoves if it's the first round. */
//    @JvmStatic
//    fun getPossibleStartMoves(gameState: GameState): Set<SetMove> {
//        val color = gameState.currentColor
//
//        return emptySet()
//    }
    
    /**
     * Returns a list of all moves, impossible or not.
     *  There's no real usage, except maybe for cases where no Move validation happens
     *  if `Constants.VALIDATE_MOVE` is false, then this function should return the same
     *  Set as `::getPossibleMoves`
     */
    @JvmStatic
    fun getAllMoves(gameState: GameState): Set<SetMove> {
        val moves = mutableSetOf<SetMove>()
        for (color in Color.values()) {
            for (shape in PieceShape.values()) {
                for (rotation in Rotation.values()) {
                    for (flip in listOf(false, true)) {
                        for (y in 0 until Constants.BOARD_SIZE) {
                            for (x in 0 until Constants.BOARD_SIZE) {
                                moves.add(SetMove(Piece(color, shape, rotation, flip, Coordinates(x, y))))
                            }
                        }
                    }
                }
            }
        }
        return moves
    }
}