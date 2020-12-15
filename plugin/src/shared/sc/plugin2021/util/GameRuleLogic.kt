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
    
    /** Prüfe, ob die Farbe des gegebenen [Move]s der aktiven Farbe des [GameState]s entspricht. */
    @JvmStatic
    fun validateMoveColor(gameState: GameState, move: Move) {
        if (move.color != gameState.currentColor)
            throw InvalidMoveException(MoveMistake.WRONG_COLOR, move)
    }
    
    /**
     * Prüfe, ob der gegebene [SetMove] gesetzt werden könnte.
     *
     * @throws InvalidMoveException wenn der Zug nicht valide war
     */
    @JvmStatic
    fun validateSetMove(gameState: GameState, move: SetMove) {
        // Check whether the color's move is currently active
        validateMoveColor(gameState, move)
        // Check whether the shape is valid
        validateShape(gameState, move.piece.kind, move.color)
        // Check whether the piece can be placed
        validateSetMove(gameState.board, move)
        
        if (isFirstMove(gameState)) {
            // Check if it is placed correctly in a corner
            if (move.piece.coordinates.none { isOnCorner(it)})
                throw InvalidMoveException(MoveMistake.NOT_IN_CORNER, move)
        } else {
            // Check if the piece is connected to at least one tile of same color by corner
            if (move.piece.coordinates.none { cornersOnColor(gameState.board, Field(it, move.color)) })
                throw InvalidMoveException(MoveMistake.NO_SHARED_CORNER, move)
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

        // If it was the last piece for this color, remove it from the turn queue
        if (gameState.undeployedPieceShapes(move.color).isEmpty())
            gameState.lastMoveMono += move.color to (move.piece.kind == PieceShape.MONO)

        do {
            gameState.tryAdvance()
        } while (!gameState.validColors.contains(gameState.currentColor))
    }

    /**
     * Prüfe, ob der gegebene Spielstein auf dem Spielfeld platziert werden könnte.
     * Fehler treten auf, wenn
     * - im ersten Zug nicht der vorgegebene Stein
     * - in nachfolgenden Zügen bereits gesetzte Steine
     * gesetzt werden würde(n).
     *
     * @throws InvalidMoveException wenn der Zug nicht valide war
     */
    @JvmStatic
    fun validateShape(gameState: GameState, shape: PieceShape, color: Color = gameState.currentColor) {
        if (isFirstMove(gameState)) {
            if (shape != gameState.startPiece)
                throw InvalidMoveException(MoveMistake.WRONG_SHAPE)
        } else {
            if (!gameState.undeployedPieceShapes(color).contains(shape))
                throw InvalidMoveException(MoveMistake.DUPLICATE_SHAPE)
        }
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
            try {
                validateSetMove(gameState, move)
                true
            } catch (e: InvalidMoveException) {
                false
            }

    /** Prüfe, ob der gegebene [SetMove] auf dem [Board] platziert werden kann.
    *
    * @throws InvalidMoveException wenn der Zug nicht valide war
    */
    @JvmStatic
    fun validateSetMove(board: Board, move: SetMove) {
        move.piece.coordinates.forEach {
            try {
                board[it]
            } catch (e: ArrayIndexOutOfBoundsException) {
                throw InvalidMoveException(MoveMistake.OUT_OF_BOUNDS, move)
            }
            // Checks if a part of the piece is obstructed
            if (board.isObstructed(it))
                throw InvalidMoveException(MoveMistake.OBSTRUCTED, move)
            // Checks if a part of the piece would border on another piece of same color
            if (bordersOnColor(board, Field(it, move.color)))
                throw InvalidMoveException(MoveMistake.TOUCHES_SAME_COLOR, move)
        }
    }
    
    /** Place a Piece on the given [board] according to [move]. */
    @JvmStatic
    private fun performSetMove(board: Board, move: SetMove) {
        move.piece.coordinates.forEach {
            board[it] = +move.color
        }
    }

    /**
     * Prüfe, ob die aktuelle Farbe den Zug überspringen kann.
     *
     * @throws InvalidMoveException wenn der Zug nicht valide war
     */
    @JvmStatic
    fun validateSkipMove(gameState: GameState) {
        if (isFirstMove(gameState))
            throw InvalidMoveException(MoveMistake.SKIP_FIRST_TURN, SkipMove(gameState.currentColor))
    }

    /** Skip a turn. */
    @JvmStatic
    private fun performSkipMove(gameState: GameState) {
        validateSkipMove(gameState)
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
        if (gameState.validColors.isEmpty()) return
        if (streamPossibleMoves(gameState).none { isValidSetMove(gameState, it) }) {
            gameState.remove()
            gameState.turn++
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
    ): Sequence<SetMove> {
        return if (isFirstMove(gameState)) {
            if (shape == gameState.startPiece)
                streamPossibleStartMoves(gameState)
            else
                sequenceOf()
        } else sequence<SetMove> {
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
    }

    /** @return alle [Coordinates], auf die die aktuelle [Color] Steine platzieren könnte. */
    @JvmStatic
    fun getValidFields(board: Board, color: Color): Set<Coordinates> =
            getColoredFields(board, color).flatMap { it.corners }.filter {
                Board.contains(it) && board[it].isEmpty && it.neighbors.none {
                    Board.contains(it) && board[it].content == +color
                }
            }.toSet()

    /** @return alle [Coordinates], deren Position auf dem [Board] die gegebene [Color] hat. */
    @JvmStatic
    fun getColoredFields(board: Board, color: Color): Set<Coordinates> =
            getColoredFieldsRecursively(board, color, Corner.values().map { it.position }.filter {
                board[it].content == +color
            }.toMutableSet())

    /** Recursive function filling [coloredFields] with all [Coordinates] on the [Board] with the given [Color]. */
    private fun getColoredFieldsRecursively(board: Board, color: Color, coloredFields: MutableSet<Coordinates>): Set<Coordinates> {
        val copy = coloredFields.toSet()
        coloredFields.addAll(coloredFields.flatMap { it.corners + it.neighbors }.filter {
            Board.contains(it) && board[it].content == +color
        })
        return if (coloredFields == copy) copy else getColoredFieldsRecursively(board, color, coloredFields)
    }
}
