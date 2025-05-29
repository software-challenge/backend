package sc.plugin2026

import sc.shared.IMoveMistake

/** Spielspezifische Zug-Fehler. Siehe auch [sc.shared.MoveMistake]. */
enum class PiranhaMoveMistake(override val message: String) : IMoveMistake {
    // TODO relevant bei performMove(Directly)
    WRONG_START("Das Startfeld ist kein Piranha des eigenen Teams"),
    JUMP_OVER_OPPONENT("Gegnerische Piranhas können nicht übersprungen werden"),
}
