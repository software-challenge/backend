package sc.plugin2099

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.IField
import sc.api.plugins.Team
import sc.framework.DeepCloneable

@XStreamAlias("field")
enum class FieldState: IField, DeepCloneable<FieldState> {
    CROSS,
    CIRCLE,
    EMPTY;

    override fun deepCopy(): FieldState = this

    override val isEmpty: Boolean
        get() = this == EMPTY

    val team: Team?
        get() = when(this) {
            CROSS -> Team.ONE
            CIRCLE -> Team.TWO
            EMPTY -> null
        }

    override fun toString() =
        when(this) {
            CROSS -> "Kreuz"
            CIRCLE -> "Kreis"
            EMPTY -> " "
        }

    fun asLetters() =
        when(this) {
            CROSS -> "X "
            CIRCLE -> "O "
            EMPTY -> "  "
        }
    
    companion object {
        fun fromTeam(team: Team): FieldState = when (team) {
            Team.ONE -> CROSS
            Team.TWO -> CIRCLE
        }
    }

}
