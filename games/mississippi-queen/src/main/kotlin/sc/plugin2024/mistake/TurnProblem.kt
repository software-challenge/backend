package sc.plugin2024.mistake

import sc.shared.IMoveMistake

enum class TurnProblem(override val message: String) : IMoveMistake {
    ROTATION_ON_SANDBANK_NOT_ALLOWED("Drehung auf Sandbank nicht erlaubt."),
    NOT_ENOUGH_COAL_FOR_ROTATION("Nicht genug Kohle für Drehung."),
    ROTATION_ON_NON_EXISTING_FIELD("Auf einem inexistenten Feld ist keine Drehung möglich.");
}