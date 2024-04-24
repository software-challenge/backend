package sc.plugin2025

import sc.shared.IMoveMistake

enum class MoveMistake(override val message: String) : IMoveMistake {
    NO_ACTIONS("Der Zug enthält keine Aktionen"),
    CANNOT_EAT_SALAD("Es kann gerade kein Salat (mehr) gegessen werden."),
    MUST_PLAY_CARD("Beim Betreten eines Hasenfeldes muss eine Hasenkarte gespielt werden.")
}
