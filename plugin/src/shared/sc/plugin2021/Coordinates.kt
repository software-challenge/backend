package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAsAttribute

data class Coordinates(
        @XStreamAsAttribute val x: Int,
        @XStreamAsAttribute val y: Int) {
    
    override fun toString(): String = "[$x, $y]"
    
    operator fun plus(vector: Vector): Coordinates {
        return Coordinates(x + vector.dx, y + vector.dy)
    }
    operator fun minus(other: Coordinates): Vector {
        return Vector(x - other.x, y - other.y)
    }
    operator fun minus(other: Vector): Coordinates {
        return Coordinates(x - other.dx, y - other.dy)
    }
    operator fun unaryPlus(): Vector = Vector(x, y)
    
    companion object {
        val origin = Coordinates(0, 0)
    }
}

data class Vector(
        @XStreamAsAttribute val dx: Int,
        @XStreamAsAttribute val dy: Int) {
    val area: Int = dx * dy
    
    operator fun times(scalar: Int): Vector {
        return Vector(scalar * dx, scalar * dy)
    }
    operator fun unaryPlus(): Coordinates = Coordinates(dx, dy)
}