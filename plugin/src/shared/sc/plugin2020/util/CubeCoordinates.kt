package sc.plugin2020.util

import com.thoughtworks.xstream.annotations.XStreamAlias

@XStreamAlias(value = "position")
data class CubeCoordinates(val x: Int,
                           val y: Int,
                           val z: Int = -x-y) : Comparable<CubeCoordinates> {

    init {
        require(x + y + z == 0) { "Constraint: (x + y + z == 0) not granted!" }
    }

    constructor(position: CubeCoordinates) : this(position.x, position.y, position.z)

    override fun toString(): String {
        return String.format("(%d,%d,%d)", this.x, this.y, this.z)
    }

    override operator fun compareTo(other: CubeCoordinates): Int {
        if(this.x > other.x) return 1
        if(this.x < other.x) return -1
        if(this.y > other.y) return 1
        if(this.y < other.y) return -1
        if(this.z > other.z) return 1
        return if(this.z < other.z) -1 else 0
    }

    override fun equals(other: Any?): Boolean {
        val to = other as CubeCoordinates?
        return this.x == to!!.x && this.y == to.y && this.z == to.z
    }
}
