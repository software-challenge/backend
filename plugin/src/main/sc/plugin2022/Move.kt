package sc.plugin2022

import sc.api.plugins.IMove

/** Ein Spielzug. */
data class Move(
        /** Ursprungsposition des Zugs. */
        val start: Coordinates,
        /** Zielposition des Zugs. */
        val destination: Coordinates,
): IMove, Comparable<Move> {
    val delta: Vector
        get() = destination - start
    
    override fun toString(): String = "Zug von $start zu $destination"
    
    /** Compares the Moves based on their length ([delta]). */
    override fun compareTo(other: Move) = delta.compareTo(other.delta)
    
    companion object {
        /** @return a [Move] if the target is on the board, or null. */
        @JvmStatic
        fun create(start: Coordinates, delta: Vector): Move? =
                (start + delta).takeIf { it.isValid }?.let { dest -> Move(start, dest) }
    }
}