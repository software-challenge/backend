package sc.plugin2026

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.IField
import sc.plugin2026.FieldState.Companion.from
import java.util.Optional

// TODO remove this

/**
 * Ein Feld des Spielfelds. Ein Spielfeld hat eine x- und y-Koordinate und einen [FieldState], der anzeigt ob sich etwas auf diesem Feld befindet.
 */
@XStreamAlias(value = "field")
class Field @JvmOverloads constructor(
    @field:XStreamAsAttribute var x: Int,
    @field:XStreamAsAttribute var y: Int,
    @field:XStreamAsAttribute var state: FieldState = FieldState.EMPTY
): IField<Field> {
    
    constructor(x: Int, y: Int, piranha: PlayerColor): this(x, y, from(piranha))
    
    constructor(x: Int, y: Int, isObstructed: Boolean): this(
        x,
        y,
        if(isObstructed) FieldState.OBSTRUCTED else FieldState.EMPTY
    )
    
    constructor(fieldToClone: Field): this(fieldToClone.x, fieldToClone.y, fieldToClone.state)
    
    override fun clone(): Field {
        return Field(this)
    }
    
    override fun equals(obj: Any?): Boolean {
        if(obj !is Field) return false
        val field = obj
        return x == field.x && y == field.y && state == field.state
    }
    
    override fun toString(): String {
        return String.format("Field(%d|%d){%s}", x, y, state)
    }
    
    val piranha: Optional<PlayerColor>
        get() {
            if(state == FieldState.ONE) return Optional.of(Team.ONE)
            else if(state == FieldState.TWO) return Optional.of(Team.TWO)
            
            return Optional.empty()
        }
    
    /** Nur für den Server relevant.  */
    fun setPiranha(piranha: PlayerColor) {
        state = from(piranha)
    }
    
    val isObstructed: Boolean
        get() = state == FieldState.OBSTRUCTED
    
    override val isEmpty: Boolean
        get() = state == FieldState.EMPTY
}
