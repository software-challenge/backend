package sc.plugin2020

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamImplicit
import sc.api.plugins.IField
import sc.plugin2020.util.CubeCoordinates
import sc.shared.PlayerColor
import java.util.*

@XStreamAlias("field")
class Field(
        x: Int = 0,
        y: Int = 0,
        z: Int = -x - y,
        pieces: Stack<Piece> = Stack(),
        @XStreamAsAttribute
        val isObstructed: Boolean = false
): CubeCoordinates(x, y, z), IField {
    
    // Custom getter because an empty collection in XML gets deserialized to null, not to an empty collection!
    // See http://x-stream.github.io/faq.html#Serialization_implicit_null
    @XStreamImplicit
    var pieces = pieces
        internal set
        get() {
            @Suppress("SENSELESS_COMPARISON")
            if(field == null) field = Stack()
            return field
        }
    
    val fieldState: FieldState
        get() {
            if(isObstructed)
                return FieldState.OBSTRUCTED
            return when(owner) {
                PlayerColor.RED -> FieldState.RED
                PlayerColor.BLUE -> FieldState.BLUE
                null -> FieldState.EMPTY
            }
        }
    
    /** @return true iff this [Field] has no pieces and is not obstructed. */
    val isEmpty: Boolean
        get() = pieces.isEmpty() && !isObstructed
    
    /** @return true iff this [Field] has pieces on it */
    val hasOwner: Boolean
        get() = pieces.isNotEmpty()
    
    /** Since [Field] is a subclass of [CubeCoordinates], this returns itself. */
    val coordinates: CubeCoordinates
        get() = this
    
    /** @return owner of the uppermost piece on this field, or null if it is empty. */
    val owner: PlayerColor?
        get() = topPiece?.owner
    
    /** @return the uppermost piece on the field, or null is it is empty. */
    val topPiece: Piece?
        get() = if(pieces.isEmpty()) null else pieces.peek()
    
    constructor(position: CubeCoordinates, obstructed: Boolean): this(position.x, position.y, position.z, isObstructed = obstructed)
    
    constructor(position: CubeCoordinates, vararg pieces: Piece): this(position.x, position.y, position.z, pieces.toCollection(Stack()))
    
    constructor (x: Int, y: Int, obstructed: Boolean): this(x, y, isObstructed = obstructed)
    
    constructor(x: Int, y: Int, vararg pieces: Piece): this(x, y, pieces = pieces.toCollection(Stack()) as Stack<Piece>)
    
    constructor(field: Field): this(field.x, field.y, field.z, field.pieces.toCollection(Stack()), field.isObstructed)
    
    public override fun clone() = Field(this)
    
}
