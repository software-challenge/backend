package sc.api.plugins

import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

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
    RIGHT(Vector(+2, 0)),
    UP_RIGHT(Vector(+1, -1)),
    DOWN_LEFT(Vector(-1, +1)),
    LEFT(Vector(-2, 0)),
    UP_LEFT(Vector(-1, -1)),
    DOWN_RIGHT(Vector(+1, +1));
    
    fun withNeighbors(): Array<HexDirection> = arrayOf(rotatedBy(-1), this, rotatedBy(1))
    
    fun opposite(): HexDirection = values().let { it[(ordinal + 3) % it.size] }
    
    fun turnCountTo(target: HexDirection): Int {
        val diff = target.ordinal - this.ordinal
        return if(diff >= 0) diff else diff + values().size
    }
    
    fun rotatedBy(turns: Int): HexDirection = values().let { it[(ordinal + turns) % it.size] }
    
    companion object {
        fun random(): HexDirection = values()[Random.nextInt(values().size)]
    }
}
