package sc.plugin2025

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.shared.IMoveMistake

/** Mögliche Aktionen, die durch das Ausspielen einer Karte ausgelöst werden können. */
@XStreamAlias(value = "card")
enum class Card {
    /** Nehme Karotten auf, oder leg sie ab. */
    TAKE_OR_DROP_CARROTS,
    /** Iß sofort einen Salat. */
    EAT_SALAD,
    /** Falle eine Position zurück. */
    FALL_BACK,
    /** Rücke eine Position vor. */
    HURRY_AHEAD
}

sealed class CardAction: HuIMove {
    abstract val card: Card
    data class PlayCard(override val card: Card): CardAction() {
        override fun perform(state: GameState): IMoveMistake? {
            TODO("Not yet implemented")
        }
    }
    class CarrotCard(val value: Int): CardAction() {
        override val card = Card.TAKE_OR_DROP_CARROTS
        override fun perform(state: GameState): IMoveMistake? {
            TODO("Not yet implemented")
        }
    }
}
