package sc.shared

/**
 * Wird optional bei Validierung von Zügen zurückgegeben, falls ein Zug nicht valide ist.
 *
 * MoveMistakes entstehen bei Zügen, die theoretisch möglich sein könnten,
 * es aber bei dem jeweiligen Spielstand nicht sind.
 */
enum class MoveMistake(override val message: String): IMoveMistake {
    WRONG_COLOR("Nur eigene Spielfiguren können gezogen werden"),
    START_EMPTY("Das Startfeld des Zuges ist leer"),
    
    DESTINATION_BLOCKED("Zielfeld ist besetzt"),
    DESTINATION_BLOCKED_BY_SELF("Kann nicht auf ein Feld des eigenen Teams ziehen"),
    DESTINATION_OUT_OF_BOUNDS("Das Zielfeld liegt außerhalb des Spielfelds"),
    
    INVALID_MOVEMENT("%s kann sich nicht um %s bewegen"), // TODO useful message formatting
    INVALID_MOVE("Dieser Zug ist nicht möglich"),
    INVALID_FORMAT("Der Zug konnte nicht erkannt werden");
    override fun toString() = message
}

