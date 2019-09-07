package sc.plugin2020

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamImplicit
import sc.api.plugins.IField
import sc.plugin2020.util.CubeCoordinates
import sc.shared.PlayerColor
import java.util.*

// NOTE that we use primitives instead of CubeCoordinates to let XStream serialize coordinates as attributes of field tag
@XStreamAlias("field")
data class Field(
        @XStreamAsAttribute
        private val x: Int = 0,
        @XStreamAsAttribute
        private val y: Int = 0,
        @XStreamAsAttribute
        private val z: Int = 0,
        @XStreamImplicit
        val pieces: Stack<Piece> = Stack(),
        @XStreamAsAttribute
        var isObstructed: Boolean = false
) : IField {

    val fieldState: FieldState
        get() {
            if(isObstructed)
                return FieldState.OBSTRUCTED
            if(!pieces.isEmpty()) {
                when(pieces.peek().owner) {
                    PlayerColor.RED -> FieldState.RED
                    PlayerColor.BLUE -> FieldState.BLUE
                }
            }
            return FieldState.EMPTY
        }

    val position: CubeCoordinates
        get() = CubeCoordinates(this.x, this.y, this.z)

    constructor(position: CubeCoordinates) : this(position.x, position.y, position.z)

    constructor(position: CubeCoordinates, obstructed: Boolean) : this(position.x, position.y, position.z, Stack(), obstructed)

    constructor(position: CubeCoordinates, pieces: Stack<Piece>) : this(position.x, position.y, position.z, pieces)

}
