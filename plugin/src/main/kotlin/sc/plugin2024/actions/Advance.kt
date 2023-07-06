package sc.plugin2024.actions

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.plugin2024.*
import sc.plugin2024.exceptions.MoveException
import sc.shared.InvalidMoveException
import java.util.*

@XStreamAlias(value = "advance")
data class Advance(
        /** Anzahl der Felder, die zurückgelegt werden. */
        @XStreamAsAttribute
        val distance: Int
) : Action {

    @Throws(InvalidMoveException::class)
    override fun perform(state: GameState, ship: Ship) {
        if (ship.movement == 0) {
            throw InvalidMoveException(MoveException.NO_MOVEMENT)
        }
        val start: Field = ship.getField(state.board)!!
        val nextFields: LinkedList<Field> = LinkedList<Field>()
        val direction = ship.direction
        if (distance == 0 || distance > 6 || distance < -1) {
            throw InvalidMoveException(MoveException.INVALID_DISTANCE)
        }
        if (distance == -1) { // Fall rückwärts von Sandbank
            if (start.type !== FieldType.SANDBANK) {
                throw InvalidMoveException(MoveException.BACKWARDS)
            }
            val next: Field? = start.getFieldInDirection(direction.opposite, state.board)
            if (next == null || next.type === FieldType.LOG || !next.isOccupied) {
                throw InvalidMoveException(MoveException.BLOCKED)
            }
            state.put(next.x, next.y, ship)
            ship.setMovement(0)
            ship.setCoal(ship.getCoal() - 1)
            return
        } else {
            if (start.getType() === FieldType.SANDBANK) {
                if (distance != 1) {
                    throw InvalidMoveException(MoveException.ONE_FORWARD)
                }
                ship.setMovement(0)
                val next: Field = start.getFieldInDirection(direction, state.getBoard())
                    ?: throw InvalidMoveException(MoveException.FIELD_NOT_FOUND)
                if (!next.isPassable()) {
                    throw InvalidMoveException(MoveException.BLOCKED)
                }
                state.put(next.getX(), next.getY(), ship)
                return
            }
            nextFields.add(start)
            // Kontrolliere für die Zurückgelegte Distanz, wie viele Bewegunsgpunkte verbraucht werden und ob es möglich ist, soweit zu ziehen
            for (i in 0 until distance) {
                val next: Field? = nextFields[i].getFieldInDirection(ship.getDirection(), state.getBoard())
                if (next != null) {
                    nextFields.add(next)
                } else {
                    throw InvalidMoveException(MoveException.FIELD_NOT_FOUND)
                }
                val checkField: Field = nextFields[i + 1] // get next field
                if (!checkField.isPassable() || state.getOtherPlayer().getField(state.getBoard())
                        .equals(checkField) && i != distance - 1
                ) {
                    throw InvalidMoveException(MoveException.BLOCKED)
                }
                if (checkField.getType() === FieldType.SANDBANK) {
                    // case sandbar
                    ship.setSpeed(1)
                    ship.setMovement(0)
                    endsTurn = true
                    if (i != distance - 1) {
                        // Zug endet hier, also darf nicht weitergelaufen werden
                        throw InvalidMoveException(MoveException.MOVE_ON_SANDBANK)
                    }
                    state.put(checkField.getX(), checkField.getY(), ship)
                    return
                } else if (checkField.getType() === FieldType.LOG) {
                    if (ship.getMovement() <= 1) {
                        throw InvalidMoveException(MoveException.INSUFFICIENT_MOVEMENT)
                    }
                    ship.setMovement(ship.getMovement() - 2)
                    ship.setSpeed(Math.max(1, ship.getSpeed() - 1))
                } else {
                    ship.setMovement(ship.getMovement() - 1)
                }
            }
            val target: Field = nextFields[distance]
            state.put(target.getX(), target.getY(), ship)
        }
        return
    }

    override fun toString(): String = "Gehe $distance vor"
}
