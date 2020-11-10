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
            validateMoveColor(gameState, move, true)

        when (move) {
            is SkipMove -> performSkipMove(gameState)
            is SetMove -> performSetMove(gameState, move)
        }
        gameState.lastMove = move
    }
    
    /** Prüfe, ob die Farbe des gegebenen [Move]s der aktiven Farbe des [GameState]s entspricht. */
    @JvmStatic
    fun validateMoveColor(gameState: GameState, move: Move, throws: Boolean = false): MoveMistake? {
        if (move.color != gameState.currentColor)
            if (throws) {
                throw InvalidMoveException("Expected move from ${gameState.currentColor}", move)
            } else {
                return MoveMistake.WRONG_COLOR
            }
        return null
    }
    
    /**
     * Prüfe, ob der gegebene [SetMove] gesetzt werden könnte.
     * @param throws wenn true, wirft die Methode bei invaliden Zügen einen Fehler.
     *
     * @return gibt einen [MoveMistake] zurück, wenn der Zug nicht valide wahr, ansonsten null
     */
    @JvmStatic
    fun validateSetMove(gameState: GameState, move: SetMove, throws: Boolean = false): MoveMistake? {
        // Check whether the color's move is currently active
        return validateMoveColor(gameState, move, throws) ?:
        // Check whether the shape is valid
        validateShape(gameState, move.piece.kind, move.color, throws) ?:
        // Check whether the piece can be placed
        validateSetMove(gameState.board, move, throws) ?:
        
        if (isFirstMove(gameState)) {
            // Check if it is placed correctly in a corner
            if (move.piece.coordinates.none { isOnCorner(it)})
                if (throws) {
                    throw InvalidMoveException("The Piece isn't located in a corner", move)
                } else {
                    MoveMistake.NOT_IN_CORNER
                }
            else null
        } else {
            // Check if the piece is connected to at least one tile of same color by corner
            if (move.piece.coordinates.none { cornersOnColor(gameState.board, Field(it, move.color)) })
                if (throws) {
                    throw InvalidMoveException("${move.piece} shares no corner with another piece of same color", move)
                } else {
                    MoveMistake.NO_SHARED_CORNER
                }
            else null
        }
    }

    /** Perform the given [SetMove]. */
    @JvmStatic
    private fun performSetMove(gameState: GameState, move: SetMove) {
        validateSetMove(gameState, move)

        if (Constants.VALIDATE_MOVE)
            validateSetMove(gameState, move, true)

        performSetMove(gameState.board, move)
        gameState.undeployedPieceShapes(move.color).remove(move.piece.kind)

        // If it was the last piece for this color, remove it from the turn queue
        if (gameState.undeployedPieceShapes(move.color).isEmpty())
            gameState.lastMoveMono += move.color to (move.piece.kind == PieceShape.MONO)

        gameState.tryAdvance()
    }

    /**
     * Prüfe, ob der gegebene Spielstein auf dem Spielfeld platziert werden könnte.
     * Fehler treten auf, wenn
     * - im ersten Zug nicht der vorgegebene Stein
     * - in nachfolgenden Zügen bereits gesetzte Steine
     * gesetzt werden würde(n).
     */
    @JvmStatic
    fun validateShape(gameState: GameState, shape: PieceShape, color: Color = gameState.currentColor, throws: Boolean = false): MoveMistake? {
        if (isFirstMove(gameState)) {
            if (shape != gameState.startPiece)
                if (throws) {
                    throw InvalidMoveException("$shape is not the requested first shape, ${gameState.startPiece}")
                } else {
                    return MoveMistake.WRONG_SHAPE
                }
        } else {
            if (!gameState.undeployedPieceShapes(color).contains(shape))
                if (throws) {
                    throw InvalidMoveException("Piece $shape has already been placed before")
                } else {
                    return MoveMistake.DUPLICATE_SHAPE
                }
        }
        return null
    }

    /**
     * Prüfe, ob der gegebene [Move] zulässig ist.
     * @param gameState der aktuelle Spielstand
     * @param move der zu überprüfende Zug
     *
     * @return ob der Zug zulässig ist
     */
    @JvmStatic
    fun isValidSetMove(gameState: GameState, move: SetMove) =
            validateSetMove(gameState, move) == null

    /** Prüfe, ob der gegebene [SetMove] auf dem [Board] platziert werden kann. */
    @JvmStatic
    fun validateSetMove(board: Board, move: SetMove, throws: Boolean = false): MoveMistake? {
        move.piece.coordinates.forEach {
            try {
                board[it]
            } catch (e: ArrayIndexOutOfBoundsException) {
                if (throws) {
                    throw InvalidMoveException("Field $it is out of bounds", move)
                } else {
                    return MoveMistake.OUT_OF_BOUNDS
                }
            }
            // Checks if a part of the piece is obstructed
            if (board.isObstructed(it))
                if (throws) {
                    throw InvalidMoveException("Field $it already belongs to ${board[it].content}", move)
                } else {
                    return MoveMistake.OBSTRUCTED
                }
            // Checks if a part of the piece would border on another piece of same color
            if (bordersOnColor(board, Field(it, move.color)))
                if (throws) {
                    throw InvalidMoveException("Field $it already borders on ${move.color}", move)
                } else {
                    return MoveMistake.TOUCHES_SAME_COLOR
                }
        }
        return null
    }
    
    /** Place a Piece on the given [board] according to [move]. */
    @JvmStatic
    private fun performSetMove(board: Board, move: SetMove) {
        move.piece.coordinates.forEach {
            board[it] = +move.color
        }
    }

    @JvmStatic
    fun validateSkipMove(gameState: GameState, throws: Boolean = false): MoveMistake? {
        if (isFirstMove(gameState))
            if (throws) {
                throw InvalidMoveException("Can't Skip on first round", SkipMove(gameState.currentColor))
            } else {
                return MoveMistake.SKIP_FIRST_TURN
            }
        return null
    }

    /** Skip a turn. */
    @JvmStatic
    private fun performSkipMove(gameState: GameState) {
        validateSkipMove(gameState, true)
        if (!gameState.tryAdvance())
            logger.error("Couldn't proceed to next turn!")
    }

    /** Prüfe, ob das gegebene [Field] bereits an eins mit gleicher Farbe angrenzt. */
    @JvmStatic
    fun bordersOnColor(board: Board, field: Field): Boolean =
            field.coordinates.neighbors.any {
                try { board[it].content == field.content && !field.isEmpty }
                catch (e: ArrayIndexOutOfBoundsException) { false }
            }
    
    /** Prüfe, ob das gegebene Feld an die Ecke eines Feldes gleicher Farbe angrenzt. */
    @JvmStatic
    fun cornersOnColor(board: Board, field: Field): Boolean =
            field.coordinates.corners.any {
                try { board[it].content == field.content && !field.isEmpty }
                catch (e: ArrayIndexOutOfBoundsException) { false }
            }
    
    /** Prüfe, ob die gegebene Position eine Ecke des Spielfelds ist. */
    @JvmStatic
    fun isOnCorner(position: Coordinates): Boolean =
            Corner.values().any { it.position == position }
    
    /** Gib zurück, ob sich der [GameState] noch in der ersten Runde befindet. */
    @JvmStatic
    fun isFirstMove(gameState: GameState) =
            gameState.undeployedPieceShapes().size == Constants.TOTAL_PIECE_SHAPES
    
    /** Return a random pentomino which is not the `x` one (Used to get a valid starting piece). */
    @JvmStatic
    fun getRandomPentomino() =
            PieceShape.values()
                    .filter{ it.size == 5 && it != PieceShape.PENTO_X }
                    .random()
    
    /** Entferne alle Farben, die keine Steine mehr auf dem Feld platzieren können. */
    @JvmStatic
    fun removeInvalidColors(gameState: GameState) {
        if (gameState.orderedColors.isEmpty()) return
        if (streamPossibleMoves(gameState).none { isValidSetMove(gameState, it) }) {
            gameState.removeActiveColor()
            removeInvalidColors(gameState)
        }
    }

    /** Gib eine Sammlung an möglichen [SetMove]s zurück. */
    @JvmStatic
    fun getPossibleMoves(gameState: GameState) =
            streamPossibleMoves(gameState).toSet()

    /** Gib eine Sequenz an möglichen [SetMove]s zurück. */
    @JvmStatic
    fun streamPossibleMoves(gameState: GameState) =
            if (isFirstMove(gameState))
                streamPossibleStartMoves(gameState)
            else
                streamAllPossibleMoves(gameState)

    /** Stream all possible moves if it's the first turn of [gameState]. */
    @JvmStatic
    private fun streamPossibleStartMoves(gameState: GameState) = sequence<SetMove> {
        val kind = gameState.startPiece
        for (variant in kind.variants) {
            for (corner in Corner.values()) {
                yield(SetMove(Piece(gameState.currentColor, kind, variant.key, corner.align(variant.key.area))))
            }
        }
    }.filter { isValidSetMove(gameState, it) }

    @JvmStatic
    fun streamAllPossibleMoves(gameState: GameState) = sequence<SetMove> {
        val validFields: Set<Coordinates> = getValidFields(gameState.board, gameState.currentColor)

        for (shape in gameState.undeployedPieceShapes())
            yieldAll(streamPossibleMovesForShape(gameState, shape, validFields))
    }

    /** Gib eine Sammlung aller möglichen [SetMove]s für die gegebene [PieceShape] zurück. */
    @JvmStatic
    fun getPossibleMovesForShape(gameState: GameState, shape: PieceShape): Set<SetMove> =
            streamPossibleMovesForShape(gameState, shape).toSet()

    /** Gib eine Sequenz aller möglichen [SetMove]s für die gegebene [PieceShape] zurück. */
    @JvmStatic
    fun streamPossibleMovesForShape(
            gameState: GameState,
            shape: PieceShape,
            validFields: Set<Coordinates> = getValidFields(gameState.board, gameState.currentColor)
    ) = if (isFirstMove(gameState)) streamPossibleStartMoves(gameState) else sequence<SetMove> {
        for (field in validFields) {
            for (variant in shape.variants) {
                val area = variant.key.area
                for (x in field.x - area.dx..field.x) {
                    for (y in field.y - area.dy..field.y) {
                        yield(SetMove(Piece(gameState.currentColor, shape, variant.value.first, variant.value.second, Coordinates(x, y))))
                    }
                }
            }
        }
    }.filter { isValidSetMove(gameState, it) }

    /** @return all [Coordinates] the currently active [Color] can place [Piece]s upon. */
    @JvmStatic
    private fun getValidFields(board: Board, color: Color): Set<Coordinates> {
        val coloredFields = getColoredFields(board, color, Corner.values().map { it.position }.filter {
            board[it].content == +color
        }.toMutableSet())

        val validFields = coloredFields.flatMap { it.corners }.filter {
            Board.contains(it) && board[it].isEmpty && it.neighbors.none {
                Board.contains(it) && board[it].content == +color
            }
        }.toSet()

        return validFields
    }

    /** @return all [Coordinates] belonging to the currently active [Color]. */
    @JvmStatic
    private fun getColoredFields(board: Board, color: Color, coloredFields: MutableSet<Coordinates>): Set<Coordinates> {
        val copy = coloredFields.toSet()
        coloredFields.addAll(coloredFields.flatMap { it.corners + it.neighbors }.filter {
            Board.contains(it) && board[it].content == +color
        })
        return if (coloredFields == copy) copy else getColoredFields(board, color, coloredFields)
    }
}
