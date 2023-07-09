package sc.plugin2024.actions

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamOmitField
import sc.api.plugins.HexDirection
import sc.plugin2024.*
import sc.plugin2024.exceptions.AdvanceException
import sc.plugin2024.exceptions.MoveException
import sc.shared.InvalidMoveException
import java.util.*

@XStreamAlias(value = "advance")
data class Advance(
        @XStreamAsAttribute override var order: Int,
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
        val start: Field = ship.position
        val direction = ship.direction
        validateDistance()
        val nextFields: LinkedList<Field> = LinkedList<Field>()
        
        when {
            isBackwardsMoveFromSandbank(start, direction, state) -> handleBackwardsMoveFromSandbank(start, ship, state, direction)
            isMoveFromSandbank(start, direction, state) -> handleMoveFromSandbank(ship, start, direction, state)
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
    
    private fun isMoveFromSandbank(start: Field, direction: HexDirection, state: GameState): Boolean {
        return start.type === FieldType.SANDBANK && distance == 1
    }
    
    private fun handleMoveFromSandbank(ship: Ship, start: Field, direction: HexDirection, state: GameState) {
        ship.movement = 0
        val next: Field = state.board.getFieldInDirection(direction, start)
                          ?: throw InvalidMoveException(AdvanceException.FIELD_NOT_EXIST)
        checkDestinationForShip(next, state)
        if(!next.isPassable()) {
            throw InvalidMoveException(AdvanceException.FIELD_IS_BLOCKED)
        }
        ship.position = next
        return
    }
    
    private fun isBackwardsMoveFromSandbank(start: Field, direction: HexDirection, state: GameState): Boolean {
        return distance == -1 && start.type === FieldType.SANDBANK
    }
    
    private fun handleBackwardsMoveFromSandbank(start: Field, ship: Ship, state: GameState, direction: HexDirection) {
        val next: Field? = state.board.getFieldInDirection(direction, start)
        if(next == null || next.type === FieldType.LOG || !next.isBlocked) {
            throw InvalidMoveException(AdvanceException.BACKWARD_MOVE_NOT_POSSIBLE)
        }
        checkDestinationForShip(next, state)
        ship.movement = 0
        ship.position = next
        return
    }
    
    private fun handleOtherMoves(ship: Ship, start: Field, direction: HexDirection, state: GameState, nextFields: LinkedList<Field>) {
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
