package sc.plugin2024.actions

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.CubeDirection
import sc.plugin2024.*
import sc.plugin2024.exceptions.PushException
import sc.shared.InvalidMoveException

/** Erzeugt eine Abdraengaktion in angegebene Richtung. */
@XStreamAlias(value = "push")
data class Push(
        /** Richtung in die abgedrängt werden soll */
        @XStreamAsAttribute val direction: CubeDirection,
): Action {
    
    @Throws(InvalidMoveException::class)
    override fun perform(state: GameState, ship: Ship) {
        val nudgedShip = state.otherShip
        
        when {
            ship.movement == 0 -> throw InvalidMoveException(PushException.MOVEMENT_POINTS_EXCEEDED)
            else -> ship.movement -= 1
        }
        
        val pushResult = calculatePush(ship, nudgedShip, state)
        val shiftToField: Field = when {
            pushResult.pushFrom + ship.direction.opposite().vector == pushResult.shiftTo -> throw InvalidMoveException(PushException.BACKWARD_PUSHING_RESTRICTED)
            else -> state.board[pushResult.shiftTo] ?: throw InvalidMoveException(PushException.INVALID_FIELD_PUSH)
        }
        
        when {
            shiftToField == Field.SANDBANK -> {
                nudgedShip.speed = 1
                nudgedShip.movement = 1
            }
            
            else -> {
                nudgedShip.freeTurns += 1
                nudgedShip.position = pushResult.shiftTo
            }
        }
    }
    
    private fun calculatePush(ship: Ship, nudgedShip: Ship, state: GameState): PushResult {
        val pushFrom: CubeCoordinates = ship.position
        val pushTo: CubeCoordinates = pushFrom + direction.vector
        
        val shiftToField: Field? = state.board[pushTo]
        
        when {
            shiftToField == null -> throw InvalidMoveException(PushException.INVALID_FIELD_PUSH)
            pushFrom != nudgedShip.position -> throw InvalidMoveException(PushException.SAME_FIELD_PUSH)
            !shiftToField.isEmpty -> throw InvalidMoveException(PushException.BLOCKED_FIELD_PUSH)
            state.board[pushFrom] == Field.SANDBANK -> throw InvalidMoveException(PushException.SANDBANK_PUSH)
            
        }
        
        return PushResult(pushFrom, pushTo)
    }
    
    private data class PushResult(val pushFrom: CubeCoordinates, val shiftTo: CubeCoordinates)
    
    override fun toString(): String = "Dränge nach $direction ab"
}
