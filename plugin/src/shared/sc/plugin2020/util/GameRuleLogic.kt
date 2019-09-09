package sc.plugin2020.util

import sc.api.plugins.IMove
import sc.plugin2020.*
import sc.shared.InvalidMoveException
import sc.shared.PlayerColor
import kotlin.math.abs

object GameRuleLogic {

    @JvmStatic
    fun getNeighbours(b: Board, c: CubeCoordinates): ArrayList<Field> {
        val tmp = ArrayList<Field>()

        for(d in Direction.values()) {
            val n: Field
            try {
                n = getNeighbourInDirection(b, c, d)
            } catch(ex: IndexOutOfBoundsException) {
                continue
            }

            tmp.add(n)
        }

        return tmp
    }

    @JvmStatic
    fun getNeighbourInDirection(b: Board, c: CubeCoordinates, d: Direction): Field {
        return b.getField(CubeCoordinates(c.x + d.shift(1).x, c.y + d.shift(1).y, c.z + d.shift(1).z))
    }

    @JvmStatic
    fun getCurrentPlayerColor(gs: GameState): PlayerColor {
        return if(gs.turn % 2 == 0) PlayerColor.RED else PlayerColor.BLUE
    }

    @JvmStatic
    fun performMove(gs: GameState, m: IMove) {
        validateMove(gs, m)
        // apply move
        when(m) {
            is SetMove -> {
                gs.getUndeployedPieces(m.piece.owner).remove(m.piece)
                gs.board.getField(m.destination).pieces.add(m.piece)
            }
            is DragMove -> {
                val board = gs.board
                val pieceToIMove = board.getField(m.start).pieces.pop()
                board.getField(m.destination).pieces.push(pieceToIMove)
            }
        }
        // change active player
        gs.currentPlayerColor = gs.otherPlayerColor
        gs.turn++;
    }

    @JvmStatic
    fun isQueenBlocked(b: Board, pc: PlayerColor): Boolean {
        val l = b.fields.filter { it.pieces.contains(Piece(pc, PieceType.BEE)) }
        if(l.isEmpty())
            return false
        return getNeighbours(b, l[0].coordinates).all { field -> field.isObstructed || !field.pieces.empty() }
    }

    @JvmStatic
    fun isOnBoard(c: CubeCoordinates): Boolean {
        val shift = (Constants.BOARD_SIZE - 1) / 2
        return -shift <= c.x && c.x <= shift && -shift <= c.y && c.y <= shift
    }

    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateMove(gs: GameState, m: IMove): Boolean {
        when(m) {
            is SetMove -> validateSetMove(gs, m)
            is DragMove -> validateDragMove(gs, m)
        }
        return true
    }

    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateSetMove(gameState: GameState, move: SetMove): Boolean {
        val ownedFields = gameState.board.fields.filter { it.owner == gameState.currentPlayerColor }
        if(ownedFields.isEmpty()) {
            val otherPlayerFields = gameState.board.fields.filter { it.owner == gameState.otherPlayerColor }
            if(otherPlayerFields.isEmpty()) {
                if(!isOnBoard(move.destination)) {
                    throw InvalidMoveException(
                            String.format(
                                    "Piece has to be placed on board. Destination (%d,%d) is out of bounds.",
                                    move.destination.x, move.destination.y, move.destination.z
                            )
                    )
                }
            } else {
                if(move.destination !in otherPlayerFields.flatMap { getNeighbours(gameState.board, it.coordinates).filter { neighbour -> neighbour.isEmpty } }.map { it.coordinates })
                    throw InvalidMoveException("Piece has to be placed next to other players piece")
            }
        } else {
            if(hasPlayerPlacedBee(gameState) && gameState.getDeployedPieces(gameState.currentPlayerColor).size == 3)
                throw InvalidMoveException("The Bee must be placed at least as fourth piece")

            if(!gameState.getUndeployedPieces(gameState.currentPlayerColor).contains(move.piece))
                throw InvalidMoveException("Piece is not a undeployed piece of the current player")

            if(!getNeighbours(gameState.board, move.destination).any { it.owner == gameState.currentPlayerColor })
                throw InvalidMoveException("A newly placed piece must touch an own piece")
        }
        return true
    }

    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateDragMove(gameState: GameState, move: DragMove) {
        if(!hasPlayerPlacedBee(gameState))
            throw InvalidMoveException("You have to place the queen to be able to perform dragmoves")

        if(!isOnBoard(move.destination) || !isOnBoard(move.start))
            throw InvalidMoveException("The Move is out of bounds")

        if(gameState.board.getField(move.start).pieces.size == 0)
            throw InvalidMoveException("There is no piece to move")

        val pieceToDrag = gameState.board.getField(move.start).pieces.peek()

        if(pieceToDrag.owner !== gameState.currentPlayerColor)
            throw InvalidMoveException("Trying to move piece of the other player")

        if(move.start == move.destination)
            throw InvalidMoveException("Destination and start are equal")

        if(gameState.board.getField(move.destination).pieces.isNotEmpty() && pieceToDrag.type !== PieceType.BEETLE)
            throw InvalidMoveException("Only beetles are allowed to climb on other Pieces")

        if(!isSwarmConnected(Board(gameState.board.fields.map {
                    if(it == move.start) Field(it).apply { pieces.pop() } else it
                })))
            throw InvalidMoveException("Removing piece would disconnect swarm")

        when(pieceToDrag.type) {
            PieceType.ANT -> validateAntMove(gameState.board, move)
            PieceType.BEE -> validateBeeMove(gameState.board, move)
            PieceType.BEETLE -> validateBeetleMove(gameState.board, move)
            PieceType.GRASSHOPPER -> validateGrasshopperMove(gameState.board, move)
            PieceType.SPIDER -> validateSpiderMove(gameState.board, move)
        }
    }

    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateAntMove(board: Board, m: DragMove): Boolean {
        val visitedFields: MutableList<CubeCoordinates> = arrayListOf(m.start)
        var index = 0
        do {
            val currentField = visitedFields[index]
            val newFields = getAccessibleNeighbours(board, currentField).filterNot { it in visitedFields }
            if(m.destination in newFields)
                return true
            visitedFields.addAll(newFields)
        } while(++index < visitedFields.size)
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
            if(visitedFields.sumBy { it.pieces.size } == totalPieces)
                return true
        } while(++index < visitedFields.size)
        return false
    }

    @JvmStatic
    fun getAccessibleNeighbours(board: Board, start: CubeCoordinates) =
            getNeighbours(board, start).filter { neighbour ->
                neighbour.owner == null && sharedNeighboursOfTwoCoords(board, start, neighbour.coordinates).let { shared ->
                    shared.size == 2 && shared.any { it.isEmpty } && shared.any { it.owner != null }
                }
            }

    @JvmStatic
    fun getFieldsNextToSwarm(b: Board, exclude: CubeCoordinates): Collection<Field> =
            b.fields.filter { it.pieces.isNotEmpty() && it.coordinates != exclude }.flatMapTo(HashSet()) { getNeighbours(b, it.coordinates) }

    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateBeeMove(b: Board, m: DragMove) {
        validateDestinationNextToStart(m)
        if(!isPathToNextFieldClear(b, m.start, m.destination))
            throw InvalidMoveException("There is no path to your destination")
    }

    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateBeetleMove(b: Board, m: DragMove) {
        validateDestinationNextToStart(m)
    }

    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateDestinationNextToStart(m: DragMove) {
        if(!this.isNeighbour(m.start, m.destination))
            throw InvalidMoveException("Destination field is not next to start field")
    }

    @JvmStatic
    fun isNeighbour(start: CubeCoordinates, destination: CubeCoordinates): Boolean {
        return Direction.values().map {
            it.shift(start)
        }.contains(destination)
    }

    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateGrasshopperMove(b: Board, m: DragMove) {
        if(!twoFieldsOnOneStraight(m.start, m.destination)) {
            throw InvalidMoveException("Grasshopper can only move straight lines")
        }
        if(isNeighbour(m.start, m.destination)) {
            throw InvalidMoveException("Grasshopper has to jump over at least one piece")
        }
        if(getLineBetweenCoords(b, m.start, m.destination).any { it.isEmpty }) {
            throw InvalidMoveException("Grasshopper can only jump over occupied fields, not empty ones")
        }
    }

    @Throws(IndexOutOfBoundsException::class)
    @JvmStatic
    fun getLineBetweenCoords(board: Board, start: CubeCoordinates, destination: CubeCoordinates): List<Field> {
        if(!twoFieldsOnOneStraight(start, destination)) {
            throw IndexOutOfBoundsException("destination is not in line with start")
        }

        val dX = start.x - destination.x
        val dY = start.y - destination.y
        val dZ = start.z - destination.z
        val d = if(dX == 0) abs(dY) else abs(dX)

        return (1 until d).map { i ->
            board.getField(CubeCoordinates(
                    destination.x + i * if(dX > 0) 1 else if(dX < 0) -1 else 0,
                    destination.y + i * if(dY > 0) 1 else if(dY < 0) -1 else 0,
                    destination.z + i * if(dZ > 0) 1 else if(dZ < 0) -1 else 0
            ))
        }
    }

    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateSpiderMove(b: Board, m: DragMove) {
    }

    @JvmStatic
    fun isPathToNextFieldClear(b: Board, coord1: CubeCoordinates, coord2: CubeCoordinates): Boolean =
            sharedNeighboursOfTwoCoords(b, coord1, coord2).any { it.isEmpty }

    @JvmStatic
    fun twoFieldsOnOneStraight(coord1: CubeCoordinates, coord2: CubeCoordinates): Boolean {
        return coord1.x == coord2.x || coord1.y == coord2.y || coord1.z == coord2.z
    }

    @JvmStatic
    fun sharedNeighboursOfTwoCoords(b: Board, coord1: CubeCoordinates, coord2: CubeCoordinates): ArrayList<Field> {
        val neighbours = getNeighbours(b, coord1)
        neighbours.retainAll(getNeighbours(b, coord2))
        return neighbours
    }

    @JvmStatic
    fun hasPlayerPlacedBee(gs: GameState) =
            gs.getDeployedPieces(gs.currentPlayerColor).any { it.type == PieceType.BEE }

    @JvmStatic
    fun boardIsEmpty(b: Board): Boolean =
            b.fields.none { it.pieces.isNotEmpty() }

    @JvmStatic
    fun getPossibleMoves(gs: GameState): List<IMove> {

        //Gather all setMoves

        //if(boardIsEmpty(gs.board))

        return listOf()
    }
}
