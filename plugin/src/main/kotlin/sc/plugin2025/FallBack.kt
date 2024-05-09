package sc.plugin2025

import com.thoughtworks.xstream.annotations.XStreamAlias

/**
 * Rückwärtszug.
 * Sollte das nächste Igelfeld hinter einem Spieler nicht belegt sein,
 * darf anstatt nach vorne zu ziehen ein Rückzug gemacht werden.
 * Dabei werden die zurückgezogene Distanz * 10 Karotten aufgenommen.
 */
@XStreamAlias(value = "fallBack")
object FallBack: HuIMove {
    override fun perform(state: GameState): MoveMistake? {
        if(state.isValidToFallBack()) {
            val previousFieldIndex: Int = state.currentPlayer.position
            state.currentPlayer.position =
                state.board.getPreviousField(Field.HEDGEHOG, previousFieldIndex) ?: return MoveMistake.CANNOT_FALL_BACK
            state.currentPlayer.carrots += 10 * (previousFieldIndex - state.currentPlayer.position)
            return null
        } else {
            return MoveMistake.CANNOT_FALL_BACK
        }
    }
    
    override fun equals(other: Any?): Boolean = other is FallBack
}
