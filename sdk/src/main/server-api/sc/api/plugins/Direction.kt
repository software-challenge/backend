package sc.api.plugins

import com.thoughtworks.xstream.annotations.XStreamAlias
import kotlin.random.Random

@XStreamAlias(value = "direction")
enum class Direction(val vector: Vector): IVector by vector {
    UP(Vector(0, 1)),
    // TODO
    UP_RIGHT
    RIGHT
    DOWN_RIGHT
    DOWN
    DOWN_LEFT
    LEFT
    UP_LEFT
    
    companion object {
        // TODO
        val diagonals = arrayOf(Vector(-1, -1), Vector(-1, 1), Vector(1, -1), Vector(1, 1))
        val cardinals = arrayOf(UP, RIGHT, DOWN, LEFT)
    }
}

enum class HexDirection(val vector: Vector): IVector by vector {
    RIGHT(Vector(+2, 0)),
    DOWN_RIGHT(Vector(+1, +1)),
    DOWN_LEFT(Vector(-1, +1)),
    LEFT(Vector(-2, 0)),
    UP_LEFT(Vector(-1, -1)),
    UP_RIGHT(Vector(+1, -1));
    
    fun withNeighbors(): Array<HexDirection> = arrayOf(rotatedBy(-1), this, rotatedBy(1))
    
    fun opposite(): HexDirection = values().let { it[(ordinal + 3) % it.size] }
    
    fun turnCountTo(target: HexDirection): Int {
        val diff = target.ordinal - this.ordinal
        return if(diff >= 0) diff else diff + values().size
    }
    
    fun rotatedBy(turns: Int): HexDirection = values().let { it[(ordinal + turns).mod(it.size)] }
    
    companion object {
        fun random(): HexDirection = values()[Random.nextInt(values().size)]
    }
}
