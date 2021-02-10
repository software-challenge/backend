package sc.plugin2021.util

import sc.shared.IMoveMistake

/**
 * Wird optional bei Validierung von Zügen zurückgegeben, falls ein Zug nicht valide ist.
 *
 * MoveMistakes entstehen bei Zügen, die theoretisch möglich sein könnten,
 * es aber bei dem jeweiligen Spielstand nicht sind.
 */
enum class MoveMistake(override val message: String): IMoveMistake {
    WRONG_COLOR("Die Farbe des Zuges ist nicht an der Reihe"),
    NOT_IN_CORNER("Der erste Zug muss auf eine freie Ecke gesetzt werden"),
    NO_SHARED_CORNER("Alle Teile müssen ein vorheriges Teil gleicher Farbe über mindestens eine Ecke berühren"),
    WRONG_SHAPE("Der erste Zug muss den festgelegten Spielstein setzen"),
    SKIP_FIRST_TURN("Der erste Zug muss einen Stein setzen"),
    DUPLICATE_SHAPE("Der gewählte Stein wurde bereits gesetzt"),
    OUT_OF_BOUNDS("Der Spielstein passt nicht vollständig auf das Spielfeld"),
    OBSTRUCTED("Der Spielstein würde eine andere Farbe überlagern"),
    TOUCHES_SAME_COLOR("Der Spielstein berührt ein Feld gleicher Farbe"),
    INVALID_FORMAT("Der Zug konnte nicht erkannt werden");
    override fun toString() = message
}