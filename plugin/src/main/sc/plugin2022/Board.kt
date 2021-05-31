package sc.plugin2022

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.IBoard
import sc.api.plugins.ITeam
import sc.api.plugins.Team
import sc.plugin2022.util.Constants.boardrange
import sc.plugin2022.util.MoveMistake
import sc.shared.InvalidMoveException

/** Das Spielbrett besteht aus 8x8 Feldern mit anfänglich 8 Figuren pro Spieler. */
@XStreamAlias(value = "board")
data class Board(
        private val piecePositions: MutableMap<Coordinates, Piece>,
): IBoard, Map<Coordinates, Piece> by piecePositions {
    
    constructor(): this(generatePiecePositions())
    
    /** Gibt das Feld an den gegebenen Koordinaten zurück. */
    operator fun get(x: Int, y: Int) =
            get(Coordinates(x, y))
    
    /** Moves a piece according to [Move].
     * @throws InvalidMoveException if something is wrong with the Move.
     * @return the moved [Piece], null if it turned into an amber. */
    @Throws(InvalidMoveException::class)
    fun movePiece(move: Move): Piece? =
            (piecePositions[move.start] ?: throw InvalidMoveException(MoveMistake.START_EMPTY, move)).let { piece ->
                if (move.delta !in piece.possibleMoves)
                    throw InvalidMoveException(MoveMistake.INVALID_MOVEMENT, move)
                piecePositions[move.destination]?.let { piece.capture(it) }
                piecePositions.remove(move.start)
                if (piece.isAmber || (piece.type.isLight && move.destination.y == piece.team.opponent().startLine)) {
                    piecePositions.remove(move.destination)
                    null
                } else {
                    piecePositions[move.destination] = piece
                    piece
                }
            }
    
    override fun toString() =
            boardrange.joinToString("\n") { y ->
                boardrange.joinToString("") { x ->
                    get(x, y)?.shortString() ?: "--"
                }
            }
    
    override fun clone() = Board(HashMap(piecePositions))
    
    companion object {
        /** Generates a random new board with two pieces per type
         * for each player arranged randomly on their starting line
         * in rotational symmetry.  */
        @JvmStatic
        fun generatePiecePositions() =
                (PieceType.values() + PieceType.values()).let { pieces ->
                    pieces.shuffle()
                    pieces.withIndex().flatMap { (index, type) ->
                        Team.values().map { team ->
                            createField(team, index, type)
                        }
                    }.toMap(HashMap())
                }
        
        @JvmStatic
        fun createField(team: ITeam, x: Int, type: PieceType) =
                Coordinates(if (team.index == 0) x else boardrange.last - x, team.startLine) to Piece(type, team)
    }
}

val ITeam.startLine
    get() = index * boardrange.last
