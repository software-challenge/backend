package sc.plugin2024.actions

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.HexDirection
import sc.api.plugins.Vector
import sc.framework.plugins.Player
import sc.plugin2024.*
import sc.shared.InvalidMoveException

/** Erzeugt eine Abdrängaktion in angegebene Richtung. */
@XStreamAlias(value = "push")
data class Push(
        /** Richtung in die abgedrängt werden soll */
        @XStreamAsAttribute val direction: HexDirection
) : Action {

    @Throws(InvalidMoveException::class)
    override fun perform(state: GameState, ship: Ship) {
        val nudgedPlayer = state.otherTeam // The player who is being pushed (using different verb to make distinction easier).
        if (pushingPlayer.getMovement() === 0) {
            throw InvalidMoveException("Keine Bewegunspunkte mehr vorhanden")
        }
        val pushFrom: Field = pushingPlayer.getField(state.getBoard())
        val pushTo: Field = pushFrom.alwaysGetFieldInDirection(direction, state.getBoard())
        if (!pushFrom.equals(nudgedPlayer.getField(state.getBoard()))) {
            throw InvalidMoveException("Um einen Spieler abzudrängen muss man sich auf demselben Feld wie der Spieler befinden.")
        }
        if (pushTo == null) {
            throw InvalidMoveException("Ein Spieler darf nicht auf ein nicht vorhandenes (oder nicht sichtbares) Feld abgedrängt werden.")
        }
        if (pushTo.isBlocked()) {
            throw InvalidMoveException("Ein Spieler darf nicht auf ein blockiertes Feld abgedrängt werden.")
        }
        if (pushFrom.getType() === FieldType.SANDBANK) {
            throw InvalidMoveException("Von einer Sandbank ist abdrängen nicht möglich.")
        }
        // pushing costs 1 movement point
        pushingPlayer.setMovement(pushingPlayer.getMovement() - 1)
        if (pushTo.getType() === FieldType.LOG) {
            // driving through logs reduces speed and movement by +1
            nudgedPlayer.setSpeed(Math.max(1, nudgedPlayer.getSpeed() - 1))
            nudgedPlayer.setMovement(nudgedPlayer.getMovement() - 1)
            // pushing onto logs costs one more movement
            pushingPlayer.setMovement(pushingPlayer.getMovement() - 1)
        }
        val fieldBehindPushingPlayer: Field =
            pushFrom.getFieldInDirection(pushingPlayer.getDirection().getOpposite(), state.getBoard())
        // If fieldBehindPushedPlayer is null, the following check is not needed
        // because pushTo cannot be that field (and pushTo cannot be null as already
        // checked above).
        if (fieldBehindPushingPlayer != null && fieldBehindPushingPlayer.equals(pushTo)) {
            throw InvalidMoveException("Ein Spieler darf nicht hinter sich, also auf das zu ihm benachbarte Feld entgegen seiner Bewegungsrichtung, abdrängen.")
        }
        if (pushTo.getType() === FieldType.SANDBANK) {
            nudgedPlayer.setSpeed(1)
            nudgedPlayer.setMovement(1)
        }
        // change Position of opponent player
        state.put(pushTo.getX(), pushTo.getY(), nudgedPlayer)
        return
    }

    override fun toString(): String = "Dränge nach $direction ab"
}
