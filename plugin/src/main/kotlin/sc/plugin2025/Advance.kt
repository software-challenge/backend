package sc.plugin2025

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.plugin2025.GameRuleLogic.calculateCarrots
import sc.plugin2025.GameRuleLogic.isValidToAdvance
import sc.shared.IMoveMistake

/**
 * Ein Vorwärtszug um die angegebene Distanz.
 * - verbrauchte Karotten werden mit k = (distance * (distance + 1)) / 2 berechnet (Gaußsche Summe).
 * - falls der Zug auf einem Hasenfeld endet, müssen auszuführende Hasenkarten mitgegeben werden.
 * - falls der Zug auf einem Marktfeld endet, wird eine zu kaufende Hasenkarte mitgegeben.
 *   Der Wert der Karottentauschkarte spielt dann keine Rolle.
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