package sc.plugin2025

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.plugin2025.GameRuleLogic.calculateCarrots
import sc.plugin2025.GameRuleLogic.isValidToAdvance
import sc.shared.IMoveMistake

/**
 * Ein Vorwärtszug, um spezifizierte Distanz. Verbrauchte Karotten werden mit k = (distance * (distance + 1)) / 2
 * berechnet (Gaußsche Summe)
 */
@XStreamAlias(value = "advance")
class Advance(@XStreamAsAttribute val distance: Int) : Action {

    override fun perform(state: GameState): IMoveMistake? {
        if (isValidToAdvance(state, this.distance)) {
            state.currentPlayer.carrots -= calculateCarrots(this.distance)
            state.currentPlayer.position += distance
            return null
        } else {
            return MoveMistake.CANNOT_MOVE_FORWARD
        }
    }

    override fun equals(other: Any?) = other is Advance && this.distance == other.distance
    override fun hashCode(): Int = distance
}
