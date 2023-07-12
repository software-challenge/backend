package sc.plugin2024.actions

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamOmitField
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.CubeDirection
import sc.plugin2024.*
import sc.plugin2024.exceptions.AdvanceException
import sc.shared.InvalidMoveException
import java.util.*

@XStreamAlias(value = "advance")
data class Advance(
        /** Anzahl der Felder, die zurückgelegt werden. */
        @XStreamAsAttribute val distance: Int,
): Action {
    
    /**
     * Das Fahren auf eine Sandbank beendet den Zug
     */
    @XStreamOmitField
    var endsTurn = false
    
    @Throws(InvalidMoveException::class)
    override fun perform(state: GameState, ship: Ship) {
        validateShipMovement(ship)
        val start: CubeCoordinates = ship.position
        val direction = ship.direction
        validateDistance()
        val nextFields: LinkedList<Field> = LinkedList<Field>()
        
        when {
            distance == -1 && state.board[start.cubeToDoubledHex()] === FieldType.SANDBANK -> handleMoveFromSandbank(start, ship, state, direction, moveBackward = true)
            state.board[start.cubeToDoubledHex()] === FieldType.SANDBANK && distance == 1 -> handleMoveFromSandbank(start, ship, state, direction)
            else -> handleOtherMoves(ship, start, direction, state, nextFields)
        }
    }
    
    private fun checkDestinationForShip(target: Field, state: GameState) {
        val otherShip = state.otherTeam.pieces.first()
        if(target == otherShip.position) {
            throw InvalidMoveException(AdvanceException.SHIP_ALREADY_IN_TARGET)
        }
    }
    
    private fun validateDistance() {
        if(distance == 0 || distance > 6 || distance < -1) {
            throw InvalidMoveException(AdvanceException.INVALID_DISTANCE)
        }
    }
    
    private fun validateShipMovement(ship: Ship) {
        if(ship.movement == 0) {
            throw InvalidMoveException(AdvanceException.NO_MOVEMENT_POINTS)
        }
    }
    
    private fun handleMoveFromSandbank(start: Field, ship: Ship, state: GameState, direction: CubeDirection, moveBackward: Boolean = false) {
        var next: FieldType? = state.board.getFieldInDirection(direction, start)
        if (moveBackward) {
            next = if(next == null || next === FieldType.BLOCKED || !next.isEmpty) {
                throw InvalidMoveException(AdvanceException.BACKWARD_MOVE_NOT_POSSIBLE)
            } else {
                next
            }
        } else {
            next = next ?: throw InvalidMoveException(AdvanceException.FIELD_NOT_EXIST)
            if (!next.isEmpty) {
                throw InvalidMoveException(AdvanceException.FIELD_IS_BLOCKED)
            }
        }
        checkDestinationForShip(next, state)
        ship.movement = 0
        ship.position = next
    }
    
    private fun handleOtherMoves(ship: Ship, start: Field, direction: CubeDirection, state: GameState, nextFields: LinkedList<Field>) {
        nextFields.add(start)
        for(i in 0 until distance) {
            val next: Field? = state.board.getFieldInDirection(direction, nextFields[i])
            next?.let { nextFields.add(it) } ?: throw InvalidMoveException(AdvanceException.FIELD_NOT_EXIST)
            checkDestinationForShip(next, state)
            handleObstacles(ship, state, nextFields, i)
        }
        val target: Field = nextFields[distance]
        ship.position = target
        return
    }
    
    private fun handleObstacles(ship: Ship, state: GameState, nextFields: LinkedList<Field>, i: Int) {
        val checkField: Field = nextFields[i + 1]
        if(!checkField.isPassable() || state.otherTeam.pieces.first().position == checkField && i != distance - 1) {
            throw InvalidMoveException(AdvanceException.FIELD_IS_BLOCKED)
        }
        
        when(checkField.type) {
            FieldType.SANDBANK -> handleSandbankObstacle(ship, checkField, state, i)
            FieldType.LOG -> handleLogObstacle(ship)
            else -> ship.movement -= 1
        }
    }
    
    private fun handleSandbankObstacle(ship: Ship, checkField: Field, state: GameState, i: Int) {
        ship.speed = 1
        ship.movement = 0
        endsTurn = true
        if(i != distance - 1) {
            throw InvalidMoveException(AdvanceException.MOVE_END_ON_SANDBANK)
        }
        return
    }
    
    private fun handleLogObstacle(ship: Ship) {
        if(ship.movement <= 1) {
            throw InvalidMoveException(AdvanceException.NOT_ENOUGH_MOVEMENT_POINTS_TO_CROSS_LOG)
        }
        ship.movement -= 2
        ship.speed = 1.coerceAtLeast(ship.speed - 1)
    }
    
    override fun toString(): String = "Gehe $distance vor"
}
