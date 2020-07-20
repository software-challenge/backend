package sc.plugin2021

import sc.api.plugins.IMove

sealed class Move: IMove {
    abstract val piece: Piece
}
