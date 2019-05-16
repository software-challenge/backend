package sc.shared

enum class PlayerColor(val index: Int, private val letter: String, val displayName: String) {

    RED(0, "R", "Rot"), BLUE(1, "B", "Blau");

    /** Die Spielerfarbe des Gegners dieses Spielers */
    fun opponent(): PlayerColor =
            when(this) {
                RED -> BLUE
                BLUE -> RED
            }

    fun asLetter(): String = letter

}
