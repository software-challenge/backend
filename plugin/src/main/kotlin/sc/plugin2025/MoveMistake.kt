package sc.plugin2025

import sc.shared.IMoveMistake

enum class MoveMistake(override val message: String) : IMoveMistake {
    CANNOT_EAT_SALAD("Es kann gerade kein Salat (mehr) gegessen werden."),
    MUST_PLAY_CARD("Beim Betreten eines Hasenfeldes muss eine Hasenkarte gespielt werden."),
    CARD_NOT_OWNED("Karte kann nicht gespielt werden, da nicht im Besitz."),
    CANNOT_PLAY_CARD("Karte kann nicht gespielt werden."),
    CANNOT_BUY_MULTIPLE_CARDS("Auf einem Marktfeld kann maximal eine Karte gekauft werden."),
    CANNOT_ENTER_FIELD("Vorwärtszug ist nicht möglich auf dieses Feld."),
    CANNOT_MOVE_FORWARD("Vorwärtszug ist nicht möglich."),
    CANNOT_FALL_BACK("Rückwärtszug ist nicht möglich."),
    CANNOT_CARD_EXCHANGE_CARROTS("Karottentauschkarte kann nicht für %s Karotten gespielt werden."),
    MISSING_CARROTS("Nicht genügend Karotten"),
}
