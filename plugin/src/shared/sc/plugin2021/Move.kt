package sc.plugin2021

import sc.api.plugins.IMove

sealed class Move(val color: Color): IMove {
    abstract override fun toString(): String
}

class SetMove(val piece: Piece): Move(piece.color) {
    override fun toString(): String = piece.toString()
}