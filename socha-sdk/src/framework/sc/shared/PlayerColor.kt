package sc.shared

enum class PlayerColor {

    RED, BLUE;

    /**
     * liefert die Spielerfarbe des Gegners dieses Spielers
     * @return Spielerfarbe des Gegners
     */
    fun opponent(): PlayerColor = if(this == RED) BLUE else RED

    fun asLetter(): String = if(this == RED) "R" else "B"

}
