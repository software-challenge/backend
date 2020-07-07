package sc.plugin2021

enum class PlayerColor {
    RED, GREEN, BLUE, YELLOW
}

class Piece(val kind: Int, val rotation: Rotation, val color: PlayerColor) {
    val shape: PieceShape = pieceShapes[kind].second.rotate(rotation)
    
    override fun toString(): String {
        return "$color Piece $kind:${rotation.value}"
    }
}
