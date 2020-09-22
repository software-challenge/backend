package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.IMove

sealed class Move: IMove {
    abstract val color: Color
    abstract override fun toString(): String
}

@XStreamAlias(value = "setmove")
class SetMove(val piece: Piece): Move() {
    override val color: Color get() = piece.color

    override fun toString(): String = piece.toString()
    override fun equals(other: Any?): Boolean = piece == other
    override fun hashCode(): Int = piece.hashCode()
}

@XStreamAlias(value = "passmove")
class PassMove(override val color: Color): Move() {
    override fun toString(): String = "$color passed"
}

@XStreamAlias(value = "skipmove")
class SkipMove(override val color: Color): Move() {
    override fun toString(): String = "$color skipped"
}