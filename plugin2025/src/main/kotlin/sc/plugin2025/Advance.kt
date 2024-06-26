package sc.plugin2025

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamImplicit
import sc.plugin2025.util.HuIConstants
import sc.shared.IMoveMistake

/**
 * Ein Vorwärtszug um die angegebene Distanz.
 * - verbrauchte Karotten werden mit k = (distance * (distance + 1)) / 2 berechnet (Gaußsche Summe).
 * - falls der Zug auf einem Hasenfeld endet, müssen auszuführende Hasenkarten mitgegeben werden.
 * - falls der Zug auf einem Marktfeld endet, wird eine zu kaufende Hasenkarte mitgegeben.
 *   Der Wert der Karottentauschkarte spielt dann keine Rolle.
 */
@XStreamAlias(value = "advance")
class Advance(
    @XStreamAsAttribute val distance: Int,
    @XStreamImplicit private vararg val cards: Card
): Move {
    
    val cost: Int
        get() = GameRuleLogic.calculateCarrots(distance)
    
    @Suppress("USELESS_ELVIS")
    fun getCards() = cards ?: arrayOf()
    
    override fun perform(state: GameState): IMoveMistake? {
        val player = state.currentPlayer
        val check = state.checkAdvance(distance)
        if(check != null)
            return check
        player.advanceBy(distance)
        
        var lastCard: Card? = null
        var bought = false
        return getCards().firstNotNullOfOrNull {
            if(bought)
                return HuIMoveMistake.MUST_BUY_ONE_CARD
            if(state.currentField == Field.MARKET) {
                return@firstNotNullOfOrNull player.consumeCarrots(10) ?: run {
                    bought = true
                    player.addCard(it)
                    null
                }
            }
            if(state.currentField != Field.HARE || lastCard?.moves == false)
                return HuIMoveMistake.CANNOT_PLAY_CARD
            lastCard = it
            it.perform(state)
        } ?: HuIMoveMistake.MUST_BUY_ONE_CARD.takeIf {
            // On Market field and no card bought or just moved there through card
            state.currentField == Field.MARKET && !bought
        } ?: HuIMoveMistake.MUST_PLAY_CARD.takeIf {
            // On Hare field and no card played or just moved there through card
            state.currentField == Field.HARE && lastCard?.moves != false
        }
    }
    
    @Suppress("SAFE_CALL_WILL_CHANGE_NULLABILITY", "UNNECESSARY_SAFE_CALL")
    override fun toString(): String =
        "Vorwärts um $distance${cards?.takeUnless { it.isEmpty() }?.joinToString(prefix = " mit Karten [", postfix = "]") { it.label } ?: ""}"
    
    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is Advance) return false
        if(distance != other.distance) return false
        return getCards().contentEquals(other.getCards())
    }
    
    override fun hashCode(): Int =
        distance + getCards().contentHashCode() * HuIConstants.NUM_FIELDS
    
}