package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

/** Ein Spielstein mit Farbe, position und entsprechend transformierter Form. */
@XStreamAlias(value = "piece")
class Piece(
        /** Die Farbe des Teams, zu dem der Stein gehört. */
        @XStreamAsAttribute val color: Color = Color.BLUE,
        /** Die Form des Steins
         * @see PieceShape */
        @XStreamAsAttribute val kind: PieceShape = PieceShape.MONO,
        /** Wie weit der Stein gedreht werden soll. */
        @XStreamAsAttribute val rotation: Rotation = Rotation.NONE,
        /** Ob der Stein entlang der y-Achse gespiegelt werden soll. */
        @XStreamAsAttribute val isFlipped: Boolean = false,
        /** Die [Coordinates] der linken oberen Ecke des kleinstmöglichen Rechtecks, das die ganze Form umschließt. */
        @XStreamAsAttribute val position: Coordinates = Coordinates.origin) {
    
    constructor(color: Color = Color.BLUE,
                kind: Int,
                rotation: Rotation = Rotation.NONE,
                isFlipped: Boolean = false,
                position: Coordinates = Coordinates.origin):
            this(color, PieceShape.shapes.getValue(kind), rotation, isFlipped, position)
    
    constructor(color: Color = Color.BLUE,
                kind: PieceShape = PieceShape.MONO,
                shape: Set<Coordinates>,
                position: Coordinates = Coordinates.origin):
            this(color, kind, kind.variants.getValue(shape), position)
    
    private constructor(color: Color, kind: PieceShape, transformation: Pair<Rotation, Boolean>, position: Coordinates):
            this(color, kind, transformation.first, transformation.second, position)
    
    /** Die normalisierte Form des Steins. */
    val shape: Set<Coordinates>
        get() = lazyShape()
    
    /** Die tatsächlichen Koordinaten, die der Stein am Ende haben soll. */
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

    /**
     * Drehe und spiegel den Stein entsprechend den gegebenen Parametern.
     * @param rotation wie weit der Stein gedreht werden soll
     * @param isFlipped ob der Stein entlang der y-Achse gespiegelt werden soll
     * @return ein neuer Stein, der sich aus den Transformationen ergibt
     */
    fun transform(rotation: Rotation, isFlipped: Boolean = false): Piece =
            Piece(color, kind, rotation, isFlipped, position)

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
