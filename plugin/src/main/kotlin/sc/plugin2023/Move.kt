package sc.plugin2023

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.Coordinates
import sc.api.plugins.IMove
import sc.api.plugins.Vector

@XStreamAlias("move")
/** Ein Spielzug. */
data class Move(
        /** Ursprungsposition des Zugs. */
        val from: Coordinates? = null,
        /** Zielposition des Zugs. */
        val to: Coordinates,
): IMove, Comparable<Move> {
    val delta: Vector?
        get() = from?.let { to - it }
    
    
    
    override fun compareTo(other: Move): Int =
            other.delta?.let { delta?.compareTo(it) } ?: 0
    
    override fun toString(): String =
            from?.let { "Schlittern von $from zu $to" } ?: "Setze Pinguin auf $to"
    
    companion object {
        @JvmStatic
        fun run(start: Coordinates, delta: Vector): Move =
                Move(start, (start + delta))
        @JvmStatic
        fun set(position: Coordinates): Move =
                Move(to = position)
    }
}