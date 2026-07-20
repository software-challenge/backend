package sc.plugin2020.util

import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import kotlin.math.absoluteValue
import kotlin.math.sign

open class CubeCoordinates
@JvmOverloads constructor(
        @XStreamAsAttribute
        val x: Int,
        @XStreamAsAttribute
        val y: Int,
        @XStreamAsAttribute
        val z: Int = -x - y
): Comparable<CubeCoordinates> {
    
    constructor(position: CubeCoordinates): this(position.x, position.y, position.z)
    
    init {
        require(x + y + z == 0) { "Constraint: (x + y + z == 0) not fulfilled for ${this}!" }
    }
    
    override fun toString(): String = String.format("(%d,%d,%d)", x, y, z)
    
    override fun equals(other: Any?): Boolean =
            other is CubeCoordinates && x == other.x && y == other.y && z == other.z
    
    override fun hashCode(): Int =
            x * 31 + y
    
    fun distanceTo(other: CubeCoordinates) =  ((x - other.x).absoluteValue + (y - other.y).absoluteValue + (z - other.z).absoluteValue) / 2
    
    override operator fun compareTo(other: CubeCoordinates): Int {
        var sign = (x - other.x).sign
        if(sign == 0)
            sign = (y - other.y).sign
        return sign * distanceTo(other)
    }
    
}

