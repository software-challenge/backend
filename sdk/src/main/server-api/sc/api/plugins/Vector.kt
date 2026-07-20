package sc.api.plugins

import kotlin.math.abs
import kotlin.math.sqrt

interface IVector {
    val dx: Int
    val dy: Int
    
    /** Verändert die Länge des Vektors um den gegebenen Faktor, ohne seine Richtung zu ändern. */
    operator fun times(scalar: Int): Vector =
        Vector(scalar * dx, scalar * dy)
    
    /** Konvertiert den Vektor zu entsprechenden [Coordinates]. */
    operator fun unaryPlus(): Coordinates = Coordinates(dx, dy)
}

/**
 * Die Strecke zwischen zwei [Coordinates].
 * @property dx die Differenz in x-Richtung
 * @property dy die Differenz in y-Richtung
 */
data class Vector(override val dx: Int, override val dy: Int): IVector, Comparable<Vector> {
    
    /** Die Fläche des Rechtecks, dessen Diagonale der Vector ist. */
    val area: Int
        get() = abs(dx * dy)
    
    /** Länge des Vektors auf einem rechteckigen Spielfeld. */
    val length: Double
        get() = sqrt(comparableLength.toDouble())
    
    /** Ob dieser Vektor auf einem doubled Hex Feld eine gerade Linie beschreibt. */
    val straightHex: Boolean
        get() = abs(dx) == abs(dy) || (dx % 2 == 0 && dy == 0)
    
    private val comparableLength: Int
        get() = dx * dx + dy * dy
    
    /**
     * Vergleicht die Länge dieses Vektors mit einem anderen.
     * @return groesser als 0, wenn dieser Vektor laenger ist
     */
    override operator fun compareTo(other: Vector): Int =
        comparableLength - other.comparableLength
}
