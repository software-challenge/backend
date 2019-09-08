package sc.plugin2020.util

import sc.api.plugins.IMove
import sc.plugin2020.*
import sc.shared.InvalidMoveException
import sc.shared.PlayerColor

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
    }

    @JvmStatic
    fun isQueenBlocked(b: Board, pc: PlayerColor): Boolean {
        val l = b.filterFields { it.pieces.contains(Piece(pc, PieceType.BEE)) }
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
    fun validateSetMove(gs: GameState, m: SetMove): Boolean {
        val ownedFields = gs.board.filterFields { it.owner == gs.currentPlayerColor }
        if(ownedFields.isEmpty()) {
            val otherPlayerFields = gs.board.filterFields { it.owner == gs.otherPlayerColor }
            if(otherPlayerFields.isEmpty()) {
                if(!isOnBoard(m.destination)) {
                    throw InvalidMoveException(
                            String.format(
                                    "Piece has to be placed on board. Destination (%d,%d) is out of bounds.",
                                    m.destination.x, m.destination.y, m.destination.z
                            )
                    )
                }
            } else {
                if(m.destination !in otherPlayerFields.flatMap { getNeighbours(gs.board, it.coordinates).filter { neighbour -> neighbour.isEmpty } }.map { it.coordinates })
                    throw InvalidMoveException("Piece has to be placed next to other players piece")
            }
        } else {
            if(hasPlayerPlacedBee(gs) && gs.getDeployedPieces(gs.currentPlayerColor).size == 3)
                throw InvalidMoveException("The Bee must be placed at least as fourth piece")

            if(!gs.getUndeployedPieces(gs.currentPlayerColor).contains(m.piece))
                throw InvalidMoveException("Piece is not a undeployed piece of the current player")

            if(!getNeighbours(gs.board, m.destination).any { it.owner == gs.currentPlayerColor })
                throw InvalidMoveException("A newly placed piece must touch an own piece")
        }
        return true
    }

    private fun validateDragMove(gs: GameState, m: DragMove) {
        // TODO: Check if swarm will be disconnected
        if(!hasPlayerPlacedBee(gs))
            throw InvalidMoveException("You have to place the queen to be able to perform dragmoves")

        if(!isOnBoard(m.destination) || !isOnBoard(m.start))
            throw InvalidMoveException("The Move is out of bounds")

        if(gs.board.getField(m.start).pieces.size == 0)
            throw InvalidMoveException("There is no piece to move")

        val pieceToDrag = gs.board.getField(m.start).pieces.peek()

        if(pieceToDrag.owner !== gs.currentPlayerColor)
            throw InvalidMoveException("Trying to move piece of the other player")

        if(m.start == m.destination)
            throw InvalidMoveException("Destination and start are equal")

        if(gs.board.getField(m.destination).pieces.isNotEmpty() && pieceToDrag.type !== PieceType.BEETLE)
            throw InvalidMoveException("Only beetles are allowed to climb on other Pieces")

        when(pieceToDrag.type) {
            PieceType.ANT -> validateAntMove(gs.board, m)
            PieceType.BEE -> validateBeeMove(gs.board, m)
            PieceType.BEETLE -> validateBeetleMove(gs.board, m)
            PieceType.GRASSHOPPER -> validateGrasshopperMove(gs.board, m)
            PieceType.SPIDER -> validateSpiderMove(gs.board, m)
        }
    }

    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateAntMove(b: Board, m: DragMove) {
    }

    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateBeeMove(b: Board, m: DragMove) {
        if(!isPathToNextFieldClear(b, m.start, m.destination))
            throw InvalidMoveException("There is no path to your destination")
    }

    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateBeetleMove(b: Board, m: DragMove) {
    }

    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateGrasshopperMove(b: Board, m: DragMove) {
    }

    @Throws(InvalidMoveException::class)
    @JvmStatic
    fun validateSpiderMove(b: Board, m: DragMove) {
    }

    @JvmStatic
    fun isPathToNextFieldClear(b: Board, coord1: CubeCoordinates, coord2: CubeCoordinates): Boolean {
        val path = sharedNeighboursOfTwoCoords(b, coord1, coord2)
        for(i in path)
            if(!i.isObstructed && i.pieces.size == 0)
                return true
        return false
    }

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
            b.filterFields { it.pieces.isNotEmpty() }.isEmpty()

    @JvmStatic
    fun getPossibleMoves(gs: GameState): List<IMove> {

        //Gather all setMoves

        //if(boardIsEmpty(gs.board))

        return listOf()
    }
}
