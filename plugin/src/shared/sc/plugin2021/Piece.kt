package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamOmitField

/** A Piece has a color, a position and a normalised shape. */
//@XStreamAlias(value = "piece")
class Piece(@XStreamAsAttribute val color: Color = Color.BLUE,
            @XStreamAsAttribute val kind: Int = 0,
            @XStreamAsAttribute val rotation: Rotation = Rotation.NONE,
            @XStreamAsAttribute val isFlipped: Boolean = false,
            @XStreamAsAttribute val position: Coordinates = PieceShape.origin) {
    
    @XStreamOmitField
    val shape: PieceShape
    
    @XStreamOmitField
    val coordinates: Set<Coordinates>
    
    
    init {
        shape = PieceShape.shapes[kind]?.flip(isFlipped)?.rotate(rotation)
                ?: throw ArrayIndexOutOfBoundsException("The Piece type must be between 0 and 20 (was $kind)")
        coordinates = shape.asVectors.map { position + it }.toSet()
    }

    override fun toString(): String =
            "$color Piece $kind:${rotation.value}${if (isFlipped) " (flipped)" else ""} [${position.x},${position.y}]"
    
    override fun equals(other: Any?): Boolean {
        return other is Piece &&
                other.color == color &&
                other.kind == kind &&
                other.rotation == rotation &&
                other.isFlipped == isFlipped &&
                other.position == position
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + coordinates.hashCode()
        return result
    }
}
