package sc.plugin2025

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.IMove
import sc.plugin2025.GameRuleLogic.calculateCarrots
import sc.plugin2025.GameRuleLogic.isValidToAdvance
import sc.shared.IMoveMistake

/**
 * Ein Vorwärtszug, um spezifizierte Distanz.
 * Verbrauchte Karotten werden mit k = (distance * (distance + 1)) / 2 berechnet (Gaußsche Summe).
 * Falls der Zug auf einem Hasenfeld endet, müssen auszuführende Hasenkarten mitgegeben werden.
 */
@XStreamAlias(value = "advance")
class Advance(@XStreamAsAttribute val distance: Int, vararg val cards: CardAction) : HuIMove {
    
    override fun perform(state: GameState): IMoveMistake? {
        if (isValidToAdvance(state, this.distance)) {
            val player = state.currentPlayer
            player.carrots -= calculateCarrots(this.distance)
            player.position += distance
            if(state.currentField == Field.HARE) {
                if(cards.isEmpty())
                    return MoveMistake.MUST_PLAY_CARD
                return cards.firstNotNullOfOrNull { it.perform(state) }
            }
            return null
        } else {
            return MoveMistake.CANNOT_MOVE_FORWARD
        }
    }
    
}