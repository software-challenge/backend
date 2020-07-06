package sc.plugin2021

enum class PlayerColor {
    RED, GREEN, BLUE, YELLOW
}

val pieceShapes = arrayOf(
        PieceShape(0, listOf(Coordinates(0, 0))),
        PieceShape(1, listOf(Coordinates(0, 0), Coordinates(1, 0))),
        PieceShape(2, listOf(Coordinates(0, 0), Coordinates(1, 0), Coordinates(1, 1))),
        PieceShape(3, listOf(Coordinates(0, 0), Coordinates(1, 0), Coordinates(2, 0))),
        PieceShape(4, listOf(Coordinates(0, 0), Coordinates(1, 0), Coordinates(0, 1), Coordinates(1, 1))),
        PieceShape(5, listOf(Coordinates(1, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(2, 1))),
        PieceShape(6, listOf(Coordinates(0, 0), Coordinates(1, 0), Coordinates(2, 0), Coordinates(3, 0))),
        PieceShape(7, listOf(Coordinates(2, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(2, 1))),
        PieceShape(8, listOf(Coordinates(1, 0), Coordinates(2, 0), Coordinates(0, 1), Coordinates(1, 1))),
        PieceShape(9, listOf(Coordinates(0, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(2, 1), Coordinates(3, 1))),
        PieceShape(10, listOf(Coordinates(1, 0), Coordinates(1, 1), Coordinates(0, 2), Coordinates(1, 2), Coordinates(2, 2))),
        PieceShape(11, listOf(Coordinates(0, 0), Coordinates(0, 1), Coordinates(0, 2), Coordinates(1, 2), Coordinates(2, 2))),
        PieceShape(12, listOf(Coordinates(1, 0), Coordinates(2, 0), Coordinates(3, 0), Coordinates(0, 1), Coordinates(1, 1))),
        PieceShape(13, listOf(Coordinates(2, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(2, 1), Coordinates(0, 2))),
        PieceShape(14, listOf(Coordinates(0, 0), Coordinates(0, 1), Coordinates(0, 2), Coordinates(0, 3), Coordinates(0, 4))),
        PieceShape(15, listOf(Coordinates(0, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(0, 2), Coordinates(1, 2))),
        PieceShape(16, listOf(Coordinates(1, 0), Coordinates(2, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(0, 2))),
        PieceShape(17, listOf(Coordinates(0, 0), Coordinates(1, 0), Coordinates(0, 1), Coordinates(0, 2), Coordinates(1, 2))),
        PieceShape(18, listOf(Coordinates(1, 0), Coordinates(2, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(1, 2))),
        PieceShape(19, listOf(Coordinates(1, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(2, 1), Coordinates(1, 2))),
        PieceShape(20, listOf(Coordinates(1, 0), Coordinates(0, 1), Coordinates(1, 1), Coordinates(2, 1), Coordinates(3, 1)))
)

class Piece(val kind: Int, val color: PlayerColor) {
    override fun toString(): String {
        return "$color Piece $kind"
    }
}

// data structure to represent one shape of piece of Blokus. There are 21 different kinds, see https://en.wikipedia.org/wiki/Blokus
// The shapes are represented as coordinate list of occupied fields, where the left upper corner of a shape is the origin (0,0), x-axis going to the right and y-axis going down
class PieceShape(val id: Int, val coordinates: List<Coordinates>)