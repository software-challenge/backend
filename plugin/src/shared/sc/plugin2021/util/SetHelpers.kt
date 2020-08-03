package sc.plugin2021.util

import sc.plugin2021.Coordinates
import sc.plugin2021.Rotation
import sc.plugin2021.Vector

/** Rotates the given shape based on the given rotation. */
fun Set<Coordinates>.rotate(rotation: Rotation): Set<Coordinates> = when(rotation) {
    Rotation.NONE   -> this
    Rotation.RIGHT  -> turnRight().align()
    Rotation.MIRROR -> mirror().align()
    Rotation.LEFT   -> turnLeft().align()
}

/** Flips the given shape along the y-axis. */
fun Set<Coordinates>.flip(shouldFlip: Boolean = true): Set<Coordinates> = when(shouldFlip) {
    false -> this
    true  -> this.map {
        Coordinates(-it.x, it.y)
    }.toSet().align()
}

/** Performs a 180 degrees rotation. */
fun Set<Coordinates>.mirror(): Set<Coordinates> {
    return map {
        Coordinates(-it.x, -it.y)
    }.toSet()
}

fun Set<Coordinates>.turnRight(): Set<Coordinates> {
    return map {
        Coordinates(-it.y, it.x)
    }.toSet()
}

fun Set<Coordinates>.turnLeft(): Set<Coordinates> {
    return map {
        Coordinates(it.y, -it.x)
    }.toSet()
}

/** Aligns a the coordinates along the x/y-axes (thus, the lowest coordinate is 0). */
fun Set<Coordinates>.align(): Set<Coordinates> {
    var minX = Constants.BOARD_SIZE
    var minY = Constants.BOARD_SIZE
    this.forEach {
        minX = kotlin.math.min(it.x, minX)
        minY = kotlin.math.min(it.y, minY)
    }
    return this.map {
        Coordinates(it.x - minX, it.y - minY)
    }.toSet()
}

/** Prints an ascii art of the piece. */
fun Set<Coordinates>.print(dimension: Vector = Vector(5, 3)) {
    val array = Array(dimension.area) {'-'}
    align().forEach { array[it.x + dimension.dx * it.y] = '#' }
    for (x in 0 until dimension.area) {
        print("${array[x]}  ")
        if (x % dimension.dx == dimension.dx - 1) println()
    }
}
