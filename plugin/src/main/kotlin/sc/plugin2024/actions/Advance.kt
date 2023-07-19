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
        
        handleMoves(ship, start, direction, state, nextFields)
    }
    
    private fun handleMoves(
            ship: Ship,
            start: CubeCoordinates,
            direction: CubeDirection,
            state: GameState,
            nextFields: LinkedList<CubeCoordinates>,
    ) {
        nextFields.add(start)
        val iterations = if(distance == -1) 1 else distance
        for(i in 0 until iterations) {
            val vector = if(distance == -1) -direction.vector else direction.vector
            val neighbour: CubeCoordinates = nextFields[i] + vector
            val nextField = state.board[neighbour]
            
            when {
                nextField == null -> throw InvalidMoveException(AdvanceException.FIELD_NOT_EXIST)
                !nextField.isEmpty -> throw InvalidMoveException(AdvanceException.FIELD_IS_BLOCKED)
                distance == -1 && state.board[start] !== Field.SANDBANK -> throw InvalidMoveException(AdvanceException.BACKWARD_MOVE_NOT_POSSIBLE)
                distance > 1 && state.board[start] == Field.SANDBANK -> throw InvalidMoveException(AdvanceException.ONLY_ONE_MOVE_ALLOWED_ON_SANDBANK)
                i != distance - 1 && nextField == Field.SANDBANK -> throw InvalidMoveException(AdvanceException.MOVE_END_ON_SANDBANK)
                i != distance - 1 && neighbour == state.otherShip.position -> throw InvalidMoveException(AdvanceException.SHIP_ALREADY_IN_TARGET)
            }
            
            when {
                nextField == Field.SANDBANK -> {
                    ship.speed = 1
                    ship.movement = 0
                    endsTurn = true
                }
                
                distance == -1 -> {
                    ship.movement = 0
                    endsTurn = true
                }
                
                ship.movement - 1 == 0 -> {
                    ship.movement = 0
                    endsTurn = true
                }
                
                else -> ship.movement -= 1
            }
            
            nextFields.add(neighbour)
        }
        val target: CubeCoordinates = nextFields.last()
        ship.position = target
    }
    
    override fun toString(): String = if(distance >= 0) "Gehe $distance Felder vor" else "Gehe $distance Felder zurück"
}
