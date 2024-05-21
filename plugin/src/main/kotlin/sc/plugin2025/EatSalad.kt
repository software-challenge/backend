package sc.plugin2025

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.shared.IMoveMistake

/**
 * Eine Salatessen-Aktion auf einem Salatfeld.
 * Muss im Zug nach Betreten eines Salatfeldes ausgeführt werden.
 * Nachdem die Aktion ausgeführt wurde, muss das Salatfeld verlassen werden.
 * Durch eine Salatessen-Aktion wird ein Salat verbraucht
 * und es werden je nachdem ob der Spieler führt oder nicht 10 oder 30 Karotten aufgenommen.
 */
@XStreamAlias(value = "EatSalad")
object EatSalad: HuIMove {
    override fun perform(state: GameState): IMoveMistake? {
        if(state.mustEatSalad()) {
            state.currentPlayer.saladEaten = true
            state.eatSalad()
            return null
        } else {
            return MoveMistake.CANNOT_EAT_SALAD
        }
    }
    
    override fun equals(other: Any?): Boolean = other is EatSalad
}