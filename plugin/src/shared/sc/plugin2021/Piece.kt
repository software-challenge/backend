package sc.plugin2021

/** A Piece has a color, a position and a normalised shape. */
class Piece(val color: Color,
            kind: Int,
            rotation: Rotation,
            isFlipped: Boolean,
            val position: Coordinates = Coordinates(0, 0)) {
    
    val shape: PieceShape = PieceShape.shapes[kind]?.flip(isFlipped)?.rotate(rotation) ?:
            throw ArrayIndexOutOfBoundsException("The Piece type must be between 0 and 20 (was $kind)")
    val coordinates = shape.asVectors.map { position + it }.toSet()
    
    private val description: String = "$color Piece $kind:${rotation.value}${if(isFlipped) " (flipped)" else ""}"
    
    override fun toString(): String = description
}
