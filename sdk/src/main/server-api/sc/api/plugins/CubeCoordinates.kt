package sc.api.plugins

import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.framework.PublicCloneable
import kotlin.math.absoluteValue
import kotlin.math.sign
import kotlin.random.Random

/**
 * Two-dimensional coordinates tracking each axis.
 *
 * See https://www.redblobgames.com/grids/hexagons/#coordinates-cube
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
    
    val coordinates: IntArray
        get() = intArrayOf(q, r, s)
    
    constructor(position: CubeCoordinates): this(position.q, position.r, position.s)
    
    override fun clone(): CubeCoordinates = CubeCoordinates(this)
    
    init {
        require(q + r + s == 0) { "Constraint: (x + y + z == 0) not fulfilled for ${this}!" }
    }
    
    override fun toString(): String = String.format("(%d,%d,%d)", q, r, s)
    
    override fun equals(other: Any?): Boolean =
            other is CubeCoordinates && q == other.q && r == other.r && s == other.s
    
    override fun hashCode(): Int = q * 31 + r
    
    fun distanceTo(other: CubeCoordinates) = ((q - other.q).absoluteValue + (r - other.r).absoluteValue + (s - other.s).absoluteValue) / 2
    
    /** Rotated by *turns* to the right. */
    fun rotatedBy(turns: Int) =
            CubeCoordinates(coordinates[Math.floorMod(turns, 3)], coordinates[Math.floorMod(turns + 1, 3)], coordinates[Math.floorMod(turns + 2, 3)]).let {
                if(Math.floorMod(turns, 2) == 1) it.unaryMinus() else it
            }
    
    /** Spiegelt diese Koordinaten. */
    operator fun unaryMinus() = CubeCoordinates(-q, -r, -s)
    
    override operator fun compareTo(other: CubeCoordinates): Int {
        var sign = (q - other.q).sign
        if(sign == 0)
            sign = (r - other.r).sign
        return sign * distanceTo(other)
    }
    
}

enum class CubeDirection(val vector: CubeCoordinates) {
    RIGHT(CubeCoordinates(+1, -1)),
    UP_RIGHT(CubeCoordinates(+1, 0)),
    DOWN_LEFT(CubeCoordinates(0, +1)),
    LEFT(CubeCoordinates(-1, 1)),
    UP_LEFT(CubeCoordinates(-1, 0)),
    DOWN_RIGHT(CubeCoordinates(0, -1));
    
    fun withNeighbors() = arrayOf(rotatedBy(-1), this, rotatedBy(1))
    
    fun opposite() = values().let { it[(ordinal + 3) % it.size] }
    
    fun turnCountTo(target: HexDirection): Int {
        val diff = target.ordinal - this.ordinal
        return if(diff >= 0) diff else diff + values().size
    }
    
    fun rotatedBy(turns: Int) = values().let { it[(ordinal + turns) % it.size] }
    
    companion object {
        fun random(): CubeDirection = values()[Random.nextInt(values().size)]
    }
}
