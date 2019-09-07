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
        // validate move TODO
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
        val l = findPiecesOfTypeAndPlayer(b, PieceType.BEE, pc)

        if(l.size == 0)
            return false

        val neighbours = getNeighbours(b, l[0])
        return neighbours.stream().allMatch { field -> field != null && (field.isObstructed || !field.pieces.empty()) }
    }

    @JvmStatic
    fun fieldsOwnedByPlayer(b: Board, color: PlayerColor): ArrayList<Field> =
            b.gameField.flatMapTo(ArrayList()) {
                it.filterNotNull().filter { field ->
                    val s = field.pieces
                    return@filter !s.empty() && s.peek().owner === color
                }
            }

    @JvmStatic
    fun findPiecesOfTypeAndPlayer(b: Board, pt: PieceType, pc: PlayerColor): ArrayList<CubeCoordinates> {
        val tmp = ArrayList<CubeCoordinates>()

        GameRuleLogic.fieldsOwnedByPlayer(b, pc).forEach { field ->
            if(field.pieces.stream().anyMatch { (_, pieceType) -> pieceType === pt }) {
                tmp.add(CubeCoordinates(field.position))
            }
        }
        return tmp
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
            is SetMove -> {
                val ownedFields = GameRuleLogic.fieldsOwnedByPlayer(gs.board, gs.currentPlayerColor)
                if(ownedFields.isEmpty()) {
                    val otherPlayerFields = GameRuleLogic.fieldsOwnedByPlayer(gs.board, gs.otherPlayerColor)
                    if(otherPlayerFields.isEmpty()) {
                        if(!GameRuleLogic.isOnBoard(m.destination)) {
                            throw InvalidMoveException(
                                    String.format(
                                            "Piece has to be placed on board. Destination (%d,%d) is out of bounds.",
                                            m.destination.x, m.destination.y, m.destination.z
                                    )
                            )
                        }
                    } else {
                        // NOTE that other player should have exactly one piece on the board here, so working with a list of fields is not really neccessary
                        val emptyNeighbours = otherPlayerFields.stream().flatMap { field -> GameRuleLogic.getNeighbours(gs.board, field.position).stream().filter { neighbour -> neighbour.fieldState == FieldState.EMPTY } }
                        if(emptyNeighbours.noneMatch { field -> field.position == m.destination }) {
                            throw InvalidMoveException("Piece has to be placed next to other players piece")
                        }
                    }
                } else {
                    if(findPiecesOfTypeAndPlayer(gs.board, PieceType.BEE, gs.currentPlayerColor).size != 1)
                        if(fieldsOwnedByPlayer(gs.board, gs.currentPlayerColor).size == 3)
                            throw InvalidMoveException("The Bee must be placed at least as fourth piece")

                    if(!gs.getUndeployedPieces(gs.currentPlayerColor).contains(m.piece))
                        throw InvalidMoveException("Piece is not a undeployed piece of the current player")

                    if(isPosNeighbourOfColor(gs.board, gs.currentPlayerColor, m.destination)) {
                        if(isPosNeighbourOfColor(gs.board, gs.currentPlayerColor.opponent(), m.destination)) {
                            throw InvalidMoveException("The destination of the move is too close to the pieces of the opponent")
                        }
                    } else {
                        throw InvalidMoveException("The destination of the move is too far away from the own pieces")
                    }
                }
            }
            is DragMove -> {
                // TODO: Check if swarm will be disconnected
                //If the queen is not placed, there should be no drag move
                if(findPiecesOfTypeAndPlayer(gs.board, PieceType.BEE, gs.currentPlayerColor).size != 1)
                    throw InvalidMoveException("The Queen is not placed. Until then drawmoves are not allowed")

                if(!isOnBoard(m.destination) || !isOnBoard(m.start))
                    throw InvalidMoveException("The IMove is out of bounds. Watch out")

                //Is a piece at the start position
                if(gs.board.getField(m.start).pieces.size == 0)
                    throw InvalidMoveException("There is no piece to move")

                //Does the piece has the color of the current player
                if(gs.board.getField(m.start).pieces.peek().owner !== gs.currentPlayerColor)
                    throw InvalidMoveException("It is not this players move")

                //No waiting moves allowed
                if(m.start.equals(m.destination))
                    throw InvalidMoveException("The destination is the start. No waiting moves allowed.")

                //Only beetles should climb on other pieces
                if(isPosOnFieldOfColor(gs.board, gs.currentPlayerColor.opponent(), m.destination) || isPosOnFieldOfColor(gs.board, gs.currentPlayerColor, m.destination))
                    if(gs.board.getField(m.start).pieces.peek().type !== PieceType.BEETLE)
                        throw InvalidMoveException("Only beetles are allowed to climb on other Pieces")

                //The general checks are done. Now the piece specific ones
                when(gs.board.getField(m.start).pieces.peek().type) {
                    PieceType.ANT -> validateAntMove(gs.board, m)
                    PieceType.BEE -> validateBeeMove(gs.board, m)
                    PieceType.BEETLE -> {
                    }
                    PieceType.GRASSHOPPER -> {
                    }
                    PieceType.SPIDER -> {
                    }
                }//HARD
            }
        }
        return true
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
        val tmp = ArrayList<Field>()

        for(i in getNeighbours(b, coord1)) {
            for(j in getNeighbours(b, coord2)) {
                if(i.position == j.position)
                    tmp.add(j)
            }
        }
        return tmp
    }

    @JvmStatic
    fun isPosOnFieldOfColor(b: Board, c: PlayerColor, coord: CubeCoordinates): Boolean {
        val playerFields = fieldsOwnedByPlayer(b, c)

        for(i in playerFields)
            if(i.position == coord)
                return true

        return false
    }

    @JvmStatic
    fun isPosNeighbourOfColor(b: Board, c: PlayerColor, coord: CubeCoordinates): Boolean {
        val playerFields = fieldsOwnedByPlayer(b, c)

        for(i in playerFields)
            for(j in getNeighbours(b, i.position))
                if(j.position == coord)
                    return true

        return false
    }

    @JvmStatic
    fun boardIsEmpty(b: Board): Boolean {
        return fieldsOwnedByPlayer(b, PlayerColor.BLUE).isEmpty() && fieldsOwnedByPlayer(b, PlayerColor.RED).isEmpty()
    }

    @JvmStatic
    fun getPossibleMoves(gs: GameState): List<IMove> {

        //Gather all setMoves

        //if(boardIsEmpty(gs.board))

        return listOf()
    }
}
