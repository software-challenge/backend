package sc.plugin2098

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.Coordinates
import sc.api.plugins.Direction
import sc.api.plugins.IMove
import sc.plugin2098.util.GameRuleLogic

@XStreamAlias("move")
/**
 * Spielzug: Das setzen eines Plätchens.
 *
 * Für weitere Funktionen siehe [GameRuleLogic].
 */
data class Move(
    /** Position an der das Plätchen plaziert werden soll. */
    val position: Coordinates,
): IMove {
    /** Kurzbeschreibung des Zugs für Log-Ausgaben. */
    override fun toString(): String =
        "Plaziere ein Plätchen auf $position."
}
