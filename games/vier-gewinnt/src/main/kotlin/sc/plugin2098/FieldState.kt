package sc.plugin2098

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.IField
import sc.api.plugins.Team
import sc.framework.DeepCloneable

/**
 * Feldzustand mit Fischgröße bzw. Spezialfeld.
 *
 * @property size Fischgröße oder 0 für leere/gesperrte Felder.
 */
@XStreamAlias("field")
enum class FieldState(): IField, DeepCloneable<FieldState> {
    RED, // Team ONE
    YELLOW, // Team TWO
    EMPTY;
    
    override fun deepCopy(): FieldState = this
    
    override val isEmpty: Boolean
        get() = this == EMPTY
    
    val team: Team?
        get() = when(this) {
            FieldState.RED -> Team.ONE
            FieldState.YELLOW -> Team.TWO
            EMPTY -> null
        }
    
    override fun toString() =
        when(this) {
            RED -> "Rot"
            YELLOW -> "Gelb"
            EMPTY -> " "
        }
    
    fun asLetters() =
        when(this) {
            RED -> "\uD83D\uDFE5 "
            YELLOW -> "\uD83D\uDFE8 "
            EMPTY -> "  "
        }
    
}
