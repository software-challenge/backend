package sc.plugin2025

import com.thoughtworks.xstream.annotations.XStreamAlias

/**
 * Rückwärtszug.
 * Sollte das nächste Igelfeld hinter einem Spieler nicht belegt sein,
 * darf anstatt nach vorne zu ziehen ein Rückzug gemacht werden.
 * Dabei werden die zurückgezogene Distanz * 10 Karotten aufgenommen.
 */
@XStreamAlias(value = "fallback")
object FallBack: Move {
    override fun perform(state: GameState): HuIMoveMistake? {
        val previousFieldIndex = state.nextFallBack()
        if(previousFieldIndex != null) {
            state.currentPlayer.carrots += 10 * (state.currentPlayer.position - previousFieldIndex)
            state.currentPlayer.position = previousFieldIndex
            return null
        } else {
            return HuIMoveMistake.CANNOT_FALL_BACK
        }
    }
    
    override fun toString(): String = "Zurückfallen"
    
    override fun equals(other: Any?): Boolean = other is FallBack
    
    override fun hashCode(): Int = javaClass.name.hashCode()
}
