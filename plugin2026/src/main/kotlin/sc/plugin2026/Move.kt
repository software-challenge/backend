package sc.plugin2026

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.Coordinates
import sc.api.plugins.Direction
import sc.api.plugins.IMove

@XStreamAlias("move")
/** Spielzug: Eine Bewegung eines Fisches. */
data class Move(
    /** Position des zu bewegenden Fisches. */
    val from: Coordinates,
    /** Bewegungsrichtung des Zugs. */
    val direction: Direction,
): IMove {
    
    override fun toString(): String =
        "Schwimme von $from in Richtung $direction"
    
}
