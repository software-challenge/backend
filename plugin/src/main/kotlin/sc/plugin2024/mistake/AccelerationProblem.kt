package sc.plugin2024.mistake

import sc.plugin2024.util.PluginConstants
import sc.shared.IMoveMistake

enum class AccelerationProblem(override val message: String) : IMoveMistake {
    ZERO_ACC("Es kann nicht um den Wert 0 beschleunigt werden."),
    ABOVE_MAX_SPEED("Die maximale Geschwindigkeit von ${PluginConstants.MAX_SPEED} darf nicht überschritten werden."),
    BELOW_MIN_SPEED("Die minimale Geschwindigkeit von ${PluginConstants.MIN_SPEED} darf nicht unterschritten werden."),
    INSUFFICIENT_COAL("Nicht genug Kohle für die Aktion vorhanden."),
    ON_SANDBANK("Auf einer Sandbank kann nicht beschleunigt werden.");
}