package sc.shared

enum class PlayerColor(val index: Int, val displayName: String) {
    
    RED(0, "Rot"), BLUE(1, "Blau");
    
    val letter = name.first()
    
    /** Die Spielerfarbe des Gegners dieses Spielers */
    fun opponent(): PlayerColor =
            when (this) {
                RED -> BLUE
                BLUE -> RED
            }
    
}
