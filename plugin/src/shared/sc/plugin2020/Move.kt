package sc.plugin2020

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.IMove
import sc.plugin2020.util.CubeCoordinates

@XStreamAlias(value = "setmove")
data class SetMove(val piece: Piece, val destination: CubeCoordinates): IMove {
    override fun toString() = String.format("Set %s %s to %s", this.piece.owner, this.piece.type, this.destination)
}

@XStreamAlias(value = "dragmove")
data class DragMove(val start: CubeCoordinates, val destination: CubeCoordinates): IMove {
    override fun toString() = String.format("Drag from %s to %s", this.start, this.destination)
}
