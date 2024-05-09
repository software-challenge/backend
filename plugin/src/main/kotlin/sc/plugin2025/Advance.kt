package sc.plugin2025

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.plugin2025.GameRuleLogic.calculateCarrots
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
        val player = state.currentPlayer
        val check = state.checkAdvance(distance)
        if(check != null)
            return check
        player.carrots -= calculateCarrots(distance)
        player.position += distance
        return when(state.currentField) {
            Field.HARE -> {
                if(cards.isEmpty())
                    return MoveMistake.MUST_PLAY_CARD
                cards.firstNotNullOfOrNull {
                    it.perform(state)
                }
            }
            Field.MARKET -> {
                if(cards.size > 1)
                    return MoveMistake.CANNOT_BUY_MULTIPLE_CARDS
                cards.firstNotNullOfOrNull { card ->
                    return player.consumeCarrots(10) ?: run {
                        player.addCard(card.card)
                        null
                    }
                }
            }
            else -> MoveMistake.CANNOT_PLAY_CARD.takeIf { cards.isNotEmpty() }
        }
    }
    
}