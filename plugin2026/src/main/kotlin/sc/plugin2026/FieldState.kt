package sc.plugin2026

import sc.api.plugins.Team

// TODO include fish size - maybe a sealed class

enum class FieldState {
    ONE,
    TWO,
    OBSTRUCTED,
    EMPTY;
    
    override fun toString() = when(this) {
        OBSTRUCTED -> "Krake"
        ONE -> "Eins"
        TWO -> "Zwei"
        else -> "empty"
    }
    
    fun asLetter() = when(this) {
        OBSTRUCTED -> 'O'
        ONE -> 'R'
        TWO -> 'B'
        else -> ' '
    }
    
    val team: Team?
        get() = when(this) {
            ONE -> Team.ONE
            TWO -> Team.TWO
            OBSTRUCTED -> null
            EMPTY -> null
        }
    
    companion object {
        @JvmStatic
        fun from(team: Team): FieldState {
            return when(team) {
                Team.ONE -> ONE
                Team.TWO -> TWO
            }
        }
    }
}
