package sc.plugin2020.util

import sc.api.plugins.IMove
import sc.plugin2020.*
import sc.shared.InvalidMoveException
import sc.shared.PlayerColor
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

object GameRuleLogic {
    
    @JvmStatic
    fun getNeighbours(board: Board, coords: CubeCoordinates): ArrayList<Field> {
        val tmp = ArrayList<Field>()
        
        for(d in Direction.values()) {
            val n: Field
            try {
                n = getNeighbourInDirection(board, coords, d)
            } catch(ex: IndexOutOfBoundsException) {
                continue
            }
            
            tmp.add(n)
        }
        
        return tmp
    }
    
    @JvmStatic
    fun getNeighbourInDirection(board: Board, coords: CubeCoordinates, direction: Direction): Field {
        return board.getField(CubeCoordinates(coords.x + direction.shift(1).x, coords.y + direction.shift(1).y, coords.z + direction.shift(1).z))
    }
    
    @JvmStatic
    fun getCurrentPlayerColor(gameState: GameState): PlayerColor {
        return if(gameState.turn % 2 == 0) PlayerColor.RED else PlayerColor.BLUE
    }
    
    @JvmStatic
    fun performMove(gameState: GameState, move: IMove) {
        validateMove(gameState, move)
        // apply move
        when(move) {
            is SetMove -> {
                gameState.getUndeployedPieces(move.piece.owner).remove(move.piece)
                gameState.board.getField(move.destination).pieces.add(move.piece)
            }
            is DragMove -> {
                val board = gameState.board
                val pieceToIMove = board.getField(move.start).pieces.pop()
                board.getField(move.destination).pieces.push(pieceToIMove)
            }
        }
        // change active player
        gameState.currentPlayerColor = gameState.otherPlayerColor
        gameState.turn++
    }
    
    @JvmStatic
    fun isQueenBlocked(board: Board, color: PlayerColor): Boolean {
        val l = board.fields.filter { it.pieces.contains(Piece(color, PieceType.BEE)) }
        if(l.isEmpty())
            return false
        return getNeighbours(board, l[0].coordinates).all { field -> field.isObstructed || !field.pieces.empty() }
    }
    
    @JvmStatic
    fun isOnBoard(coords: CubeCoordinates): Boolean {
        val shift = (Constants.BOARD_SIZE - 1) / 2
        return -shift <= coords.x && coords.x <= shift && -shift <= coords.y && coords.y <= shift
    }
    
    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateMove(gameState: GameState, move: IMove): Boolean {
        when(move) {
            is SetMove -> validateSetMove(gameState, move)
            is DragMove -> validateDragMove(gameState, move)
        }
        return true
    }
    
    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateSetMove(gameState: GameState, move: SetMove): Boolean {
        if(!isOnBoard(move.destination))
            throw InvalidMoveException("Piece has to be placed on board. Destination ${move.destination} is out of bounds.")
        if(!gameState.board.getField(move.destination).isEmpty)
            throw InvalidMoveException("Set destination is not empty!")
        
        val ownedFields = gameState.board.fields.filter { it.owner == gameState.currentPlayerColor }
        if(ownedFields.isEmpty()) {
            val otherPlayerFields = gameState.board.fields.filter { it.owner == gameState.otherPlayerColor }
            if(otherPlayerFields.isNotEmpty()) {
                if(move.destination !in otherPlayerFields.flatMap { getNeighbours(gameState.board, it.coordinates) })
                    throw InvalidMoveException("Piece has to be placed next to other players piece")
            }
        } else {
            if(!hasPlayerPlacedBee(gameState) && gameState.getDeployedPieces(gameState.currentPlayerColor).size == 3)
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
        
        val boardWithoutPiece = Board(gameState.board.fields.map {
            if(it == move.start) Field(it).apply { pieces.pop() } else it
        })
        if(!isSwarmConnected(boardWithoutPiece))
            throw InvalidMoveException("Moving piece would disconnect swarm")
        
        when(pieceToDrag.type) {
            PieceType.ANT -> validateAntMove(boardWithoutPiece, move)
            PieceType.BEE -> validateBeeMove(boardWithoutPiece, move)
            PieceType.BEETLE -> validateBeetleMove(boardWithoutPiece, move)
            PieceType.GRASSHOPPER -> validateGrasshopperMove(boardWithoutPiece, move)
            PieceType.SPIDER -> validateSpiderMove(boardWithoutPiece, move)
        }
    }
    
    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateAntMove(board: Board, move: DragMove): Boolean {
        val visitedFields: MutableList<CubeCoordinates> = arrayListOf(move.start)
        var index = 0
        do {
            val currentField = visitedFields[index]
            val newFields = getAccessibleNeighbours(board, currentField).filterNot { it in visitedFields }
            if(move.destination in newFields)
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
                neighbour.isEmpty && canMoveBetween(board, start, neighbour)
            }
    
    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateBeeMove(board: Board, move: DragMove) {
        validateDestinationNextToStart(move)
        if(!canMoveBetween(board, move.start, move.destination))
            throw InvalidMoveException("There is no path to your destination")
    }
    
    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateBeetleMove(board: Board, move: DragMove) {
        validateDestinationNextToStart(move)
        if(sharedNeighboursOfTwoCoords(board, move.start, move.destination).none { it.pieces.isNotEmpty() })
            throw InvalidMoveException("Beetle has to move along swarm")
    }
    
    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateGrasshopperMove(board: Board, move: DragMove) {
        if(!twoFieldsOnOneStraight(move.start, move.destination)) {
            throw InvalidMoveException("Grasshopper can only move straight lines")
        }
        if(isNeighbour(move.start, move.destination)) {
            throw InvalidMoveException("Grasshopper has to jump over at least one piece")
        }
        if(getLineBetweenCoords(board, move.start, move.destination).any { it.isEmpty }) {
            throw InvalidMoveException("Grasshopper can only jump over occupied fields, not empty ones")
        }
    }
    
    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateSpiderMove(board: Board, move: DragMove): Boolean {
        val paths: Deque<Array<CubeCoordinates>> = ArrayDeque()
        paths.add(arrayOf(move.start))
        do {
            val currentPath = paths.removeFirst()
            val newFields = getAccessibleNeighbours(board, currentPath.last()).filterNot { it in currentPath }
            if(currentPath.size < 3)
                paths.addAll(newFields.map { currentPath + it })
            else if(move.destination in newFields)
                return true
        } while(paths.isNotEmpty())
        throw InvalidMoveException("No path found for Spider move")
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
    
    @JvmStatic
    fun canMoveBetween(board: Board, coords1: CubeCoordinates, coords2: CubeCoordinates): Boolean =
            sharedNeighboursOfTwoCoords(board, coords1, coords2).let { shared ->
                (shared.size == 1 || shared.any { it.isEmpty }) && shared.any { it.pieces.isNotEmpty() }
            }
    
    
    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateDestinationNextToStart(move: DragMove) {
        if(!this.isNeighbour(move.start, move.destination))
            throw InvalidMoveException("Destination field is not next to start field")
    }
    
    @JvmStatic
    fun isNeighbour(start: CubeCoordinates, destination: CubeCoordinates): Boolean {
        return Direction.values().map {
            it.shift(start)
        }.contains(destination)
    }
    
    @JvmStatic
    fun twoFieldsOnOneStraight(coords1: CubeCoordinates, coords2: CubeCoordinates): Boolean {
        return coords1.x == coords2.x || coords1.y == coords2.y || coords1.z == coords2.z
    }
    
    @JvmStatic
    fun sharedNeighboursOfTwoCoords(board: Board, coords1: CubeCoordinates, coords2: CubeCoordinates): ArrayList<Field> {
        val neighbours = getNeighbours(board, coords1)
        neighbours.retainAll(getNeighbours(board, coords2))
        return neighbours
    }
    
    @JvmStatic
    fun hasPlayerPlacedBee(gameState: GameState) =
            gameState.getDeployedPieces(gameState.currentPlayerColor).any { it.type == PieceType.BEE }
    
    @JvmStatic
    fun boardIsEmpty(board: Board): Boolean =
            board.fields.none { it.pieces.isNotEmpty() }
    
    @JvmStatic
    fun getPossibleMoves(gameState: GameState): List<IMove> {
        
        //Gather all setMoves
        
        //if(boardIsEmpty(gameState.board))
        
        return listOf()
    }
}
