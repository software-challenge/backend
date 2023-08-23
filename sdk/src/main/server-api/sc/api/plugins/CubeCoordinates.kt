package sc.api.plugins

import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.framework.PublicCloneable
import kotlin.math.absoluteValue
import kotlin.math.sign

/**
 * Two-dimensional coordinates tracking each axis.
 *
 * @see <a href="https://www.redblobgames.com/grids/hexagons/#coordinates-cube">Cube Coordinate</a>
 */
data class CubeCoordinates
@JvmOverloads constructor(
        @XStreamAsAttribute
        val q: Int,
        @XStreamAsAttribute
        val r: Int,
        @XStreamAsAttribute
        val s: Int = -q - r,
): Comparable<CubeCoordinates>, PublicCloneable<CubeCoordinates> {
    
    constructor(position: CubeCoordinates): this(position.q, position.r, position.s)
    
    override fun clone(): CubeCoordinates = CubeCoordinates(this)
    
    init {
        require(q + r + s == 0) { "Constraint: (x + y + z == 0) not fulfilled for ${this}!" }
    }
    
    val coordinates: IntArray
        get() = intArrayOf(q, r, s)
    
    val x: Int
        get() = q * 2 + r
    
    operator fun times(count: Int): CubeCoordinates =
            CubeCoordinates(q * count, r * count)
    
    operator fun plus(other: CubeCoordinates): CubeCoordinates =
            CubeCoordinates(q + other.q, r + other.r)
    
    /** Berechne die Distanz zweier Koordinaten. */
    operator fun minus(other: CubeCoordinates): CubeCoordinates =
            CubeCoordinates(q - other.q, r - other.r)
    
    /** Spiegelt diese Koordinaten. */
    operator fun unaryMinus() =
            CubeCoordinates(-q, -r, -s)
    
    /** Rotated by *turns* to the right. */
    fun rotatedBy(turns: Int) =
            CubeCoordinates(coordinates[turns.mod(3)], coordinates[turns.plus(1).mod(3)], coordinates[turns.plus(2).mod(3)]).let {
                if(turns.mod(2) == 1) it.unaryMinus() else it
            }
    
    /**
     * Wandelt [CubeCoordinates] zu DoubledHex-[Coordinates] um.
     *
     * @see <a href="https://www.redblobgames.com/grids/hexagons/#conversions-axial">Cube to Axial Coordinate Conversion</a>
     * @see <a href="https://www.redblobgames.com/grids/hexagons/#conversions-doubled">Axial to Doubled Coordinate Conversion</a>
     */
    fun cubeToDoubledHex(): Coordinates =
            Coordinates(2 * q + r, r)
    
    fun distanceTo(other: CubeCoordinates): Int =
            ((q - other.q).absoluteValue + (r - other.r).absoluteValue + (s - other.s).absoluteValue) / 2
    
    override operator fun compareTo(other: CubeCoordinates): Int {
        var sign = (q - other.q).sign
        if(sign == 0)
            sign = (r - other.r).sign
        return sign * distanceTo(other)
    }
    
    override fun toString(): String = String.format("(%d,%d,%d)", q, r, s)
    
    override fun equals(other: Any?): Boolean =
            other is CubeCoordinates && q == other.q && r == other.r && s == other.s
    
    override fun hashCode(): Int = q * 9999 + r
    
    companion object {
        /** Der Ursprung des Koordinatensystems (0, 0). */
        val ORIGIN = CubeCoordinates(0, 0)
    }
    
}

enum class CubeDirection(val vector: CubeCoordinates) {
    RIGHT(CubeCoordinates(+1, 0)),
    DOWN_RIGHT(CubeCoordinates(0, +1)),
    DOWN_LEFT(CubeCoordinates(-1, +1)),
    LEFT(CubeCoordinates(-1, 0)),
    UP_LEFT(CubeCoordinates(0, -1)),
    UP_RIGHT(CubeCoordinates(+1, -1));
    
    val angle
        get() = ordinal * 60
    
    /** @return an array of this and the two neighboring directions. */
    fun withNeighbors() = arrayOf(rotatedBy(-1), this, rotatedBy(1))
    
    fun opposite() = values().let { it[(ordinal + 3) % it.size] }
    
    /** Required clockwise turns to target direction.
     * @return value between 3 and -3 */
    fun turnCountTo(target: CubeDirection): Int {
        val diff = (target.ordinal - this.ordinal).mod(6)
        return if(diff > 3) diff - values().size else diff
    }
    
    /** @return the direction rotated clockwise [turns] times. */
    fun rotatedBy(turns: Int) = values().let { it[(ordinal + turns).mod(it.size)] }
}
