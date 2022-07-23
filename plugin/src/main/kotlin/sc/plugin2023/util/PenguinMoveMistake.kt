package sc.plugin2023.util

import sc.shared.IMoveMistake

/**
 * Wird optional bei Validierung von Zügen zurückgegeben, falls ein Zug nicht valide ist.
 *
 * MoveMistakes entstehen bei Zügen, die theoretisch möglich sein könnten,
 * es aber bei dem jeweiligen Spielstand nicht sind.
 */
enum class PenguinMoveMistake(override val message: String): IMoveMistake {
    SINGLE_FISH("Pinguine können nur auf einzelne Fische gesetzt werden"),
    PENGUINS("Setze zuerst alle Pinguine"),
    MAX_PENGUINS("Bereits alle Pinguine gesetzt");
    override fun toString() = message
}

