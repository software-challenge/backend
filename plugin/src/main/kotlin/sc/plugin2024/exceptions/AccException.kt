package sc.plugin2024.exceptions

import sc.shared.IMoveMistake

enum class AccException(override val message: String) : IMoveMistake {
    ZERO_ACC("Es kann nicht um den Wert 0 beschleunigt werden."),
    MAX_ACC("Die maximale Geschwindigkeit von 6 darf nicht überschritten werden."),
    MIN_ACC("Die minimale Geschwindigkeit von 1 darf nicht unterschritten werden."),
    SANDBANK("Auf einer Sandbank kann nicht beschleunigt werden.");

    override fun toString() = message
}