package sc.plugin2021

class Coordinates(val x: Int, val y: Int) {
    override fun toString(): String = "[$x, $y]"
    
    override fun equals(other: Any?): Boolean {
        return other is Coordinates &&
                other.x == x &&
                other.y == y
    }
}