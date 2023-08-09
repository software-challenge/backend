package sc.api.plugins

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute

class Positioned<FIELD>(
        override val key: Coordinates,
        override val value: FIELD
): Map.Entry<Coordinates, FIELD>

/** Eine (normalerweise kartesische) 2D-Koordinate der Form (x, y).
 * Für Hex-Koordinaten siehe https://www.redblobgames.com/grids/hexagons/#coordinates-doubled */
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
    
    /** Gibt ein Set der vier benachbarten Felder dieser Koordinaten zurück. */
    val neighbors: Collection<Coordinates>
        get() = Vector.cardinals.map { this + it }
    
    val hexNeighbors: Collection<Coordinates>
        get() = HexDirection.values().map { this + it }
    
    /** The array indices for a rectangular board of hex fields. */
    fun fromDoubledHex() = Coordinates(x / 2, y)
    /** Turn array indices for a rectangular board into double Hex coordinates. */
    fun toDoubledHex() = doubledHex(x, y)
    
    /**
     * Wandelt DoubledHex-[Coordinates] zu [CubeCoordinates] um.
     *
     * @see <a href="https://www.redblobgames.com/grids/hexagons/#conversions-axial">Cube to Axial Coordinate Conversion</a>
     * @see <a href="https://www.redblobgames.com/grids/hexagons/#conversions-doubled">Axial to Doubled Coordinate Conversion</a>
     */
    fun doubledHexToCube(): CubeCoordinates =
            CubeCoordinates((x - y) / 2, y)
    
    companion object {
        /** Der Ursprung des Koordinatensystems (0, 0). */
        val ORIGIN = Coordinates(0, 0)
        
        fun doubledHex(x: Int, y: Int) = Coordinates(x * 2 + y % 2, y)
    }
}