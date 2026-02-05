package sc.plugin2026

import sc.shared.IMoveMistake

/** Spielspezifische Zug-Fehler. Siehe auch [sc.shared.MoveMistake]. */
enum class PiranhaMoveMistake(override val message: String) : IMoveMistake {
    /** Startfeld gehoert nicht zum eigenen Team. */
    WRONG_START("Das Startfeld ist kein Piranha des eigenen Teams"),
    /** Gegnerische Piranhas koennen nicht uebersprungen werden. */
    JUMP_OVER_OPPONENT("Gegnerische Piranhas können nicht übersprungen werden"),
}
