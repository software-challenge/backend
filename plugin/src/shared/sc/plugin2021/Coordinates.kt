package sc.plugin2021

data class Coordinates(val x: Int, val y: Int) {
    override fun toString(): String = "[$x, $y]"
    
    operator fun plus(vector: Vector): Coordinates {
        return Coordinates(x + vector.dx, y + vector.dy)
    }
    operator fun minus(other: Coordinates): Vector {
        return Vector(x - other.x, y - other.y)
    }
}

data class Vector(val dx: Int, val dy: Int) {
    operator fun times(scalar: Int): Vector {
        return Vector(scalar * dx, scalar * dy)
    }
}