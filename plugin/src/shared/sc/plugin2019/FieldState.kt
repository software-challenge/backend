package sc.plugin2019

import sc.shared.PlayerColor

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
        OBSTRUCTED -> "O"
        RED -> "R"
        BLUE -> "B"
        else -> " "
    }

    companion object {
        fun from(color: PlayerColor): FieldState {
            return when(color) {
                PlayerColor.RED -> RED
                PlayerColor.BLUE -> BLUE
            }
        }
    }
}
