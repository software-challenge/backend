package sc.plugin2024.exceptions

import sc.shared.IMoveMistake

enum class TurnException(override val message: String) : IMoveMistake {
    INVALID_TURN("Drehung ist ungültig."),
    SANDBANK("Drehung auf Sandbank nicht erlaubt."),
    COAL("Nicht genug Kohle für Drehung.");

    override fun toString() = message
}