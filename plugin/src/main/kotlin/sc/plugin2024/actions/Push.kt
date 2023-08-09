package sc.plugin2024.actions

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.CubeDirection
import sc.plugin2024.*
import sc.plugin2024.exceptions.PushException
import sc.shared.InvalidMoveException

@XStreamAlias(value = "push")
/**
 * Stellt eine [Push]-[Action] im Spiel dar.
 *
 * 1. Ein [Ship] kann nur einen anderes [Ship] abdrängen, wenn er noch [Ship.movement]-Punkte hat.
 * 2. Das verschiebende [Ship] und das zu verschiebende [Ship] müssen sich im [Field] mit den gleichen [CubeCoordinates] befinden.
 * 3. Ein [Ship] kann nicht auf ein nicht vorhandenes oder blockiertes [Field] abgedrängt werden.
 * 4. Ein Spieler kann nicht von einem [Field.SANDBANK] aus abgedrängt werden.
 * 5. Das Abdrängen kostet dem verschiebenden [Ship] einen [Ship.movement].
 * 7. Ein [Ship] kann nicht in die entgegengesetzte Richtung seiner [Ship.direction] abgedrängt werden.
 * 8. Wenn ein [Ship] auf eine [Field.SANDBANK] abgedrängt wird, werden seine [Ship.speed] und [Ship.movement] auf eins gesetzt.
 * 9. Nach dem Abdrängen wird die [Ship.position] des zu verschiebenden Spielers auf dem Spielbrett geändert.
 * 10. Das abgedrängte [Ship] bekommt eine zusätzliche [Ship.freeTurns] für seinen nächsten Zug.
 * 11. Wurde der Spieler auf eine [Field.SANDBANK] abgedrängt, entfällt diese [Ship.freeTurns].
 *
 * @property direction Die [CubeDirection], in der der [Push] ausgeführt werden soll.
 * @constructor Erzeugt ein neues [Push]-Objekt.
 */
data class Push(
        /** Richtung in die abgedrängt werden soll */
        @XStreamAsAttribute val direction: CubeDirection,
): Action {
    
    @Throws(InvalidMoveException::class)
    override fun perform(state: GameState, ship: Ship) {
        val nudgedShip = state.otherShip
        
        if(ship.movement == 0)
            throw InvalidMoveException(PushException.MOVEMENT_POINTS_EXCEEDED)
        ship.movement--
        
        val pushFrom: CubeCoordinates = ship.position
        val pushTo: CubeCoordinates = pushFrom + direction.vector
        val shiftToField: Field? = state.board[pushTo]
        when {
            shiftToField == null -> throw InvalidMoveException(PushException.INVALID_FIELD_PUSH)
            !shiftToField.isEmpty -> throw InvalidMoveException(PushException.BLOCKED_FIELD_PUSH)
            pushFrom != nudgedShip.position -> throw InvalidMoveException(PushException.SAME_FIELD_PUSH)
            state.board[pushFrom] == Field.SANDBANK -> throw InvalidMoveException(PushException.SANDBANK_PUSH)
            direction == ship.direction.opposite() -> throw InvalidMoveException(PushException.BACKWARD_PUSHING_RESTRICTED)
        }
        
        if(shiftToField == Field.SANDBANK) {
            nudgedShip.speed = 1
            nudgedShip.movement = 1
        }
        nudgedShip.position = pushTo
        nudgedShip.freeTurns++
    }
    
    override fun toString(): String = "Dränge nach $direction ab"
}
