package sc.plugin2026

import sc.api.plugins.Team

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
