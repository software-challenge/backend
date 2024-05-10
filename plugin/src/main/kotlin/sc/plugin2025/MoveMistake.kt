package sc.plugin2025

import sc.shared.IMoveMistake

enum class MoveMistake(override val message: String) : IMoveMistake {
    MISSING_CARROTS("Nicht genügend Karotten"),
    MUST_EAT_SALAD("Auf einem Salatfeld muss ein Salat gegessen werden"),
    CANNOT_ENTER_FIELD("Feld kann nicht betreten werden."),
    CANNOT_MOVE_FORWARD("Vorwärtszug ist nicht möglich."),
    CANNOT_FALL_BACK("Rückwärtszug ist nicht möglich."),
    CANNOT_EAT_SALAD("Es kann gerade kein Salat (mehr) gegessen werden."),
    CANNOT_EXCHANGE_CARROTS("Karottentauschen kann nicht mit dieser Karottenanzahl gespielt werden."),
    
    MUST_BUY_ONE_CARD("Auf einem Marktfeld muss genau eine Karte gekauft werden."),
    MUST_PLAY_CARD("Beim Betreten eines Hasenfeldes muss eine Hasenkarte gespielt werden."),
    CARD_NOT_OWNED("Karte kann nicht gespielt werden, da nicht im Besitz."),
    CANNOT_PLAY_CARD("Karte kann nicht gespielt werden."),
    CANNOT_PLAY_FALL_BACK("Rückzugskarte nicht spielbar."),
    CANNOT_PLAY_HURRY_AHEAD("Vorwärtssprungkarte nicht spielbar."),
}
