package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.IMove

/** Ein abstrakter Spielzug. */
sealed class Move: IMove {
    /** Die [Color], die den Zug getätigt hat. */
    abstract val color: Color
    abstract override fun toString(): String
}

/**
 * Ein Zug, der den gegebenen Spielstein auf dem Spielfeld platziert.
 * @property piece der zu platzierende Spielstein
 */
@XStreamAlias(value = "setmove")
data class SetMove(val piece: Piece): Move() {
    override val color: Color get() = piece.color

    override fun toString(): String = "Setze $piece"
}

/** Ein Zug, der die aktuelle Runde aussetzt. */
@XStreamAlias(value = "skipmove")
data class SkipMove(override val color: Color): Move() {
    override fun toString(): String = "$color setzt aus"
}