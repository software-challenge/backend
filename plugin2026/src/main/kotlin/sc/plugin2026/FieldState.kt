package sc.plugin2026

import sc.api.plugins.Team

enum class FieldState(val size: Int) {
    ONE_S(1),
    ONE_M(2),
    ONE_L(3),
    TWO_S(1),
    TWO_M(2),
    TWO_L(3),
    OBSTRUCTED(0),
    EMPTY(0);
    
    override fun toString() =
        when(this) {
            OBSTRUCTED -> "Krake"
            EMPTY -> "Leer"
            else -> team?.color.toString() + size.toString()
        }
    
    fun asLetters() =
        when(this) {
            OBSTRUCTED -> "X "
            EMPTY -> "  "
            else -> team?.letter.toString() + size.toString()
        }
    
    val team: Team?
        get() = when(this) {
            ONE_S, ONE_M, ONE_L -> Team.ONE
            TWO_S, TWO_M, TWO_L -> Team.TWO
            OBSTRUCTED, EMPTY -> null
        }
    
}
