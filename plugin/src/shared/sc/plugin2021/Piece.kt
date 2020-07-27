package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamOmitField

/** A Piece has a color, a position and a normalised shape. */
class Piece(val color: Color = Color.BLUE,
            val kind: Int = 0,
            val rotation: Rotation = Rotation.NONE,
            val isFlipped: Boolean = false,
            val position: Coordinates = PieceShape.origin) {
    
    @XStreamOmitField
    val shape: PieceShape = PieceShape.shapes[kind]?.flip(isFlipped)?.rotate(rotation) ?:
            throw ArrayIndexOutOfBoundsException("The Piece type must be between 0 and 20 (was $kind)")
    
    @XStreamOmitField
    var coordinates: Set<Coordinates> = shape.asVectors.map { position + it }.toSet()
        private set
    
    @XStreamOmitField
    private val description: String =
            "$color Piece $kind:${rotation.value}${if(isFlipped) " (flipped)" else ""} [${position.x},${position.y}]"
    
    override fun toString(): String = description
    
    override fun equals(other: Any?): Boolean {
        return other is Piece &&
                color == other.color &&
                shape == other.shape &&
                position == other.position
    }
    
    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + coordinates.hashCode()
        return result
    }
}
