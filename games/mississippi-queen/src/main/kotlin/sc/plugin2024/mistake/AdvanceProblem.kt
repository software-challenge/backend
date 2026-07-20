package sc.plugin2024.mistake

import sc.shared.IMoveMistake

enum class AdvanceProblem(override val message: String) : IMoveMistake {
    MOVEMENT_POINTS_MISSING("Nicht genug Bewegungspunkte."),
    INSUFFICIENT_PUSH("Nicht genug Bewegungspunkte für notwendige nachfolgende Abdrängaktion."),
    INVALID_DISTANCE("Zurückzulegende Distanz ist ungültig."),
    SHIP_ALREADY_IN_TARGET("Kann nicht durch einen Gegner ziehen."),
    FIELD_IS_BLOCKED("Feld ist blockiert."),
    MOVE_END_ON_SANDBANK("Zug sollte bereits enden, da auf Sandbank gefahren wurde.");
}