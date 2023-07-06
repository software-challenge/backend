package sc.plugin2024.exceptions

import sc.shared.IMoveMistake

enum class AdvanceException(override val message: String) : IMoveMistake {
    NO_MOVEMENT("Keine Bewegunspunkte mehr vorhanden."),
    INVALID_DISTANCE("Zurückgelegte Distanz ist ungültig."),
    BACKWARDS("Rückwärtszug ist nur von Sandbank aus möglich."),
    BLOCKED("Der Weg ist versperrt."),
    ONE_FORWARD("Nur eine Bewegung nach vorne auf einer Sandbank möglich"),
    FIELD_NOT_FOUND("Feld ist nicht vorhanden. Ungültiger Zug."),
    MOVE_ON_SANDBANK("Zug sollte bereits enden, da auf Sandbank gefahren wurde."),
    INSUFFICIENT_MOVEMENT("Nicht genug Bewegunspunkte vorhanden, um Baumstamm zu überqueren.");

    override fun toString() = message
}