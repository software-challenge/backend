package sc.plugin2026

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.IField
import sc.api.plugins.Team
import sc.framework.DeepCloneable

@XStreamAlias("field")
enum class FieldState(val size: Int): IField, DeepCloneable<FieldState> {
    ONE_S(1),
    ONE_M(2),
    ONE_L(3),
    TWO_S(1),
    TWO_M(2),
    TWO_L(3),
    SQUID(0),
    EMPTY(0);
    
    override fun deepCopy(): FieldState = this
    
    override val isEmpty: Boolean
        get() = this == EMPTY
    
    val team: Team?
        get() = when(this) {
            ONE_S, ONE_M, ONE_L -> Team.ONE
            TWO_S, TWO_M, TWO_L -> Team.TWO
            SQUID, EMPTY -> null
        }
    
    override fun toString() =
        when(this) {
            SQUID -> "Krake"
            EMPTY -> "Leer "
            else -> team?.color.toString() + size.toString()
        }
    
    fun asLetters() =
        when(this) {
            SQUID -> "X "
            EMPTY -> "  "
            else -> team?.letter.toString() + size.toString()
        }
    
    companion object {
        fun from(team: Team, size: Int): FieldState =
            when(team) {
                Team.ONE -> when(size) {
                    1 -> ONE_S
                    2 -> ONE_M
                    3 -> ONE_L
                    else -> throw IllegalArgumentException("Invalid size: $size")
                }
                Team.TWO -> when(size) {
                    1 -> TWO_S
                    2 -> TWO_M
                    3 -> TWO_L
                    else -> throw IllegalArgumentException("Invalid size: $size")
                }
            }
        
    }
    
}
