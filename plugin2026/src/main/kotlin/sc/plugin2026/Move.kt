package sc.plugin2026

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.Coordinates
import sc.api.plugins.IMove
import sc.api.plugins.Vector

@XStreamAlias("move")
/** Ein Spielzug. */
data class Move(
    /** Ursprungsposition des Zugs. */
    val from: Coordinates,
    /** Zielposition des Zugs. */
    val to: Coordinates,
): IMove, Comparable<Move> {
    val delta: Vector?
        get() = from?.let { to - it }
    
    fun reversed(): Move? =
        from?.let { Move(to, from) }
    
    override fun compareTo(other: Move): Int =
        other.delta?.let { delta?.compareTo(it) } ?: 0
    
    override fun toString(): String =
        "Schwimmen $from zu $to"
    
    companion object {
        @JvmStatic
        fun run(start: Coordinates, delta: Vector): Move =
            Move(start, (start + delta))
    }
}
