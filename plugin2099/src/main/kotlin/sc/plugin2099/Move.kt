package sc.plugin2099

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.Coordinates
import sc.api.plugins.IMove
import sc.plugin2099.util.GameRuleLogic

@XStreamAlias("move")
/**
 * Spielzug: Eine Bewegung eines Fisches.
 *
 * Für weitere Funktionen siehe [GameRuleLogic].
 */
data class Move(
    /** Position des zu bewegenden Fisches. */
    val field: Coordinates,
): IMove {
    
    override fun toString(): String =
        "Beanspruche $field"
    
}
