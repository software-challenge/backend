package sc.plugin2024.exceptions

import sc.shared.IMoveMistake

enum class MoveException(override val message: String) : IMoveMistake {
    NO_ACTIONS("Der Zug enthält keine Aktionen"),
    PUSH_ACTION_REQUIRED("Wenn du auf einem gegnerischen Schiff landest, muss darauf eine Abdrängaktion folgen."),
    SAND_BANK_END("Zug auf eine Sandbank muss letzte Aktion sein."),
    TURN_ON_SANDBANK("Du kannst nicht auf einer Sandbank drehen."),
    FIRST_ACTION_ACCELERATE("Du kannst nur in der ersten Aktion beschleunigen."),
    OPPONENT_END("Der Zug darf nicht auf dem Gegner enden."),
    INSUFFICIENT_COAL("Nicht genug Kohle für den Zug vorhanden."),
    MOVEMENT_POINTS_LEFT("Es sind noch Bewegungspunkte übrig."),
    EXCESS_MOVEMENT_POINTS("Es sind Bewegungspunkte zuviel verbraucht worden.");

    override fun toString() = message
}
