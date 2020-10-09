package sc.plugin2021.util

import org.slf4j.LoggerFactory
import sc.plugin2021.*
import sc.shared.InvalidMoveException

/**
 * Eine Sammlung an Funktionen, die die Spielregeln logisch umsetzen.
 * Sie beinhalten primär Funktionen, um
 * * den Spielstand zu modifizieren
 * * mögliche Züge zu berechnen
 * * und die Punkte einer Farbe zu berechnen.
 */
object GameRuleLogic {
    val logger = LoggerFactory.getLogger(GameRuleLogic::class.java)
    
    const val SUM_MAX_SQUARES = 89
    
    /**
     * Berechne den Punktestand anhand der gegebenen [PieceShape]s.
     * @param undeployed eine Sammlung aller nicht gelegten [PieceShape]s
     * @param monoLast ob der letzte gelegte Stein das Monomino war
     *
     * @return die erreichte Punktezahl
     */
    @JvmStatic
    fun getPointsFromUndeployed(undeployed: Set<PieceShape>, monoLast: Boolean = false): Int {
        // If all pieces were placed:
        if (undeployed.isEmpty()) {
            // Return sum of all squares plus 15 bonus points
            return SUM_MAX_SQUARES + 15 +
            // If the Monomino was the last placed piece, add another 5 points
            if (monoLast) 5 else 0
        }
        // One point per block per piece placed
        return SUM_MAX_SQUARES - undeployed.map{ it.coordinates.size }.sum()
    }
    
    /**
     * Führe den gegebenen [Move] im gebenenen [GameState] aus.
     * @param gameState der aktuelle Spielstand
     * @param move der auszuführende Zug
     */
    @JvmStatic
    fun performMove(gameState: GameState, move: Move) {
        if (Constants.VALIDATE_MOVE)
            validateMoveColor(gameState, move)

        when (move) {
            is SkipMove -> performSkipMove(gameState)
            is SetMove -> performSetMove(gameState, move)
        }
        gameState.lastMove = move
    }
    
    /** Check if the given [move] has the right [Color]. */
    @JvmStatic
    private fun validateMoveColor(gameState: GameState, move: Move) {
        if (move.color != gameState.currentColor)
            throw InvalidMoveException("Expected move from ${gameState.currentColor}", move)
    }
    
    /** Check if the given [move] is able to be performed for the given [gameState]. */
    @JvmStatic
    private fun validateSetMove(gameState: GameState, move: SetMove) {
        // Check whether the color's move is currently active
        validateMoveColor(gameState, move)
        // Check whether the shape is valid
        validateShape(gameState, move.piece.kind, move.color)
        // Check whether the piece can be placed
        validateSetMove(gameState.board, move)
        
        if (isFirstMove(gameState)) {
            // Check if it is placed correctly in a corner
            if (move.piece.coordinates.none { isOnCorner(it)})
                throw InvalidMoveException("The Piece isn't located in a corner", move)
        } else {
            // Check if the piece is connected to at least one tile of same color by corner
            if (move.piece.coordinates.none { cornersOnColor(gameState.board, it, move.color) })
                throw InvalidMoveException("${move.piece} shares no corner with another piece of same color", move)
        }
    }

    /** Perform the given [SetMove]. */
    @JvmStatic
    private fun performSetMove(gameState: GameState, move: SetMove) {
        validateSetMove(gameState, move)

        if (Constants.VALIDATE_MOVE)
            validateSetMove(gameState, move)

        performSetMove(gameState.board, move)
        gameState.undeployedPieceShapes(move.color).remove(move.piece.kind)
        gameState.deployedPieces.getValue(move.color).add(move.piece)

        // If it was the last piece for this color, remove it from the turn queue
        if (gameState.undeployedPieceShapes(move.color).isEmpty())
            gameState.lastMoveMono += move.color to (move.piece.kind == PieceShape.MONO)

        gameState.tryAdvance()
    }

    /** Validate the [PieceShape] of a [SetMove] depending on the current [GameState]. */
    @JvmStatic
    private fun validateShape(gameState: GameState, shape: PieceShape, color: Color = gameState.currentColor) {
        if (isFirstMove(gameState)) {
            if (shape != gameState.startPiece)
                throw InvalidMoveException("$shape is not the requested first shape, ${gameState.startPiece}")
        } else {
            if (!gameState.undeployedPieceShapes(color).contains(shape))
                throw InvalidMoveException("Piece $shape has already been placed before")
        }
    }

    /**
     * Prüft, ob der gegebene [Move] zulässig ist.
     * @param gameState der aktuelle Spielstand
     * @param move der zu überprüfende Zug
     *
     * @return ob der Zug zulässig ist
     */
    @JvmStatic
    fun isValidSetMove(gameState: GameState, move: SetMove) =
            try {
                validateSetMove(gameState, move)
                true
            } catch (e: InvalidMoveException) {
                false
            }

    /** Validate a [SetMove] on a [board]. */
    @JvmStatic
    private fun validateSetMove(board: Board, move: SetMove) {
        move.piece.coordinates.forEach {
            try {
                board[it]
            } catch (e: ArrayIndexOutOfBoundsException) {
                throw InvalidMoveException("Field $it is out of bounds", move)
            }
            // Checks if a part of the piece is obstructed
            if (board.isObstructed(it))
                throw InvalidMoveException("Field $it already belongs to ${board[it].content}", move)
            // Checks if a part of the piece would border on another piece of same color
            if (bordersOnColor(board, it, move.color))
                throw InvalidMoveException("Field $it already borders on ${move.color}", move)
        }
    }
    
    /** Place a Piece on the given [board] according to [move]. */
    @JvmStatic
    private fun performSetMove(board: Board, move: SetMove) {
        move.piece.coordinates.forEach {
            board[it] = +move.color
        }
    }

    /** Skip a turn. */
    @JvmStatic
    private fun performSkipMove(gameState: GameState) {
        if (!gameState.tryAdvance())
            logger.error("Couldn't proceed to next turn!")
        if (isFirstMove(gameState))
            throw InvalidMoveException("Can't Skip on first round", SkipMove(gameState.currentColor))
    }

    /** Check if the given [position] already borders on another piece of same [color]. */
    @JvmStatic
    private fun bordersOnColor(board: Board, position: Coordinates, color: Color): Boolean = listOf(
            Vector(1, 0),
            Vector(0, 1),
            Vector(-1, 0),
            Vector(0, -1)).any {
        try {
            board[position + it].content == +color
        } catch (e: ArrayIndexOutOfBoundsException) { false }
    }
    
    /** Return true if the given [Coordinates] touch a corner of a field of same color. */
    @JvmStatic
    private fun cornersOnColor(board: Board, position: Coordinates, color: Color): Boolean = listOf(
            Vector(1, 1),
            Vector(1, -1),
            Vector(-1, -1),
            Vector(-1, 1)).any {
        try {
            board[position + it].content == +color
        } catch (e: ArrayIndexOutOfBoundsException) { false }
    }
    
    /** Return true if the given [Coordinates] are a corner. */
    @JvmStatic
    private fun isOnCorner(position: Coordinates): Boolean =
            Corner.values().any { it.position == position }
    
    /** Gib zurück, ob sich der [GameState] noch in der ersten Runde befindet. */
    @JvmStatic
    fun isFirstMove(gameState: GameState) =
            gameState.undeployedPieceShapes(gameState.currentColor).size == Constants.TOTAL_PIECE_SHAPES
    
    /** Return a random pentomino which is not the `x` one (Used to get a valid starting piece). */
    @JvmStatic
    fun getRandomPentomino() =
            PieceShape.values()
                    .filter{ it.size == 5 && it != PieceShape.PENTO_X }
                    .random()
    
    /** Gib eine Sammlung an möglichen [SetMove]s zurück. */
    @JvmStatic
    fun getPossibleMoves(gameState: GameState) =
            streamPossibleMoves(gameState).toSet()
    
    /** Return a list of all possible SetMoves, regardless of whether it's the first round. */
    @JvmStatic
    private fun getAllPossibleMoves(gameState: GameState) =
            streamAllPossibleMoves(gameState).toSet()
    
    /** Return a list of possible SetMoves if it's the first round. */
    @JvmStatic
    private fun getPossibleStartMoves(gameState: GameState) =
            streamPossibleStartMoves(gameState).toSet()
    
    /**
     * Return a list of all moves, impossible or not.
     *  There's no real usage, except maybe for cases where no Move validation happens
     *  if `Constants.VALIDATE_MOVE` is false, then this function should return the same
     *  Set as `::getPossibleMoves`
     */
    @JvmStatic
    private fun getAllMoves(): Set<SetMove> {
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
    
    /** Entferne alle Farben, die keine Steine mehr auf dem Feld platzieren können. */
    @JvmStatic
    fun removeInvalidColors(gameState: GameState) {
        if (gameState.orderedColors.isEmpty()) return
        if (streamPossibleMoves(gameState).none { isValidSetMove(gameState, it) }) {
            gameState.removeActiveColor()
            removeInvalidColors(gameState)
        }
    }
    
    /** Gib Eine Sequenz an möglichen [SetMove]s zurück. */
    @JvmStatic
    fun streamPossibleMoves(gameState: GameState) =
            if (isFirstMove(gameState))
                streamPossibleStartMoves(gameState)
            else
                streamAllPossibleMoves(gameState)
    
    /** Stream all possible moves regardless of whether it's the first turn. */
    @JvmStatic
    private fun streamAllPossibleMoves(gameState: GameState) = sequence<SetMove> {
        val color = gameState.currentColor
        gameState.undeployedPieceShapes(color).map {
            val area = it.coordinates.area()
            for (y in 0 until Constants.BOARD_SIZE - area.dy)
                for (x in 0 until Constants.BOARD_SIZE - area.dx)
                    for (variant in it.variants) {
                        yield(SetMove(Piece(color, it, variant.key, Coordinates(x, y))))
                    }
        }
    }.filter { isValidSetMove(gameState, it) }
    
    /** Stream all possible moves if it's the first turn of [gameState]. */
    @JvmStatic
    private fun streamPossibleStartMoves(gameState: GameState) = sequence<SetMove> {
        val kind = gameState.startPiece
        for (variant in kind.variants) {
            for (corner in Corner.values()) {
                yield(SetMove(Piece(gameState.currentColor, kind, variant.key, corner.align(variant.key.area()))))
            }
        }
    }.filter { isValidSetMove(gameState, it) }
}