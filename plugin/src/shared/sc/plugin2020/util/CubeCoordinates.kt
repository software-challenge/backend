package sc.plugin2020.util

import com.thoughtworks.xstream.annotations.XStreamAsAttribute

open class CubeCoordinates(
        @XStreamAsAttribute
        open val x: Int,
        @XStreamAsAttribute
        open val y: Int,
        @XStreamAsAttribute
        open val z: Int = -x - y
): Comparable<CubeCoordinates> {

    constructor(position: CubeCoordinates): this(position.x, position.y, position.z)

    init {
        @Suppress("LeakingThis")
        require(x + y + z == 0) { "Constraint: (x + y + z == 0) not granted for ${this}!" }
    }

    override fun toString(): String = String.format("(%d,%d,%d)", x, y, z)

    override fun equals(other: Any?): Boolean = other is CubeCoordinates && x == other.x && y == other.y && z == other.z

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + z
        return result
    }

    override operator fun compareTo(other: CubeCoordinates): Int {
        if(this.x > other.x) return 1
        if(this.x < other.x) return -1
        if(this.y > other.y) return 1
        if(this.y < other.y) return -1
        if(this.z > other.z) return 1
        return if(this.z < other.z) -1 else 0
    }

}

