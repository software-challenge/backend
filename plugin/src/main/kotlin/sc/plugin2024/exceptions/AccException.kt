package sc.plugin2024.exceptions

import sc.shared.IMoveMistake

enum class AccException(override val message: String) : IMoveMistake {
    FIRST_ACTION_ACCELERATE("Du kannst nur in der ersten Aktion beschleunigen."),
    ZERO_ACC("Es kann nicht um den Wert 0 beschleunigt werden."),
    ABOVE_MAX_SPEED("Die maximale Geschwindigkeit von 6 darf nicht überschritten werden."),
    BELOW_MIN_SPEED("Die minimale Geschwindigkeit von 1 darf nicht unterschritten werden."),
    INSUFFICIENT_COAL("Nicht genug Kohle für die Aktion vorhanden."),
    ON_SANDBANK("Auf einer Sandbank kann nicht beschleunigt werden.");

    override fun toString() = message
}