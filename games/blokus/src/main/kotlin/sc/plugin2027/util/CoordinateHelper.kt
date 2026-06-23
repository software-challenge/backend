package sc.plugin2027.util

import sc.api.plugins.Coordinates
import sc.api.plugins.Vector
import sc.plugin2027.*
import java.lang.IndexOutOfBoundsException
import kotlin.collections.HashSet
import kotlin.math.min

// A collection of methods to deal with Collections of Coordinates

fun Set<Coordinates>.rotate(rotation: Rotation) =
    when (rotation) {
        Rotation.NONE -> this
        Rotation.RIGHT -> turnRight().align()
        Rotation.MIRROR -> mirror().align()
        Rotation.LEFT -> turnLeft().align()
    }

/**
 * Spiegel die Koordinaten entlang der y-Achse.
 *
 * @param shouldFlip gibt an, ob die Koordinaten tatsächlich gespiegelt werden sollen. Wenn false, wird die ursprüngliche Menge zurückgegeben.
 * @return die gespiegelten Koordinaten, oder die ursprüngliche Menge, wenn shouldFlip false ist.
 */
fun Set<Coordinates>.flip(shouldFlip: Boolean = true) =
    when (shouldFlip) {
        false -> this
        true -> map { Coordinates(-it.x, it.y) }.align()
    }

/**
 * Drehe die Koordinaten um 180 Grad.
 *
 * @return die gespiegelten Koordinaten, also die Koordinaten mit negierten x- und y-Werten
 */
fun Collection<Coordinates>.mirror() =
    mapTo(HashSet()) { Coordinates(-it.x, -it.y) }

/**
 * Drehe die Koordinaten 90 Grad im Uhrzeigersinn.
 *
 * @return die gedrehten Koordinaten.
 */
fun Collection<Coordinates>.turnRight() =
    mapTo(HashSet()) { Coordinates(-it.y, it.x) }

/**
 * Drehe die Koordinaten 90 Grad gegen den Uhrzeigersinn.
 *
 * @return die gedrehten Koordinaten.
 */
fun Collection<Coordinates>.turnLeft() =
    mapTo(HashSet()) { Coordinates(it.y, -it.x) }

/**
 * Bewege die Koordinaten in die linke obere Ecke (Punkt(0, 0)).
 * (Dabei werden die Punkte effektiv an den beiden Achsen angelegt).
 *
 * @return die Koordinaten, so verschoben, dass die linke obere Ecke der Form bei (0, 0) liegt.
 */
fun Collection<Coordinates>.align(): Set<Coordinates> {
    var minX = Constants.BOARD_LENGTH
    var minY = Constants.BOARD_LENGTH
    forEach {
        minX = min(it.x, minX)
        minY = min(it.y, minY)
    }
    return mapTo(HashSet()) { Coordinates(it.x - minX, it.y - minY) }
}

/**
 * Berechne die Ausmaße des kleinstmöglichen Rechtecks, welches alle Koordinaten umfasst.
 * @return ein Vector von der linken oberen Ecke zur rechten unteren Ecke
 */
val Collection<Coordinates>.area: Vector
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
 * @param dimension die Ausmaße der entstehenden Grafik
 */
fun Collection<Coordinates>.print(dimension: Vector = area) =
    printShapes(this, dimension = dimension)

/**
 * Gebe die gegebenen Formen zur Konsole aus, alle in gegebenen Ausmaßen.
 *
 * @param shapes die Formen, die ausgegeben werden sollen
 * @param dimension die Ausmaße der entstehenden Grafik
 */
fun printShapes(vararg shapes: Collection<Coordinates>, dimension: Vector = Vector(4, 5)) {
    if (shapes.any { it.area < dimension })
        throw IndexOutOfBoundsException("The largest shape has to fit in the given dimension")
    
    val width = shapes.size * (dimension.dx + 1)
    val array = Array(dimension.dy * width) { FieldContent.EMPTY.letter }
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
        if ((x + 1) % width == 0) println()
    }
    println()
}