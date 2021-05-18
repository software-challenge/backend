package sc.plugin2022.util

import sc.shared.IMoveMistake

/**
 * Wird optional bei Validierung von Zügen zurückgegeben, falls ein Zug nicht valide ist.
 *
 * MoveMistakes entstehen bei Zügen, die theoretisch möglich sein könnten,
 * es aber bei dem jeweiligen Spielstand nicht sind.
 */
enum class MoveMistake(override val message: String): IMoveMistake {
    WRONG_COLOR("Die Farbe des Zuges ist nicht an der Reihe"),
    START_EMPTY("Das Startfeld des Zuges ist leer"),
    INVALID_MOVEMENT("%s kann sich nicht um %s bewegen"),
    INVALID_MOVE("Dieser Zug ist nicht möglich"),
    INVALID_FORMAT("Der Zug konnte nicht erkannt werden");
    override fun toString() = message
}

