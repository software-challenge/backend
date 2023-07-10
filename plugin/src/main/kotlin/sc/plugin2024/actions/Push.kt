package sc.plugin2024.actions

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.HexDirection
import sc.plugin2024.*
import sc.plugin2024.exceptions.PushException
import sc.shared.InvalidMoveException

/** Erzeugt eine Abdraengaktion in angegebene Richtung. */
@XStreamAlias(value = "push")
data class Push(
        /** Richtung in die abgedrängt werden soll */
        @XStreamAsAttribute val direction: HexDirection,
): Action {
    
    @Throws(InvalidMoveException::class)
    override fun perform(state: GameState, ship: Ship) {
        val nudgedShip = state.otherTeam.pieces.first() as Ship
        ensureMovementAvailable(ship)
        val pushResult = calculatePush(ship, nudgedShip, state)
        if(pushResult.shiftTo.type === FieldType.LOG) {
            adjustForLogPush(nudgedShip, ship)
        }
        checkBackwardPushingRestricted(pushResult.pushFrom, pushResult.shiftTo, ship, state)
        if(pushResult.shiftTo.type === FieldType.SANDBANK) {
            adjustForSandbankPush(nudgedShip)
        }
        
        nudgedShip.freeTurns += 1
        nudgedShip.position = pushResult.shiftTo
    }
    
    private fun ensureMovementAvailable(ship: Ship) {
        if(ship.movement == 0) {
            throw InvalidMoveException(PushException.MOVEMENT_POINTS_EXCEEDED)
        }
        ship.movement -= 1
    }
    
    private fun calculatePush(ship: Ship, nudgedShip: Ship, state: GameState): PushResult {
        val pushFrom: Field = ship.position
        val pushTo: Field = getPushTo(state, ship, pushFrom)
        checkValidPush(pushFrom, nudgedShip, pushTo)
        return PushResult(pushFrom, pushTo)
    }
    
    private fun getPushTo(state: GameState, ship: Ship, pushFrom: Field): Field {
        return state.board.getFieldInDirection(direction, pushFrom)
               ?: throw InvalidMoveException(PushException.INVALID_FIELD_PUSH)
    }
    
    private fun checkValidPush(pushFrom: Field, nudgedShip: Ship, pushTo: Field) {
        if(pushFrom != nudgedShip.position) {
            throw InvalidMoveException(PushException.SAME_FIELD_PUSH)
        }
        if(pushTo.isBlocked) {
            throw InvalidMoveException(PushException.BLOCKED_FIELD_PUSH)
        }
        if(pushFrom.type === FieldType.SANDBANK) {
            throw InvalidMoveException(PushException.SANDBANK_PUSH)
        }
    }
    
    private fun adjustForLogPush(nudgedShip: Ship, ship: Ship) {
        nudgedShip.speed = 1.coerceAtLeast(nudgedShip.speed - 1)
        nudgedShip.movement -= 1
        // pushing onto logs costs one more movement
        ship.movement -= 1
    }
    
    private fun checkBackwardPushingRestricted(pushFrom: Field, pushTo: Field, ship: Ship, state: GameState) {
        val fieldBehindPushingPlayer: Field = state.board.getFieldInDirection(ship.direction.opposite(), pushFrom)!!
        if(fieldBehindPushingPlayer == pushTo) {
            throw InvalidMoveException(PushException.BACKWARD_PUSHING_RESTRICTED)
        }
    }
    
    private fun adjustForSandbankPush(nudgedShip: Ship) {
        nudgedShip.speed = 1
        nudgedShip.movement = 1
    }
    
    private data class PushResult(val pushFrom: Field, val shiftTo: Field)
    
    override fun toString(): String = "Dränge nach $direction ab"
}
