package sc.plugin2025

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.shared.IMoveMistake

/** Mögliche Aktionen, die durch das Ausspielen einer Karte ausgelöst werden können. */
@XStreamAlias(value = "card")
enum class Card(val moves: Boolean, val playable: (GameState) -> MoveMistake?, val play: (GameState) -> Unit): HuIMove {
    /** Falle hinter den Gegenspieler. */
    FALL_BACK(true,
        { state ->
            MoveMistake.CANNOT_PLAY_FALL_BACK.takeUnless { state.isAhead() }
            ?: state.validateTargetField(state.otherPlayer.position - 1)
        },
        { it.moveToField(it.otherPlayer.position - 1) }),
    /** Rücke vor den Gegenspieler. */
    HURRY_AHEAD(true,
        { state ->
            MoveMistake.CANNOT_PLAY_HURRY_AHEAD.takeIf { state.isAhead() }
            ?: state.validateTargetField(state.otherPlayer.position + 1)
        },
        { it.moveToField(it.otherPlayer.position + 1) }),
    /** Friss sofort einen Salat. */
    EAT_SALAD(
        false,
        { state -> MoveMistake.NO_SALAD.takeUnless { state.currentPlayer.salads > 0 } },
        { it.eatSalad() }),
    /** Karottenvorrat mit dem Gegner tauschen. */
    SWAP_CARROTS(false,
        { null },
        {
            val car = it.currentPlayer.carrots
            it.currentPlayer.carrots = it.otherPlayer.carrots
            it.otherPlayer.carrots = car
        });
    
    override fun perform(state: GameState): IMoveMistake? {
        if(state.currentField != Field.HARE)
            return MoveMistake.CANNOT_PLAY_CARD
        if(!state.currentPlayer.removeCard(this))
            return MoveMistake.CARD_NOT_OWNED
        return playable(state) ?: run {
            play(state)
            null
        }
    }
}