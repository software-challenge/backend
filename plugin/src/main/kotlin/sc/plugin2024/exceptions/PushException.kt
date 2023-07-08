package sc.plugin2024.exceptions

import sc.shared.IMoveMistake

enum class PushException(override val message: String): IMoveMistake {
    MOVEMENT_POINTS_EXCEEDED("Keine Bewegunspunkte mehr vorhanden"),
    SAME_FIELD_PUSH("Um einen Spieler abzudrängen muss man sich auf demselben Feld wie der Spieler befinden."),
    INVALID_FIELD_PUSH("Ein Spieler darf nicht auf ein nicht vorhandenes (oder nicht sichtbares) Feld abgedrängt werden."),
    BLOCKED_FIELD_PUSH("Ein Spieler darf nicht auf ein blockiertes Feld abgedrängt werden."),
    SANDBANK_PUSH("Von einer Sandbank ist abdrängen nicht möglich."),
    BACKWARD_PUSHING_RESTRICTED("Ein Spieler darf nicht hinter sich, also auf das zu ihm benachbarte Feld entgegen seiner Bewegungsrichtung, abdrängen.");
    
    override fun toString() = message
}