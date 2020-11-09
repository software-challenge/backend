package sc.plugin2021.util

import sc.plugin2021.*
import sc.shared.InvalidMoveException
import java.lang.IndexOutOfBoundsException

// A Collection of methods callable on specific Sets or functions that take Sets as input.

/** Drehe die Koordinaten um die gegebene Anzahl an Rotationen. */
fun Set<Coordinates>.rotate(rotation: Rotation): Set<Coordinates> = when(rotation) {
    Rotation.NONE   -> this
    Rotation.RIGHT  -> turnRight().align()
    Rotation.MIRROR -> mirror().align()
    Rotation.LEFT   -> turnLeft().align()
}

/** Spiegel die Koordinaten entlang der y-Achse. */
fun Set<Coordinates>.flip(shouldFlip: Boolean = true): Set<Coordinates> = when(shouldFlip) {
    false -> this
    true  -> this.map {
        Coordinates(-it.x, it.y)
    }.toSet().align()
}

/** Drehe die Koordinaten um 180 Grad. */
fun Set<Coordinates>.mirror(): Set<Coordinates> {
    return map {
        Coordinates(-it.x, -it.y)
    }.toSet()
}

/** Drehe die Koordinaten 90 Grad im Uhrzeigersinn. */
fun Set<Coordinates>.turnRight(): Set<Coordinates> {
    return map {
        Coordinates(-it.y, it.x)
    }.toSet()
}

/** Drehe die Koordinaten 90 Grad gegen den Uhrzeigersinn. */
fun Set<Coordinates>.turnLeft(): Set<Coordinates> {
    return map {
        Coordinates(it.y, -it.x)
    }.toSet()
}

/**
 * Bewege die Koordinaten in die linke obere Ecke (Punkt(0, 0)).
 * (Dabei werden die Puknte effektiv an den beiden Achsen angelegt).
 */
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

/**
 * Berechne die Ausmaße des kleinstmöglichen Rechtecks, welches alle Koordinaten umfasst.
 * @return ein Vector von der linken oberen Ecke zur rechten unteren Ecke
 */
val Set<Coordinates>.area: Vector
    get() {
        var dx = 0
        var dy = 0
        forEach {
            dx = kotlin.math.max(it.x, dx)
            dy = kotlin.math.max(it.y, dy)
        }
        return Vector(dx, dy)
    }

/**
 * Gebe die Form der Koordinaten zur Konsole aus.
 * @param dimension die Ausmaße der entstehenden Graphik
 */
fun Set<Coordinates>.print(dimension: Vector = area) {
    printShapes(this, dimension = dimension)
}

/** Gebe die gegebenen Formen zur Konsole aus, alle in gegebenen Ausmaßen. */
fun printShapes(vararg shapes: Set<Coordinates>, dimension: Vector = Vector(4, 5)) {
    if (shapes.any{ it.area < dimension })
        throw IndexOutOfBoundsException("The largest shape has to fit in the given dimension")
        
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

/** Entferne alle Züge, die nicht auf den gegebenen Spielstand anwendbar sind. */
fun Set<SetMove>.filterValidMoves(gameState: GameState): Set<SetMove> =
        filter { GameRuleLogic.isValidSetMove(gameState, it) }.toSet()
