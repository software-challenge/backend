package sc.plugin2024.exceptions

import sc.shared.IMoveMistake

enum class TurnException(override val message: String) : IMoveMistake {
    INVALID_ROTATION("Drehung ist ungültig."),
    ROTATION_ON_SANDBANK_NOT_ALLOWED("Drehung auf Sandbank nicht erlaubt."),
    NOT_ENOUGH_COAL_FOR_ROTATION("Nicht genug Kohle für Drehung.");

    override fun toString() = message
}