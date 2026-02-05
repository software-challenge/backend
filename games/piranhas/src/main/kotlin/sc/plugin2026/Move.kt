package sc.plugin2026

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.Coordinates
import sc.api.plugins.Direction
import sc.api.plugins.IMove
import sc.plugin2026.util.GameRuleLogic

@XStreamAlias("move")
/**
 * Spielzug: Eine Bewegung eines Fisches.
 *
 * Für weitere Funktionen siehe [GameRuleLogic].
 */
data class Move(
    /** Position des zu bewegenden Fisches. */
    val from: Coordinates,
    /** Bewegungsrichtung des Zugs. */
    val direction: Direction,
): IMove {
    
    /** Erwartete Zugdistanz auf dem gegebenen [board].
     * Kann mit [direction] multipliziert werden, um den Zugvektor zu ermitteln. */
    fun getDistance(board: Board) = GameRuleLogic.movementDistance(board, this)
    
    /** Kurzbeschreibung des Zugs für Log-Ausgaben. */
    override fun toString(): String =
        "Schwimme von $from in Richtung $direction"
    
}
