package sc.plugin2021

class Piece(val kind: Int, val rotation: Rotation, val color: Color) {
    val shape: PieceShape = PieceShape.shapes[kind].second.rotate(rotation)
    
    override fun toString(): String {
        return "$color Piece $kind:${rotation.value}"
    }
}
