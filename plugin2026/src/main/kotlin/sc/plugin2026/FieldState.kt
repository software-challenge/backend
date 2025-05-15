package sc.plugin2026

enum class FieldState {
    RED,
    BLUE,
    OBSTRUCTED,
    EMPTY;
    
    override fun toString() = when(this) {
        OBSTRUCTED -> "Obstructed"
        RED -> "Red"
        BLUE -> "Blue"
        else -> "empty"
    }
    
    fun asLetter() = when(this) {
        OBSTRUCTED -> 'O'
        RED -> 'R'
        BLUE -> 'B'
        else -> ' '
    }
    
    companion object {
        @JvmStatic
        fun from(color: PlayerColor): FieldState {
            return when(color) {
                Team.ONE -> RED
                Team.TWO -> BLUE
            }
        }
    }
}
