package sc.plugin2020.util

import sc.plugin2020.*
import sc.shared.InvalidMoveException
import sc.shared.PlayerColor
import java.util.*
import kotlin.math.abs

object GameRuleLogic {
    
    /** @return all (up to 6) neighbouring fields of [coords]. */
    @JvmStatic
    fun getNeighbours(board: Board, coords: CubeCoordinates): List<Field> =
            Direction.values().mapNotNull {
                try {
                    getNeighbourInDirection(board, coords, it)
                } catch (ex: IndexOutOfBoundsException) {
                    null
                }
            }
    
    /** Gets the [Field] adjacent to [coords] in [direction].
     * @throws IndexOutOfBoundsException if there is no field in that direction. */
    @JvmStatic
    fun getNeighbourInDirection(board: Board, coords: CubeCoordinates, direction: Direction): Field {
        return board.getField(CubeCoordinates(coords.x + direction.shift(1).x, coords.y + direction.shift(1).y, coords.z + direction.shift(1).z))
    }
    
    /** @return the [PlayerColor] that has to make the next Move. */
    @JvmStatic
    fun getCurrentPlayerColor(gameState: GameState): PlayerColor = gameState.currentPlayerColor
    
    /** Validates & executes the [move] on the [gameState]. */
    @JvmStatic
    fun performMove(gameState: GameState, move: Move) {
        validateMove(gameState, move)
        // apply move
        when (move) {
            is SetMove -> {
                gameState.getUndeployedPieces(move.piece.owner).remove(move.piece)
                gameState.board.getField(move.destination).pieces.add(move.piece)
            }
            is DragMove -> {
                val board = gameState.board
                val pieceToMove = board.getField(move.start).pieces.pop()
                board.getField(move.destination).pieces.push(pieceToMove)
            }
        }
        gameState.turn++
        gameState.lastMove = move
    }
    
    /** Whether the Bee is completely blocked.
     * @return true iff [freeBeeNeighbours] returns 0 */
    @JvmStatic
    fun isBeeBlocked(board: Board, color: PlayerColor): Boolean =
            freeBeeNeighbours(board, color) == 0
    
    /** @return number of free (empty & not obstructed) fields around the Bee - -1 if no Bee has been placed. */
    @JvmStatic
    fun freeBeeNeighbours(board: Board, color: PlayerColor): Int =
            board.fields.find { it.pieces.contains(Piece(color, PieceType.BEE)) }
                    ?.let { getNeighbours(board, it) }?.count { field -> field.isEmpty }
                    ?: -1
    
    /** @return true iff the given [coords] are within the Board size. */
    @JvmStatic
    fun isOnBoard(coords: CubeCoordinates): Boolean {
        val shift = (Constants.BOARD_SIZE - 1) / 2
        return -shift <= coords.x && coords.x <= shift && -shift <= coords.y && coords.y <= shift
    }
    
    /** Checks if the given [move] is able to be performed on the given [gameState].
     * @throws InvalidMoveException if [move] is not possible */
    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateMove(gameState: GameState, move: Move) {
        move.destination?.let { destination ->
            if (!isOnBoard(destination))
                throw InvalidMoveException("Destination $destination is out of bounds")
            if (gameState.board.getField(destination).isObstructed)
                throw InvalidMoveException("The target field is obstructed")
        }
        when (move) {
            is SetMove -> validateSetMove(gameState, move)
            is DragMove -> validateDragMove(gameState, move)
            is SkipMove -> validateSkipMove(gameState)
        }
    }
    
    @Throws(InvalidMoveException::class)
    @JvmStatic
    private fun validateSkipMove(gameState: GameState) {
        if (this.getPossibleMoves(gameState).any { it !is SkipMove })
            throw InvalidMoveException("Skipping a turn is only allowed when no other moves can be made")
    }
    
    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateSetMove(gameState: GameState, move: SetMove) {
        if (!gameState.board.getField(move.destination).isEmpty)
            throw InvalidMoveException("Set destination is not empty")
        
        if (gameState.currentPlayerColor != move.piece.owner)
            throw InvalidMoveException("The piece ${move.piece} does not belong to the current Player(${gameState.currentPlayerColor})")
        
        val ownedFields = gameState.board.fields.filter { it.owner == gameState.currentPlayerColor }
        if (ownedFields.isEmpty()) {
            val otherPlayerFields = gameState.board.fields.filter { it.owner == gameState.otherPlayerColor }
            if (otherPlayerFields.isNotEmpty()) {
                if (move.destination !in otherPlayerFields.flatMap { getNeighbours(gameState.board, it.coordinates) })
                    throw InvalidMoveException("Your first piece has to touch the piece of the other player")
            }
        } else {
            if (!gameState.getUndeployedPieces(gameState.currentPlayerColor).contains(move.piece))
                throw InvalidMoveException("Piece ${move.piece} is not an undeployed piece of the current player")
            
            if (gameState.round >= 3 && !gameState.hasPlayerPlacedBee() && move.piece.type != PieceType.BEE)
                throw InvalidMoveException("The bee must be placed in fourth round latest")
            
            val destinationNeighbours = getNeighbours(gameState.board, move.destination)
            if (!destinationNeighbours.any { it.owner == gameState.currentPlayerColor })
                throw InvalidMoveException("A newly placed piece must touch an own piece")
            
            if (destinationNeighbours.any { it.owner == gameState.otherPlayerColor })
                throw InvalidMoveException("A newly placed piece must not touch a piece of the other player")
        }
    }
    
    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateDragMove(gameState: GameState, move: DragMove) {
        if (!gameState.hasPlayerPlacedBee())
            throw InvalidMoveException("You have to place the Bee to be able to perform dragmoves")
        
        val availablePieces = gameState.board.getField(move.start).pieces
        
        if (availablePieces.size == 0)
            throw InvalidMoveException("There is no piece to move")
        
        val pieceToDrag = availablePieces.peek()
        
        if (pieceToDrag.owner !== gameState.currentPlayerColor)
            throw InvalidMoveException("Trying to move piece of the other player")
        
        if (move.start == move.destination)
            throw InvalidMoveException("Destination and start are equal")
        
        if (gameState.board.getField(move.destination).pieces.isNotEmpty() && pieceToDrag.type !== PieceType.BEETLE)
            throw InvalidMoveException("Only beetles are allowed to climb on other Pieces")
        
        val boardWithoutPiece = Board(gameState.board.fields.map {
            if (it == move.start) Field(it).apply { pieces.pop() } else it
        })
        if (!isSwarmConnected(boardWithoutPiece))
            throw InvalidMoveException("Moving piece would disconnect swarm")
        
        when (pieceToDrag.type) {
            PieceType.ANT -> validateAntMove(boardWithoutPiece, move)
            PieceType.BEE -> validateBeeMove(boardWithoutPiece, move)
            PieceType.BEETLE -> validateBeetleMove(boardWithoutPiece, move)
            PieceType.GRASSHOPPER -> validateGrasshopperMove(boardWithoutPiece, move)
            PieceType.SPIDER -> validateSpiderMove(boardWithoutPiece, move)
        }
    }
    
    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateAntMove(board: Board, move: DragMove) {
        val visitedFields = mutableListOf(move.start)
        var index = 0
        do {
            val currentField = visitedFields[index]
            val newFields = getAccessibleNeighboursExcept(board, currentField, move.start).filterNot { it in visitedFields }
            if (move.destination in newFields)
                return
            visitedFields.addAll(newFields)
        } while (++index < visitedFields.size)
        throw InvalidMoveException("No path found for Ant move")
    }
    
    @JvmStatic
    fun isSwarmConnected(board: Board): Boolean {
        val visitedFields = arrayListOf(board.fields.firstOrNull { it.pieces.isNotEmpty() } ?: return true)
        val totalPieces = board.getPieces().size
        var index = 0
        do {
            val currentField = visitedFields[index]
            val occupiedNeighbours = getNeighbours(board, currentField.coordinates)
                    .filterTo(ArrayList()) { it.pieces.isNotEmpty() }
            occupiedNeighbours.removeAll(visitedFields)
            visitedFields.addAll(occupiedNeighbours)
            if (visitedFields.sumBy { it.pieces.size } == totalPieces)
                return true
        } while (++index < visitedFields.size)
        return false
    }
    
    @JvmStatic
    fun getAccessibleNeighbours(board: Board, start: CubeCoordinates) =
            getNeighbours(board, start).filter { neighbour ->
                neighbour.isEmpty && canMoveBetween(board, start, neighbour)
            }
    
    @JvmStatic
    fun getAccessibleNeighboursExcept(board: Board, start: CubeCoordinates, except: CubeCoordinates) =
            getNeighbours(board, start).filter { neighbour ->
                neighbour.isEmpty && canMoveBetweenExcept(board, start, neighbour, except) && neighbour.coordinates != except
            }
    
    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateBeeMove(board: Board, move: DragMove) {
        validateDestinationNextToStart(move)
        if (!canMoveBetween(board, move.start, move.destination))
            throw InvalidMoveException("There is no path to your destination")
    }
    
    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateBeetleMove(board: Board, move: DragMove) {
        validateDestinationNextToStart(move)
        if ((sharedNeighboursOfTwoCoords(board, move.start, move.destination) + board.getField(move.destination) + board.getField(move.start)).all { it.pieces.isEmpty() })
            throw InvalidMoveException("Beetle has to move along swarm")
    }
    
    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateGrasshopperMove(board: Board, move: DragMove) {
        if (!twoFieldsOnOneStraight(move.start, move.destination)) {
            throw InvalidMoveException("Grasshopper can only move straight lines")
        }
        if (isNeighbour(move.start, move.destination)) {
            throw InvalidMoveException("Grasshopper has to jump over at least one piece")
        }
        if (getLineBetweenCoords(board, move.start, move.destination).any { it.isEmpty }) {
            throw InvalidMoveException("Grasshopper can only jump over occupied fields, not empty ones")
        }
    }
    
    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateSpiderMove(board: Board, move: DragMove) {
        val paths: Deque<Array<CubeCoordinates>> = ArrayDeque()
        paths.add(arrayOf(move.start))
        do {
            val currentPath = paths.removeFirst()
            val newFields = getAccessibleNeighboursExcept(board, currentPath.last(), move.start).filterNot { it in currentPath }
            if (currentPath.size < 3)
                paths.addAll(newFields.map { currentPath + it })
            else if (move.destination in newFields)
                return
        } while (paths.isNotEmpty())
        throw InvalidMoveException("No path found for Spider move")
    }
    
    @Throws(IndexOutOfBoundsException::class)
    @JvmStatic
    fun getLineBetweenCoords(board: Board, start: CubeCoordinates, destination: CubeCoordinates): List<Field> {
        if (!twoFieldsOnOneStraight(start, destination)) {
            throw IndexOutOfBoundsException("destination is not in line with start")
        }
        
        val dX = start.x - destination.x
        val dY = start.y - destination.y
        val dZ = start.z - destination.z
        val d = if (dX == 0) abs(dY) else abs(dX)
        
        return (1 until d).map { i ->
            board.getField(CubeCoordinates(
                    destination.x + i * if (dX > 0) 1 else if (dX < 0) -1 else 0,
                    destination.y + i * if (dY > 0) 1 else if (dY < 0) -1 else 0,
                    destination.z + i * if (dZ > 0) 1 else if (dZ < 0) -1 else 0
            ))
        }
    }
    
    @JvmStatic
    fun canMoveBetween(board: Board, coords1: CubeCoordinates, coords2: CubeCoordinates): Boolean =
            sharedNeighboursOfTwoCoords(board, coords1, coords2).let { shared ->
                (shared.size == 1 || shared.any { it.isEmpty && !it.isObstructed }) && shared.any { it.pieces.isNotEmpty() }
            }
    
    @JvmStatic
    fun canMoveBetweenExcept(board: Board, coords1: CubeCoordinates, coords2: CubeCoordinates, except: CubeCoordinates): Boolean =
            sharedNeighboursOfTwoCoords(board, coords1, coords2).filterNot { it.pieces.size == 1 && except == it.coordinates }.let { shared ->
                (shared.size == 1 || shared.any { it.isEmpty && !it.isObstructed }) && shared.any { it.pieces.isNotEmpty() }
            }
    
    /** Checks that start and destination of this [DragMove] are neighbours.
     * @throws InvalidMoveException if start and destination are not neighbours */
    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateDestinationNextToStart(move: DragMove) {
        if (!this.isNeighbour(move.start, move.destination))
            throw InvalidMoveException("Destination field is not next to start field")
    }
    
    @JvmStatic
    fun isNeighbour(start: CubeCoordinates, destination: CubeCoordinates): Boolean =
            start.distanceTo(destination) == 1
    
    /** @return true iff the two fields have an axis in common. */
    @JvmStatic
    fun twoFieldsOnOneStraight(coords1: CubeCoordinates, coords2: CubeCoordinates): Boolean =
            coords1.x == coords2.x || coords1.y == coords2.y || coords1.z == coords2.z
    
    /** @return the Fields (up to 2) that are neighbours of both fields. */
    @JvmStatic
    fun sharedNeighboursOfTwoCoords(board: Board, coords1: CubeCoordinates, coords2: CubeCoordinates): Collection<Field> =
            getNeighbours(board, coords1).intersect(getNeighbours(board, coords2))
    
    /** @return true iff the current Player has to place the Bee in this round. */
    @JvmStatic
    fun GameState.mustPlayerPlaceBee() =
            round >= 3 && !hasPlayerPlacedBee()
    
    /** @return true iff the current Player has his Bee deployed. */
    @JvmStatic
    fun GameState.hasPlayerPlacedBee() =
            getDeployedPieces(currentPlayerColor).any { it.type == PieceType.BEE }
    
    /** @return true iff there are no pieces on the Board. */
    @JvmStatic
    fun boardIsEmpty(board: Board): Boolean =
            board.getPieces().isEmpty()
    
    @JvmStatic
    fun getPossibleMoves(gameState: GameState): List<Move> =
            (this.getPossibleSetMoves(gameState) + this.getPossibleDragMoves(gameState)).ifEmpty {
                if (gameState.mustPlayerPlaceBee())
                    emptyList()
                else
                    listOf(SkipMove)
            }
    
    @JvmStatic
    fun getPossibleDragMoves(gameState: GameState): List<DragMove> {
        if(gameState.mustPlayerPlaceBee())
            return emptyList()
        return gameState.board.getFieldsOwnedBy(gameState.currentPlayerColor).flatMap { startField ->
            val edgeTargets: Set<CubeCoordinates> = this.getEmptyFieldsConnectedToSwarm(gameState.board)
            val additionalTargets: Set<CubeCoordinates> =
                    if (startField.topPiece?.type == PieceType.BEETLE) {
                        this.getNeighbours(gameState.board, startField).toSet()
                    } else {
                        emptySet()
                    }
            (edgeTargets + additionalTargets).mapNotNull { destination: CubeCoordinates ->
                val move = DragMove(startField, destination)
                try {
                    this.validateMove(gameState, move)
                    move
                } catch (e: InvalidMoveException) {
                    null
                }
            }
        }
    }
    
    @JvmStatic
    fun getEmptyFieldsConnectedToSwarm(board: Board): Set<CubeCoordinates> =
            board.fields.filter { it.hasOwner }
                    .flatMap { this.getNeighbours(board, it).filter { it.isEmpty } }
                    .toSet()
    
    @JvmStatic
    fun getPossibleSetMoveDestinations(board: Board, owner: PlayerColor): Collection<CubeCoordinates> =
            board.getFieldsOwnedBy(owner)
                    .asSequence()
                    .flatMap { this.getNeighbours(board, it).asSequence() }
                    .filter { it.isEmpty }
                    .filter { this.getNeighbours(board, it).all { it.owner != owner.opponent() } }
                    .toSet()
    
    @JvmStatic
    fun getPossibleSetMoves(gameState: GameState): List<SetMove> {
        val undeployed = gameState.getUndeployedPieces(gameState.currentPlayerColor)
        val setDestinations =
                if (undeployed.size == Constants.STARTING_PIECES.length) {
                    // current player has not placed any pieces yet (first or second turn)
                    if (gameState.getUndeployedPieces(gameState.otherPlayerColor).size == Constants.STARTING_PIECES.length) {
                        gameState.board.fields.filter { it.isEmpty }
                    } else {
                        gameState.board.getFieldsOwnedBy(gameState.otherPlayerColor)
                                .flatMap { getNeighbours(gameState.board, it).filter { it.isEmpty } }
                    }
                } else {
                    this.getPossibleSetMoveDestinations(gameState.board, gameState.currentPlayerColor)
                }
        
        val possiblePieceTypes = if (gameState.mustPlayerPlaceBee()) listOf(PieceType.BEE) else undeployed.map { it.type }.toSet()
        return setDestinations
                .flatMap {
                    possiblePieceTypes.map { u ->
                        SetMove(Piece(gameState.currentPlayerColor, u), it)
                    }
                }
    }
}

