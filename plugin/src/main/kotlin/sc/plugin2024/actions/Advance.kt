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
        if(ship.movement == 0) {
            throw InvalidMoveException(AdvanceException.NO_MOVEMENT_POINTS)
        }
        val start: CubeCoordinates = ship.position
        val direction = ship.direction
        if(distance == 0 || distance > 6 || distance < -1) {
            throw InvalidMoveException(AdvanceException.INVALID_DISTANCE)
        }
        val nextFields: LinkedList<CubeCoordinates> = LinkedList<CubeCoordinates>()
        
        when {
            distance == -1 && state.board[start] === FieldType.SANDBANK -> handleMoveFromSandbank(start, ship, state, direction, moveBackward = true)
            state.board[start] === FieldType.SANDBANK && distance == 1 -> handleMoveFromSandbank(start, ship, state, direction)
            else -> handleOtherMoves(ship, start, direction, state, nextFields)
        }
    }
    
    private fun checkDestinationForShip(target: CubeCoordinates, state: GameState) {
        val otherShip = state.otherShip
        if(target == otherShip.position) {
            throw InvalidMoveException(AdvanceException.SHIP_ALREADY_IN_TARGET)
        }
    }
    
    private fun handleMoveFromSandbank(start: CubeCoordinates, ship: Ship, state: GameState, direction: CubeDirection, moveBackward: Boolean = false) {
        var next: CubeCoordinates = start + direction.vector
        val nextFieldType: FieldType = state.board[next] ?: throw InvalidMoveException(AdvanceException.FIELD_NOT_EXIST)
        if(moveBackward) {
            next = if(nextFieldType === FieldType.BLOCKED || !nextFieldType.isEmpty) {
                throw InvalidMoveException(AdvanceException.BACKWARD_MOVE_NOT_POSSIBLE)
            } else {
                next
            }
        } else {
            if(!nextFieldType.isEmpty) {
                throw InvalidMoveException(AdvanceException.FIELD_IS_BLOCKED)
            }
        }
        checkDestinationForShip(next, state)
        ship.movement = 0
        ship.position = next
    }
    
    private fun handleOtherMoves(ship: Ship, start: CubeCoordinates, direction: CubeDirection, state: GameState, nextFields: LinkedList<CubeCoordinates>) {
        nextFields.add(start)
        for(i in 0 until distance) {
            val neighbour: CubeCoordinates = nextFields[i] + direction.vector
            if(state.board[neighbour] != null) {
                nextFields.add(neighbour)
                checkDestinationForShip(neighbour, state)
                handleObstacles(ship, state, nextFields, i)
            } else {
                throw InvalidMoveException(AdvanceException.FIELD_NOT_EXIST)
            }
        }
        val target: CubeCoordinates = nextFields[distance]
        ship.position = target
        return
    }
    
    private fun handleObstacles(ship: Ship, state: GameState, nextFields: LinkedList<CubeCoordinates>, i: Int) {
        val checkField: CubeCoordinates = nextFields[i + 1]
        val checkedFieldType: FieldType? = state.board[checkField]
        if(checkedFieldType == null || !checkedFieldType.isEmpty || state.otherShip.position == checkField && i != distance - 1) {
            throw InvalidMoveException(AdvanceException.FIELD_IS_BLOCKED)
        }
        
        when(state.board[checkField]) {
            FieldType.SANDBANK -> {
                ship.speed = 1
                ship.movement = 0
                endsTurn = true
                if(i != distance - 1) {
                    throw InvalidMoveException(AdvanceException.MOVE_END_ON_SANDBANK)
                }
                return
            }

            else -> ship.movement -= 1
        }
    }
    
    override fun toString(): String = "Gehe $distance vor"
}
