package sc.plugin2021.util

import sc.plugin2021.*

/**
 * A Collection of methods callable on specific Sets or functions that take Sets as input.
 */

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
fun Set<Coordinates>.print(dimension: Vector = Vector(4, 5)) {
    printShapes(this, dimension = dimension)
}

/** Prints all given shapes next to each other. */
fun printShapes(vararg shapes: Set<Coordinates>, dimension: Vector = Vector(4, 5)) {
    val width = shapes.size * (dimension.dx + 1)
    val array = Array(dimension.dy * width) {FieldContent.EMPTY.letter}
    for (n in array.indices) {
        if ((n + 1) % (dimension.dx + 1) == 0) array[n] = ' '
    }
    for (n in shapes.indices) {
        shapes[n].align().forEach {
            array[it.x + n * (dimension.dx + 1) + width * it.y] = '#'
        }
    }
    for (x in array.indices) {
        print("${array[x]}  ")
        if ((x + 1) % width == 0 ) println()
    }
    println()
}
