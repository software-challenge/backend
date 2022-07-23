package sc.plugin2022

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.IBoard
import sc.api.plugins.ITeam
import sc.api.plugins.Team
import sc.plugin2023.util.Constants.boardrange
import sc.plugin2023.Move
import sc.api.plugins.Coordinates
import sc.api.plugins.Vector
import sc.plugin2023.util.PluginConstants.boardrange
import sc.shared.MoveMistake
import sc.shared.InvalidMoveException

/** Das Spielbrett besteht aus 8x8 Feldern mit anfänglich 8 Figuren pro Spieler. */
@XStreamAlias(value = "board")
data class Board(
        @XStreamAlias("pieces")
        private val piecePositions: MutableMap<Coordinates, Piece>,
): IBoard, Map<Coordinates, Piece> by piecePositions {
    
    constructor(): this(generatePiecePositions())
    
    /** Gibt das Feld an den gegebenen Koordinaten zurück. */
    operator fun get(x: Int, y: Int) =
            get(Coordinates(x, y))
    
    /** Moves a piece according to [Move] after verification.
     * @throws InvalidMoveException if something is wrong with the Move.
     * @return number of ambers if any */
    @Throws(InvalidMoveException::class)
    fun movePiece(move: Move): Int =
            (piecePositions[move.from] ?: throw InvalidMoveException(MoveMistake.START_EMPTY, move)).let { piece ->
                if (!move.to.isValid)
                    throw InvalidMoveException(MoveMistake.OUT_OF_BOUNDS, move)
                if (move.delta !in piece.possibleMoves)
                    throw InvalidMoveException(MoveMistake.INVALID_MOVEMENT, move)
                val newPiece = piecePositions[move.to]?.let {
                    if (it.team == piece.team)
                        throw InvalidMoveException(MoveMistake.DESTINATION_BLOCKED, move)
                    piece.capture(it)
                } ?: piece
                piecePositions.remove(move.from)
                piecePositions[move.to] = newPiece
                checkAmber(move.to)
            }
    
    /** Checks whether the piece at [position] should be turned into an amber
     * and if yes, removes it.
     * @return number of ambers */
    fun checkAmber(position: Coordinates): Int =
            piecePositions[position]?.let { piece ->
                arrayOf(piece.isAmber, piece.type.isLight && position.x == piece.team.opponent().startLine)
                        .sumOf {
                            @Suppress("USELESS_CAST")
                            if (it) 1 else 0 as Int
                        }
                        .also { if (it > 0) piecePositions.remove(position) }
            } ?: 0
    
    /** Checks whether the given [piece] could jump to [destination],
     * not accounting for whether the move itself is valid. */
    fun canMoveTo(destination: Coordinates, piece: Piece) =
            destination.isValid && piecePositions[destination]?.team != piece.team
    
    fun possibleMovesFrom(position: Coordinates): List<Vector> =
            get(position)?.let { piece ->
                piece.possibleMoves.filter {
                    canMoveTo(position + it, piece)
                }
            } ?: emptyList()
    
    /** Berechnet die Züge, die die beiden Spielbretter unterscheiden.
     *
     * - Berücksichtigt nicht die Turmhöhen, kann daher in Ausnahmefällen inkorrekt sein.
     * - Die Züge müssen nicht valide Züge der entsprechenden Figuren sein.
     * - Wenn ein Stein verschwindet (z.B. weil er zu einem Bernstein wird),
     *   ist der Zielpunkt des Zuges eine invalide Koordinate.
     * - Extra Steine in [other] werden nicht berücksichtigt.
     *
     * @return List an Zügen, die nötig wären, damit dieses Board äquivalent zu [other] wird.*/
    fun diff(other: Board): Collection<Move> {
        val both = arrayOf(this, other)
        val teams = both.flatMapTo(HashSet()) { b -> b.values.map { it.team } }
        val moves = teams.flatMapTo(ArrayList()) { team ->
            PieceType.values().flatMap { type ->
                both.map { it.filterValues { it.team == team && it.type == type }.keys }
                        .zipWithNext { b1, b2 ->
                            val combinations = b1.flatMapTo(ArrayDeque()) { e1 ->
                                b2.map { e2 -> Move(e1, e2) }
                            }
                            val moves = mutableListOf<Move>()
                            while (combinations.isNotEmpty()) {
                                val move = combinations.minOrNull() ?: break
                                moves.add(move)
                                combinations.removeIf {
                                    it.from == move.from ||
                                    it.to == move.to
                                }
                            }
                            moves
                        }.single()
            }
        }
        val startPoints = moves.map { it.from }
        moves.addAll(keys.filter {
            it !in startPoints
        }.map { Move(it, Coordinates(-1, -1)) })
        moves.removeIf { it.from == it.to }
        return moves
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
                PieceType.values().let { types ->
                    val pieces = types + types
                    pieces.shuffle()
                    pieces.withIndex().flatMap { (index, type) ->
                        Team.values().map { team ->
                            createField(team, index, type)
                        }
                    }.toMap(HashMap())
                }
        
        @JvmStatic
        fun createField(team: Team, y: Int, type: PieceType) =
                Coordinates(team.startLine, if (team.index == 0) y else boardrange.last - y) to Piece(type, team)
    }
}

val ITeam.startLine
    get() = index * boardrange.last
