package sc.plugin2099

import com.thoughtworks.xstream.annotations.XStreamAlias
import sc.api.plugins.IField
import sc.api.plugins.Team
import sc.framework.DeepCloneable

@XStreamAlias("field")
enum class FieldState(): IField, DeepCloneable<FieldState> {
    CIRCLE,
    CROSS,
    EMPTY;

    override fun deepCopy(): FieldState = this

    override val isEmpty: Boolean
        get() = this == EMPTY

    val team: Team?
        get() = when(this) {
            FieldState.CIRCLE -> Team.ONE
            FieldState.CROSS -> Team.TWO
            EMPTY -> null
        }

    override fun toString() =
        when(this) {
            CIRCLE -> "Kreis"
            CROSS -> "Kreuz"
            EMPTY -> " "
        }

    fun asLetters() =
        when(this) {
            CIRCLE -> "O "
            CROSS -> "X"
            EMPTY -> "  "
        }

}
