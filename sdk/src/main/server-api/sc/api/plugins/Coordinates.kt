package sc.api.plugins

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

class Positioned<FIELD>(
        override val key: Coordinates,
        override val value: FIELD
): Map.Entry<Coordinates, FIELD>

/** Eine kartesische 2D-Koordinate der Form (x, y). */
@XStreamAlias(value = "coordinates")
data class Coordinates(
        @XStreamAsAttribute val x: Int,
        @XStreamAsAttribute val y: Int,
) {
    
    override fun toString(): String = "[$x|$y]"
    
    /** Addiere den [Vector] auf die [Coordinates] auf. */
    operator fun plus(vector: IVector): Coordinates =
            Coordinates(x + vector.dx, y + vector.dy)
    /** Berechne die Distanz zweier Koordinaten, als [Vector] */
    operator fun minus(other: Coordinates): Vector =
            Vector(x - other.x, y - other.y)
    /** Ziehe die Distanz (als [Vector]) von der Koordinate ab. */
    operator fun minus(other: IVector): Coordinates =
            Coordinates(x - other.dx, y - other.dy)
    /** Wandelt die [Coordinates] in einen entsprechenden [Vector]. */
    operator fun unaryPlus(): Vector = Vector(x, y)
    
    /** Gibt die vier benachbarten Feldkoordinaten zurück. */
    val neighbors: Collection<Coordinates>
        get() = Direction.cardinals.map { this + it }
    
    companion object {
        /** Der Ursprung des Koordinatensystems (0, 0). */
        val ORIGIN = Coordinates(0, 0)
    }
}