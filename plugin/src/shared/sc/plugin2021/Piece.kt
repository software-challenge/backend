package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamOmitField
import sc.plugin2021.util.align
import sc.plugin2021.util.flip
import sc.plugin2021.util.print
import sc.plugin2021.util.rotate

/** A Piece has a color, a position and a normalised shape. */
//@XStreamAlias(value = "piece")
class Piece(@XStreamAsAttribute val color: Color = Color.BLUE,
            @XStreamAsAttribute val kind: PieceShape = PieceShape.MONO,
            @XStreamAsAttribute val rotation: Rotation = Rotation.NONE,
            @XStreamAsAttribute val isFlipped: Boolean = false,
            @XStreamAsAttribute val position: Coordinates = Coordinates.origin) {
    
    constructor(color: Color = Color.BLUE,
                kind: Int,
                rotation: Rotation = Rotation.NONE,
                isFlipped: Boolean = false,
                position: Coordinates = Coordinates.origin):
            this(color, PieceShape.shapes.getValue(kind), rotation, isFlipped, position)
    
    val shape: Set<Coordinates>
        get() = lazyShape()
    
    val coordinates: Set<Coordinates>
        get() = lazyCoordinates()
    
    private fun lazyShape(): Set<Coordinates> {
        val shape by lazy {kind.transform(rotation, isFlipped)}
        return shape
    }
    
    private fun lazyCoordinates(): Set<Coordinates> {
        val coordinates by lazy {shape.map{position + +it}.toSet()}
        return coordinates
    }

    override fun toString(): String =
            "$color Piece $kind:${rotation.value}${if (isFlipped) " (flipped)" else ""} [${position.x},${position.y}]"
    
    override fun equals(other: Any?): Boolean = when(other) {
        is SetMove -> this == other.piece
        is Piece -> {
            this.color == other.color &&
            this.coordinates == other.coordinates
        }
        else -> false
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + coordinates.hashCode()
        return result
    }
}
