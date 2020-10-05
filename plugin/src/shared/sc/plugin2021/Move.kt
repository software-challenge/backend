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
class SetMove(val piece: Piece): Move() {
    override val color: Color get() = piece.color

    override fun toString(): String = piece.toString()
    override fun equals(other: Any?): Boolean = piece == other
    override fun hashCode(): Int = piece.hashCode()
}

/** Ein Zug, der die aktuelle Runde aussetzt. */
@XStreamAlias(value = "skipmove")
class SkipMove(override val color: Color): Move() {
    override fun toString(): String = "$color skipped"
}