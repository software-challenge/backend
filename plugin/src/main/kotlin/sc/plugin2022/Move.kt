package sc.plugin2022

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.IMove

@XStreamAlias("Move")
/** Ein Spielzug. */
data class Move(
        /** Ursprungsposition des Zugs. */
        val from: Coordinates,
        /** Zielposition des Zugs. */
        val to: Coordinates,
): IMove, Comparable<Move> {
    val delta: Vector
        get() = to - from
    
    /** Prüft, ob die Koordinaten des Zuges valide sind,
     * nicht ob der Zug zur Figur passt. */
    val isValid
        get() = from.isValid && to.isValid
    
    fun reversed() = Move(to, from)
    
    override fun toString(): String = "Zug von $from zu $to"
    
    /** Compares the Moves based on their length ([delta]). */
    override fun compareTo(other: Move) = delta.compareTo(other.delta)
    
    companion object {
        /** @return a [Move] if the target is on the board, or null. */
        @JvmStatic
        fun create(start: Coordinates, delta: Vector): Move? =
                (start + delta).takeIf { it.isValid }?.let { dest -> Move(start, dest) }
    }
}