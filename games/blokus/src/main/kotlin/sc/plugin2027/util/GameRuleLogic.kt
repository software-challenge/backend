package sc.plugin2027.util

import sc.api.plugins.Coordinates
import sc.plugin2027.*
import sc.shared.InvalidMoveException
import sc.shared.MoveMistake

/**
 * Eine Sammlung an Funktionen, die die Spielregeln logisch umsetzen.
 * Sie beinhalten primär Funktionen, um
 * * den Spielstand zu modifizieren
 * * mögliche Züge zu berechnen
 * * und die Punkte einer Farbe zu berechnen.
 */
object GameRuleLogic {
    
    /**
     * Summe aller Quadrate aller Polyminos einer Farbe.
     */
    const val SUM_MAX_SQUARES = 1 * 1 + 1 * 2 + 2 * 3 + 5 * 4 + 12 * 5 // = 89
    
    /**
     * Berechne den Punktestand anhand der gegebenen [PieceShape]s.
     * @param undeployed eine Sammlung aller nicht gelegten [PieceShape]s
     * @param monoLast ob der letzte gelegte Stein das Monomino war
     *
     * @return die erreichte Punktezahl
     */
    @JvmStatic
    fun getPointsFromUndeployed(undeployed: Collection<PieceShape>, monoLast: Boolean = false): Int {
        // If all pieces were placed:
        if (undeployed.isEmpty()) {
            // Return sum of all squares plus 15 bonus points
            return SUM_MAX_SQUARES + 15 +
                    // If the Monomino was the last placed piece, add another 5 points
                    if (monoLast) 5 else 0
        }
        // One point per block per piece placed
        return SUM_MAX_SQUARES - undeployed.sumOf { it.coordinates.size }
    }
    
    /**
     * Führe [move] in [gameState] aus.
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
        gameState.advance()
        gameState.lastMove = move
    }
    
    /**
     * Prüfe, ob die Farbe des gegebenen [Move]s der aktiven Farbe des [GameState]s entspricht.
     *
     * @param gameState der aktuelle Spielstand
     * @param move der zu überprüfende Zug
     * @throws InvalidMoveException wenn der Zug nicht valide war
     */
    @JvmStatic
    fun validateMoveColor(gameState: GameState, move: Move) {
        if (move.color != gameState.currentColor)
            throw InvalidMoveException(MoveMistake.WRONG_COLOR, move)
    }
    
    /**
     * Prüfe, ob der gegebene [SetMove] gesetzt werden könnte.
     *
     * @param gameState der aktuelle Spielstand
     * @param move der zu überprüfende Zug
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
            // Check if it is placed correctly at the border
            if (move.piece.coordinates.none { isOnBorder(it)})
                throw InvalidMoveException(BlokusMoveMistake.NOT_ON_BORDER, move)
        } else {
            // Check if the piece is connected to at least one tile of same color by corner
            if (move.piece.coordinates.none { cornersOnColor(gameState.board, Field(it, move.color)) })
                throw InvalidMoveException(BlokusMoveMistake.NO_SHARED_CORNER, move)
        }
    }
    
    /**
     * Perform the given [SetMove].
     * This is only used internally to execute a move.
     *
     * @param gameState the current game state
     * @param move the move to perform
     */
    @JvmStatic
    fun performSetMove(gameState: GameState, move: SetMove) {
        if (Constants.VALIDATE_MOVE)
            validateSetMove(gameState, move)
        
        performSetMove(gameState.board, move)
        gameState.removeUndeployedPiece(move.piece)
        
        // If it was the last piece for this color, remove it from the turn queue
        if (gameState.undeployedPieceShapes(move.color).isEmpty())
            gameState.lastMoveMono += move.color to (move.piece.kind == PieceShape.MONO)
    }
    
    /**
     * Prüfe, ob der gegebene Spielstein auf dem Spielfeld platziert werden könnte.
     * Fehler treten auf, wenn
     * - im ersten Zug nicht der vorgegebene Stein
     * - in nachfolgenden Zügen bereits gesetzte Steine
     * gesetzt werden würde(n).
     *
     * @param gameState der aktuelle Spielstand
     * @param shape die zu überprüfende Form
     * @param color die Farbe, für die die Form überprüft werden soll (optional, Standard ist die aktuelle Farbe)
     *
     * @throws InvalidMoveException wenn der Zug nicht valide war
     */
    @JvmStatic
    fun validateShape(gameState: GameState, shape: PieceShape, color: Color = gameState.currentColor) {
        if (isFirstMove(gameState)) {
            if (shape != gameState.startPiece)
                throw InvalidMoveException(BlokusMoveMistake.WRONG_SHAPE)
        } else {
            if (!gameState.undeployedPieceShapes(color).contains(shape))
                throw InvalidMoveException(BlokusMoveMistake.DUPLICATE_SHAPE)
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
    fun isValidSetMove(gameState: GameState, move: SetMove): Boolean {
        try {
            validateSetMove(gameState, move)
            return true
        } catch(e: InvalidMoveException) {
            return false
        }
    }
    
    /** Prüfe, ob der gegebene [SetMove] auf dem [Board] platziert werden kann.
     *
     * @param board das Spielfeld, auf dem der Zug ausgeführt werden soll
     * @param move der zu überprüfende Zug
     *
     * @throws InvalidMoveException wenn der Zug nicht valide war
     */
    @JvmStatic
    fun validateSetMove(board: Board, move: SetMove) {
        move.piece.coordinates.forEach {
            try {
                board[it]
            } catch (e: IllegalArgumentException) { // This is thrown by the Board when the coordinates are out of bounds
                throw InvalidMoveException(BlokusMoveMistake.OUT_OF_BOUNDS, move)
            }
            // Checks if a part of the piece is obstructed
            if (board.isObstructed(it))
                throw InvalidMoveException(BlokusMoveMistake.OBSTRUCTED, move)
            // Checks if a part of the piece would border on another piece of same color
            if (bordersOnColor(board, Field(it, move.color)))
                throw InvalidMoveException(BlokusMoveMistake.TOUCHES_SAME_COLOR, move)
        }
    }
    
    /**
     * Place a Piece on the given [board] according to [move].
     * Only used internally to perform a move.
     *
     * @param board the board on which the move should be performed
     * @param move the move to perform
     */
    @JvmStatic
    private fun performSetMove(board: Board, move: SetMove) {
        move.piece.coordinates.forEach {
            board[it].content = move.color.toFieldContent()
        }
    }
    
    /**
     * Prüfe, ob die aktuelle Farbe den Zug überspringen kann.
     *
     * @param gameState der aktuelle Spielstand
     *
     * @throws InvalidMoveException wenn der Zug nicht valide war
     */
    @JvmStatic
    fun validateSkipMove(gameState: GameState) {
        if (isFirstMove(gameState))
            throw InvalidMoveException(BlokusMoveMistake.SKIP_FIRST_TURN, SkipMove(gameState.currentColor))
    }
    
    /**
     * Skip a turn.
     * Only used internally to perform a [SkipMove].
     *
     * @param gameState the current game state
     */
    @JvmStatic
    fun performSkipMove(gameState: GameState) {
        validateSkipMove(gameState)
    }
    
    /**
     * Prüfe, ob das gegebene [Field] bereits an eins mit gleicher Farbe angrenzt.
     *
     * @param board das Spielfeld, auf dem der Zug ausgeführt werden soll
     * @param field das zu überprüfende Feld
     * @return true, falls das Feld an ein anderes Feld gleicher Farbe angrenzt
     */
    @JvmStatic
    fun bordersOnColor(board: Board, field: Field): Boolean =
        field.coordinates.neighbors.any {
            try { board[it].content == field.content && !field.isEmpty }
            catch (e: Exception) {
                // Kotlin has no multi-catch, so we catch all exceptions and check their type manually
                // FIXME check whether only IndexOutOfBounds can be thrown here.
                when(e) {
                    is IllegalArgumentException, is IndexOutOfBoundsException -> {
                        false
                    }
                    else -> throw e
                }
            }
        }
    
    /**
     * Prüfe, ob das gegebene Feld an die Ecke eines Feldes gleicher Farbe angrenzt.
     *
     * @param board das Spielfeld, auf dem der Zug ausgeführt werden soll
     * @param field das zu überprüfende Feld
     * @return true, falls das Feld an die Ecke eines anderen Feldes gleicher Farbe angrenzt
     */
    @JvmStatic
    fun cornersOnColor(board: Board, field: Field): Boolean =
        field.coordinates.diagonalNeighbors.any {
            try { board[it].content == field.content && !field.isEmpty }
            // If a corner field is not in the board, it cannot be the same color.
            // Note that the IBoard rethrows the ArrayIndexOutOfBoundsException as an IllegalArgumentException
            catch (e: IllegalArgumentException) { false }
        }
    
    /**
     * Prüfe, ob die gegebene Position am Rand des Spielfelds liegt.
     *
     * @param position die zu überprüfende Position
     * @return true, falls die Position am Rand des Spielfelds liegt
     */
    @JvmStatic
    fun isOnBorder(position: Coordinates): Boolean =
        position.x == 0 || position.x == Constants.BOARD_LENGTH - 1 ||
                position.y == 0 || position.y == Constants.BOARD_LENGTH - 1
    
    /**
     * Gib zurück, ob sich der [GameState] noch in der ersten Runde befindet.
     * FIXME why is this not done why the turn or round attributes?
     *
     * @param gameState der aktuelle Spielstand
     * @return true, wenn sich der [GameState] noch in der ersten Runde befindet, also alle Farben noch alle Steine haben
     */
    @JvmStatic
    fun isFirstMove(gameState: GameState) =
        gameState.undeployedPieceShapes().size == Constants.TOTAL_PIECE_SHAPES
    
    /**
     * Returns a random pentomino.
     * This is used to determine the starting piece for a game and is only used internally.
     * In contrast to the 2021 implementation, this can also return the PENTO_X.
     *
     * @return a random Pentomino. */
    @JvmStatic
    fun getRandomStartPentomino() =
        PieceShape.entries
            .filter{ it.size == 5 }
            .random()
    
    /**
     * Entferne alle Farben, die keine Steine mehr auf dem Feld platzieren können.
     * This is only used internally to update the game state after a move.
     *
     * @param gameState der aktuelle Spielstand
     */
    @JvmStatic
    fun removeInvalidColors(gameState: GameState) {
        if (!gameState.hasValidColors()) return
        if (getAllPossibleMoves(gameState).none { isValidSetMove(gameState, it) }) {
            gameState.removeActiveColor()
            removeInvalidColors(gameState)
        }
    }
    
    /**
     * Gib eine Liste an möglichen [SetMove]s zurück.
     * Diese Liste enthält auch mögliche Startzüge.
     *
     * @param gameState der aktuelle Spielstand
     * @return eine Liste aller möglichen [SetMove]s für den aktuellen Spielstand
     */
    @JvmStatic
    fun getAllPossibleMoves(gameState: GameState): List<SetMove> =
        if (isFirstMove(gameState)) {
            getPossibleStartMoves(gameState)
        } else {
            getPossibleMoves(gameState)
        }
    
    /**
     * Gibt alle möglichen [SetMove]s für den ersten Zug zurück.
     *
     * @param gameState der aktuelle Spielstand
     * @return eine Liste aller möglichen [SetMove]s für den ersten Zug
     */
    @JvmStatic
    fun getPossibleStartMoves(gameState: GameState): List<SetMove> {
        val moves = ArrayList<SetMove>()
        val filteredMoves = ArrayList<SetMove>()
        val kind = gameState.startPiece
        for (variant: Map.Entry<Set<Coordinates>, Pair<Rotation, Boolean>> in kind.variants) {
            val borderCoordinates: ArrayList<Coordinates> = ArrayList()
            // Add top border
            // x from [0,16], y = 0
            for (x in 0 until Constants.BOARD_LENGTH - variant.key.area.dx - 1) {
                borderCoordinates.add(Coordinates(x, 0))
            }
            // Add right border
            // x = 20-2-1 = 17, y from [0,16]
            for (y in 0 until Constants.BOARD_LENGTH - variant.key.area.dy - 1) {
                borderCoordinates.add(Coordinates(Constants.BOARD_LENGTH - variant.key.area.dx - 1, y))
            }
            // Add bottom border
            // x from [1, 17] = 17, y = 20 - 2 - 1 = 17
            for (x in 1 until Constants.BOARD_LENGTH - variant.key.area.dx) {
                borderCoordinates.add(Coordinates(x, Constants.BOARD_LENGTH - variant.key.area.dy - 1))
            }
            // Add left border
            // x = 0, y from [1, 17]
            for (y in 1 until Constants.BOARD_LENGTH - variant.key.area.dy) {
                borderCoordinates.add(Coordinates(0, y))
            }
            for (borderCoordinate in borderCoordinates) {
                val move = SetMove(Piece(gameState.currentColor, kind, variant.key, borderCoordinate))
                // Remove all moves that got turned or mirrored such that they are no longer valid, e.g. because they would be placed out of bounds.
                if (isValidSetMove(gameState, move)) {
                    moves.add(move)
                } else {
                    filteredMoves.add(move)
                }
            }
        }
        return moves
    }
    
    /**
     * Gib eine Sammlung aller möglichen [SetMove]s zurück (ohne den Startzug).
     *
     * @param gameState der aktuelle Spielstand
     * @return eine Sammlung aller möglichen [SetMove]s (ohne den Startzug)
     */
    @JvmStatic
    fun getPossibleMoves(gameState: GameState): List<SetMove> {
        val validFields: Set<Coordinates> = getValidFields(gameState.board, gameState.currentColor)
        val moves: ArrayList<SetMove> = ArrayList()
        for (shape in gameState.undeployedPieceShapes()) {
             moves.addAll(getPossibleMovesForShape(gameState, shape, validFields))
        }
        return moves
    }
    
    /**
     * Gib die Menge aller möglichen [SetMove]s für die gegebene [PieceShape] zurück.
     */
    @JvmStatic
    fun getPossibleMovesForShape(
        gameState: GameState,
        shape: PieceShape,
        validFields: Set<Coordinates> = getValidFields(gameState.board, gameState.currentColor),
    ): Set<SetMove> {
        val moves: MutableSet<SetMove> = mutableSetOf()
        if (isFirstMove(gameState)) {
            if(shape == gameState.startPiece) {
                return getPossibleStartMoves(gameState).toSet()
            } else {
                return emptySet()
            }
        } else {
            for (field in validFields) {
                for (variant in shape.variants) {
                    val area = variant.key.area
                    for (x in field.x - area.dx..field.x) {
                        for (y in field.y - area.dy..field.y) {
                            val move = SetMove(
                                Piece(
                                    gameState.currentColor,
                                    shape,
                                    variant.value.first,
                                    variant.value.second,
                                    Coordinates(x, y)
                                )
                            )
                            if (isValidSetMove(gameState, move)) {
                                moves.add(move)
                            }
                        }
                    }
                }
            }
        }
        
        return moves
    }
    
    /** @return alle [Coordinates], auf die die aktuelle [Color] Steine platzieren könnte. */
    @JvmStatic
    fun getValidFields(board: Board, color: Color): Set<Coordinates> =
        getColoredFields(board, color).flatMap { it.diagonalNeighbors }.filter { corner ->
            Board.contains(corner) && board[corner].isEmpty &&
                    corner.neighbors.none { neighbor -> Board.contains(neighbor) && board[neighbor].content == color.toFieldContent() }
        }.toSet()
    
    /** @return alle [Coordinates], deren Position auf dem [Board] die gegebene [Color] hat. */
    @JvmStatic
    fun getColoredFields(board: Board, color: Color): Set<Coordinates> {
        // Just check all fields in the board
        val coloredFields: MutableSet<Coordinates> = mutableSetOf()
        for (x in 0 until Constants.BOARD_LENGTH) {
            for (y in 0 until Constants.BOARD_LENGTH) {
                val pos = Coordinates(x, y)
                if (board[pos].content == color.toFieldContent())
                    coloredFields.add(pos)
            }
        }
        return coloredFields
    }
    
    
    /** Recursive function filling [coloredFields] with all [Coordinates] on the [Board] with the given [Color]. */
    private fun getColoredFieldsRecursively(board: Board, color: Color, coloredFields: MutableSet<Coordinates>): Set<Coordinates> {
        val copy = coloredFields.toSet()
        coloredFields.addAll(coloredFields.flatMap { it.diagonalNeighbors + it.neighbors }.filter {
            Board.contains(it) && board[it].content == color.toFieldContent()
        })
        return if (coloredFields == copy) copy else getColoredFieldsRecursively(board, color, coloredFields)
    }
}