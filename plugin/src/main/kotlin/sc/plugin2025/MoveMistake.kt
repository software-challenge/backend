package sc.plugin2025

import sc.shared.IMoveMistake

enum class MoveMistake(override val message: String) : IMoveMistake {
    CANNOT_EAT_SALAD("Es kann gerade kein Salat (mehr) gegessen werden."),
    MUST_PLAY_CARD("Beim Betreten eines Hasenfeldes muss eine Hasenkarte gespielt werden."),
    CANNOT_MOVE_FORWARD("Vorwärtszug ist nicht möglich."),
    CANNOT_FALL_BACK("Rückwärtszug ist nicht möglich."),
    CANNOT_CARD_EXCHANGE_CARROTS("Karottentauschkarte kann nicht für %s Karotten gespielt werden."),
}
