package sc.shared

enum class PlayerColor(val index: Int, private val letter: String) {

    RED(0, "R"), BLUE(1, "B");

    /** Die Spielerfarbe des Gegners dieses Spielers */
    fun opponent(): PlayerColor = if(this == RED) BLUE else RED

    fun asLetter(): String = letter

}
