package sc.plugin2021

import sc.api.plugins.IMove

sealed class Move: IMove {
    abstract val piece: Piece
    /** The coordinates the left upper corner of the piece is on. */
    abstract val position: Coordinates
}
