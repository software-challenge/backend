package sc.plugin2024.mistake

import sc.shared.IMoveMistake

enum class MoveMistake(override val message: String) : IMoveMistake {
    NO_ACTIONS("Der Zug enthält keine Aktionen"),
    PUSH_ACTION_REQUIRED("Wenn du auf einem gegnerischen Schiff landest, muss darauf eine Abdrängaktion folgen."),
    SAND_BANK_END("Zug auf eine Sandbank muss letzte Aktion sein."),
    FIRST_ACTION_ACCELERATE("Du kannst nur in der ersten Aktion beschleunigen."),
    MOVEMENT_POINTS_LEFT("Es sind noch Bewegungspunkte übrig."),
    MOVEMENT_POINTS_MISSING("Nicht genug Bewegungspunkte.");
}