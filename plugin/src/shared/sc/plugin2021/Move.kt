package sc.plugin2021

import sc.api.plugins.IMove

sealed class Move(val color: Color): IMove {
    abstract override fun toString(): String
}

class SetMove(val piece: Piece): Move(piece.color) {
    override fun toString(): String = piece.toString()
    override fun equals(other: Any?): Boolean = piece == other
    override fun hashCode(): Int = piece.hashCode()
}

class PassMove(color: Color): Move(color) {
    override fun toString(): String = "$color passed out"
}