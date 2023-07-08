package sc.api.plugins

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

/** Eine 2D Koordinate der Form (x, y).
 * Für Hex-Koordinaten siehe https://www.redblobgames.com/grids/hexagons/#coordinates-doubled */
@XStreamAlias(value = "coordinates")
data class Coordinates(
    @XStreamAsAttribute val x: Int,
    @XStreamAsAttribute val y: Int,
) {
    
    override fun toString(): String = "[$x|$y]"
    
    /** Addiere den [Vector] auf die [Coordinates] auf. */
    operator fun plus(vector: IVector): Coordinates =
        Coordinates(x + vector.dx, y + vector.dy)
    /** Berechne die Distanz zweier Koordinaten, als [Vector] */
    operator fun minus(other: Coordinates): Vector =
        Vector(x - other.x, y - other.y)
    /** Ziehe die Distanz (als [Vector]) von der Koordinate ab. */
    operator fun minus(other: IVector): Coordinates =
        Coordinates(x - other.dx, y - other.dy)
    /** Wandelt die [Coordinates] in einen entsprechenden [Vector]. */
    operator fun unaryPlus(): Vector = Vector(x, y)
    
    /** Gibt ein Set der vier benachbarten Felder dieser Koordinaten zurück. */
    val neighbors: Collection<Coordinates>
        get() = Vector.cardinals.map { this + it }
    
    val hexNeighbors: Collection<Coordinates>
        get() = HexDirection.values().map { this + it }
    
    /** The array indices for a rectangular board of hex fields. */
    fun fromDoubledHex() = Coordinates(x / 2, y)
    /** Turn array indices for a rectangular board into double Hex coordinates. */
    fun toDoubledHex() = doubledHex(x, y)
    
    companion object {
        /** Der Ursprung des Koordinatensystems (0, 0). */
        val origin = Coordinates(0, 0)
        
        fun doubledHex(x: Int, y: Int) = Coordinates(x * 2 + y % 2, y)
    }
}

interface IVector {
    val dx: Int
    val dy: Int
}

/**
 * Die Strecke zwischen zwei [Coordinates].
 * @property dx die Differenz in x-Richtung
 * @property dy die Differenz in y-Richtung
 */
data class Vector(override val dx: Int, override val dy: Int): IVector, Comparable<IVector> {
    
    /** Die Fläche des Rechtecks, dessen Diagonale der Vector ist. */
    val area: Int
        get() = abs(dx * dy)
    
    val length: Double
        get() = sqrt(comparableLength.toDouble())
    
    /** Verändert die Länge des Vektors um den gegebenen Faktor, ohne seine Richtung zu ändern. */
    operator fun times(scalar: Int): Vector =
        Vector(scalar * dx, scalar * dy)
    
    val straightHex: Boolean
        get() = abs(dx) == abs(dy) || (dx % 2 == 0 && dy == 0)
    
    private val IVector.comparableLength: Int
        get() = dx * dx + dy * dy
    
    /**
     * Vergleicht die Länge dieses Vektors mit einem anderen.
     * @return groesser Null, wenn dieser laenger ist
     */
    override operator fun compareTo(other: IVector): Int =
        comparableLength - other.comparableLength
    
    /** Konvertiert den Vektor zu entsprechenden [Coordinates]. */
    operator fun unaryPlus(): Coordinates = Coordinates(dx, dy)
    
    companion object {
        val diagonals = arrayOf(Vector(-1, -1), Vector(-1, 1), Vector(1, -1), Vector(1, 1))
        val cardinals = arrayOf(Vector(-1, 0), Vector(0, -1), Vector(1, 0), Vector(0, 1))
    }
}

enum class HexDirection(val vector: Vector): IVector by vector {
    RIGHT(Vector(+1, 0)),
    UP_RIGHT(Vector(+1, -1)),
    UP_LEFT(Vector(-1, -1)),
    LEFT(Vector(-1, 0)),
    DOWN_LEFT(Vector(-1, +1)),
    DOWN_RIGHT(Vector(+1, +1));
    
    fun opposite(): HexDirection = values().let { it[(ordinal + 3) % it.size] }
    
    fun turnCountTo(target: HexDirection): Int {
        val diff = target.ordinal - this.ordinal
        return if (diff >= 0) diff else diff + values().size
    }
    
    companion object {
        fun random(): HexDirection = values()[Random.nextInt(values().size)]
    }
}