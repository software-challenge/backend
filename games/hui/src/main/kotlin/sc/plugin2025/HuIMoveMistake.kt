package sc.plugin2025

import sc.shared.IMoveMistake

enum class HuIMoveMistake(override val message: String) : IMoveMistake {
    GOAL_CONDITIONS("Voraussetzungen für Zielfeld nicht erfüllt"),
    NO_SALAD("Kein Salat verfügbar"),
    MISSING_CARROTS("Nicht genügend Karotten"),
    MUST_EAT_SALAD("Auf einem Salatfeld muss ein Salat gegessen werden"),
    CANNOT_ENTER_FIELD("Feld kann nicht betreten werden"),
    FIELD_OCCUPIED("Das Feld ist besetzt"),
    FIELD_NONEXISTENT("Das Feld existiert nicht"),
    HEDGEHOG_ONLY_BACKWARDS("Ein Igelfeld kann nur mit einem Rückwärtzug betreten werden"),
    CANNOT_MOVE_FORWARD("Vorwärtszug ist nicht möglich"),
    CANNOT_FALL_BACK("Rückwärtszug ist nicht möglich"),
    CANNOT_EAT_SALAD("Es kann gerade kein Salat gegessen werden"),
    CANNOT_EXCHANGE_CARROTS("Karotten tauschen kann nicht mit dieser Karottenanzahl gespielt werden"),
    
    MUST_BUY_ONE_CARD("Auf einem Marktfeld muss genau eine Karte gekauft werden"),
    MUST_PLAY_CARD("Beim Betreten eines Hasenfeldes muss eine Hasenkarte gespielt werden"),
    CARD_NOT_OWNED("Karte kann nicht gespielt werden, da nicht im Besitz"),
    CANNOT_PLAY_CARD("Karte kann nicht gespielt werden"),
    CANNOT_PLAY_SWAP_CARROTS_ALREADY_PLAYED("Karottenaustauschkarte kann nicht gespielt werden: Wurde bereits innerhalb der letzten zwei Züge gespielt"),
    CANNOT_PLAY_SWAP_CARROTS_BEYOND_LAST_SALAD("Karottenaustauschkarte kann nicht gespielt werden: Nicht alle Spieler sind vor dem letzten Salatfeld"),
    CANNOT_PLAY_FALL_BACK("Rückzugskarte nicht spielbar"),
    CANNOT_PLAY_HURRY_AHEAD("Vorwärtssprungkarte nicht spielbar"),
}
