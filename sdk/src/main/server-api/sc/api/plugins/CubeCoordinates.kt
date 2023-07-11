package sc.api.plugins

import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.framework.PublicCloneable
import kotlin.math.absoluteValue
import kotlin.math.sign

/**
 * Two-dimensional coordinates tracking each axis.
 *
 * See https://www.redblobgames.com/grids/hexagons/#coordinates-cube
 */
open class CubeCoordinates
@JvmOverloads constructor(
        @XStreamAsAttribute
        val q: Int,
        @XStreamAsAttribute
        val r: Int,
        @XStreamAsAttribute
        val s: Int = -q - r
): Comparable<CubeCoordinates>, PublicCloneable<CubeCoordinates> {
    
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
    
    override operator fun compareTo(other: CubeCoordinates): Int {
        var sign = (q - other.q).sign
        if(sign == 0)
            sign = (r - other.r).sign
        return sign * distanceTo(other)
    }
    
}

