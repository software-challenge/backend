package sc.plugin2020

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.IMove
import sc.plugin2020.util.CubeCoordinates

/* NOTE: Remember to add all these classes to classesToRegister in sc/plugin2020/util/Configuration.kt */

@XStreamAlias(value = "setmove")
data class SetMove(val piece: Piece, val destination: CubeCoordinates): IMove {
    override fun toString() = String.format("Set %s %s to %s", this.piece.owner, this.piece.type, this.destination)
}

@XStreamAlias(value = "dragmove")
data class DragMove(val start: CubeCoordinates, val destination: CubeCoordinates): IMove {
    override fun toString() = String.format("Drag from %s to %s", this.start, this.destination)
}

@XStreamAlias(value = "missmove")
data class MissMove(val _miss: String? = null): IMove {
    /* parameter is only there because data classes need at least one constructor parameter */
    override fun toString() = "Miss move"
}
