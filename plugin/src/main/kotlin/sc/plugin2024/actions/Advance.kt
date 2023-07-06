package sc.plugin2024.actions

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamOmitField
import org.slf4j.LoggerFactory
import sc.plugin2024.*
import sc.plugin2024.exceptions.MoveException
import sc.shared.InvalidMoveException
import java.util.*

@XStreamAlias(value = "advance")
class Advance : Action {
    /**
     * Anzahl der Felder, die zurückgelegt werden.
     */
    @XStreamAsAttribute
    var distance: Int

    /**
     * Das Fahren auf eine Sandbank beendet den Zug
     */
    @XStreamOmitField
    var endsTurn = false

    constructor() {
        distance = 0
        order = 0
    }

    /**
     * Legt eine neue Laufaktion an
     * @param distance Felder, die überwunden werden
     */
    constructor(distance: Int) {
        this.distance = distance
        endsTurn = false
    }

    /**
     * Legt eine neue Laufaktion an
     * @param distance Felder, die überwunden werden
     * @param order Nummer der Aktion. Aktionen werden aufsteigend sortiert nach
     * ihrer Nummer ausgeführt.
     */
    constructor(distance: Int, order: Int) {
        this.distance = distance
        super.order = order
        endsTurn = false
    }

    @Throws(InvalidMoveException::class)
    override fun perform(state: GameState?, player: Ship?) {
        if (player!!.movement == 0) {
            throw InvalidMoveException(MoveException.NO_MOVEMENT)
        }
        val start: Field = player.getField(state!!.board)!!
        val nextFields: LinkedList<Field> = LinkedList<Field>()
        val direction: Direction = player.direction!!
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
            state.put(next.x, next.y, player)
            player.setMovement(0)
            player.setCoal(player.getCoal() - 1)
            return
        } else {
            if (start.getType() === FieldType.SANDBANK) {
                if (distance != 1) {
                    throw InvalidMoveException(MoveException.ONE_FORWARD)
                }
                player.setMovement(0)
                val next: Field = start.getFieldInDirection(direction, state.getBoard())
                    ?: throw InvalidMoveException(MoveException.FIELD_NOT_FOUND)
                if (!next.isPassable()) {
                    throw InvalidMoveException(MoveException.BLOCKED)
                }
                state.put(next.getX(), next.getY(), player)
                return
            }
            nextFields.add(start)
            // Kontrolliere für die Zurückgelegte Distanz, wie viele Bewegunsgpunkte verbraucht werden und ob es möglich ist, soweit zu ziehen
            for (i in 0 until distance) {
                val next: Field? = nextFields[i].getFieldInDirection(player.getDirection(), state.getBoard())
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
                    player.setSpeed(1)
                    player.setMovement(0)
                    endsTurn = true
                    if (i != distance - 1) {
                        // Zug endet hier, also darf nicht weitergelaufen werden
                        throw InvalidMoveException(MoveException.MOVE_ON_SANDBANK)
                    }
                    state.put(checkField.getX(), checkField.getY(), player)
                    return
                } else if (checkField.getType() === FieldType.LOG) {
                    if (player.getMovement() <= 1) {
                        throw InvalidMoveException(MoveException.INSUFFICIENT_MOVEMENT)
                    }
                    player.setMovement(player.getMovement() - 2)
                    player.setSpeed(Math.max(1, player.getSpeed() - 1))
                } else {
                    player.setMovement(player.getMovement() - 1)
                }
            }
            val target: Field = nextFields[distance]
            state.put(target.getX(), target.getY(), player)
        }
        return
    }

    fun clone(): Advance {
        return Advance(distance, this.order)
    }

    override fun equals(o: Any?): Boolean {
        return if (o is Advance) {
            distance == o.distance
        } else false
    }

    override fun toString(): String {
        return "Gehe $distance vor"
    }

    override fun hashCode(): Int {
        var result = distance
        result = 31 * result + endsTurn.hashCode()
        return result
    }

    companion object {
        private val logger = LoggerFactory.getLogger(Advance::class.java)
    }
}
