package sc.plugin2024.exceptions

import sc.shared.IMoveMistake

enum class AdvanceException(override val message: String) : IMoveMistake {
    NO_MOVEMENT_POINTS("Keine Bewegungspunkte mehr vorhanden"),
    INVALID_DISTANCE("Zurückzulegende Distanz ist ungültig."),
    BACKWARD_MOVE_NOT_POSSIBLE("Rückwärtszug ist nur von Sandbank aus möglich."),
    ONLY_ONE_MOVE_ALLOWED_ON_SANDBANK("Nur eine Bewegung nach vorne auf einer Sandbank möglich."),
    SHIP_ALREADY_IN_TARGET("Der Zug darf nicht auf dem Gegner enden."),
    FIELD_NOT_EXIST("Feld ist nicht vorhanden."),
    FIELD_IS_BLOCKED("Feld ist blockiert."),
    MOVE_END_ON_SANDBANK("Zug sollte bereits enden, da auf Sandbank gefahren wurde.");
    
    override fun toString() = message
}