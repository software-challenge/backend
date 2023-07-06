package sc.plugin2024.exceptions

import sc.shared.IMoveMistake

enum class MoveException(override val message: String) : IMoveMistake {
    // AdvanceException
    NO_MOVEMENT("Keine Bewegunspunkte mehr vorhanden."),
    INVALID_DISTANCE("Zurückgelegte Distanz ist ungültig."),
    BACKWARDS("Rückwärtszug ist nur von Sandbank aus möglich."),
    BLOCKED("Der Weg ist versperrt."),
    ONE_FORWARD("Nur eine Bewegung nach vorne auf einer Sandbank möglich"),
    FIELD_NOT_FOUND("Feld ist nicht vorhanden. Ungültiger Zug."),
    MOVE_ON_SANDBANK("Zug sollte bereits enden, da auf Sandbank gefahren wurde."),
    INSUFFICIENT_MOVEMENT("Nicht genug Bewegunspunkte vorhanden, um Baumstamm zu überqueren"),

    // AccException
    ZERO_ACC("Es kann nicht um den Wert 0 beschleunigt werden."),
    MAX_ACC("Die maximale Geschwindigkeit von 6 darf nicht überschritten werden."),
    MIN_ACC("Die minimale Geschwindigkeit von 1 darf nicht unterschritten werden."),
    SANDBANK("Auf einer Sandbank kann nicht beschleunigt werden."),

    // TurnException
    INVALID_TURN("Drehung ist ungültig."),
    TURN_SANDBANK("Drehung auf Sandbank nicht erlaubt."),
    COAL("Nicht genug Kohle für Drehung"),

    // Additional MoveException cases
    NO_ACTIONS("Der Zug enthält keine Aktionen"),
    PUSH_ACTION_REQUIRED("Wenn du auf einem gegnerischen Schiff landest, muss darauf eine Abdrängaktion folgen.");

    override fun toString() = message
}
