package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import kotlin.math.min

/** Eine 2D Koordinate der Form (x, y). */
@XStreamAlias(value = "coordinates")
data class Coordinates(
        @XStreamAsAttribute val x: Int,
        @XStreamAsAttribute val y: Int) {
    
    override fun toString(): String = "[$x, $y]"

    /** Addiere den [Vector] auf die [Coordinates] auf. */
    operator fun plus(vector: Vector): Coordinates {
        return Coordinates(x + vector.dx, y + vector.dy)
    }
    /** Berechne die Distanz zweier Koordinaten, als [Vector] */
    operator fun minus(other: Coordinates): Vector {
        return Vector(x - other.x, y - other.y)
    }
    /** Ziehe die Distanz (als [Vector]) von der Koordinate ab. */
    operator fun minus(other: Vector): Coordinates {
        return Coordinates(x - other.dx, y - other.dy)
    }
    /** Wandelt die [Coordinates] in einen entsprechenden [Vector]. */
    operator fun unaryPlus(): Vector = Vector(x, y)

    /** Gibt ein Set der vier Ecken dieser Koordinaten zurück. */
    val corners: Set<Coordinates>
        get() = Vector.diagonals.map { this + it }.toSet()

    /** Gibt ein Set der vier benachbarten Felder dieser Koordinaten zurück. */
    val neighbors: Set<Coordinates>
        get() = Vector.cardinals.map { this + it }.toSet()

    companion object {
        /** Der Ursprung des Koordinatensystems (0, 0). */
        val origin = Coordinates(0, 0)
    }
}

/**
 * Die Strecke zwischen zwei [Coordinates].
 * @property dx die Differenz in x-Richtung
 * @property dy die Differenz in y-Richtung
 */
data class Vector(
        @XStreamAsAttribute val dx: Int,
        @XStreamAsAttribute val dy: Int) {
    /** Die Fläche des Rechtecks, dessen Diagonale der Vector ist. */
    val area: Int = dx * dy

    /** Verändert die Länge des Vectors um den gegebenen Faktor, ohne seine Richtung zu ändern. */
    operator fun times(scalar: Int): Vector {
        return Vector(scalar * dx, scalar * dy)
    }

    /**
     * Vergleicht die beiden Vektoren. Der Rückgabewert ist
     * - positiv, wenn beide Größen dieses Vektors kleiner sind als die des anderen.
     * - null, wenn beide Vektoren gleich groß sind.
     * - negativ, wenn mindestens eine Größe dieses Vektors größer als die des anderen ist.
     */
    operator fun compareTo(other: Vector): Int =
            min(other.dx - dx, other.dy - dy)

    /** Konvertiert den Vektor zu entsprechendn [Coordinates]. */
    operator fun unaryPlus(): Coordinates = Coordinates(dx, dy)

    companion object {
        /** Die vier Vektoren in diagonaler Richtung. */
        val diagonals: Set<Vector> = setOf(
                Vector(-1, -1),
                Vector(-1, 1),
                Vector(1, -1),
                Vector(1, 1)
        )
        /** Die vier Vektoren in kardinaler Richtung. */
        val cardinals: Set<Vector> = setOf(
                Vector(-1, 0),
                Vector(0, -1),
                Vector(1, 0),
                Vector(0, 1)
        )
    }
}