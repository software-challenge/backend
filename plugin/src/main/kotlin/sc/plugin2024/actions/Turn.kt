package sc.plugin2024.actions

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.HexDirection
import sc.plugin2024.*
import sc.plugin2024.exceptions.TurnException
import sc.shared.InvalidMoveException
import kotlin.math.absoluteValue

@XStreamAlias(value = "turn")
data class Turn(
        @XStreamAsAttribute val direction: HexDirection,
): Action {
    
    @Throws(InvalidMoveException::class)
    override fun perform(state: GameState, ship: Ship) {
        val turnCount = ship.direction.turnCountTo(direction)
        require(!(turnCount == 0 || turnCount < -3 || turnCount > 3)) {
            throw InvalidMoveException(TurnException.INVALID_ROTATION)
        }
        
        requireNotNull(ship.position) {
            throw InvalidMoveException(TurnException.ROTATION_ON_SANDBANK_NOT_ALLOWED)
        }.takeIf { it.type == FieldType.SANDBANK }
        
        val absTurnCount = turnCount.absoluteValue
        val usedCoal: Int = absTurnCount - ship.freeTurns
        
        ship.freeTurns = maxOf(ship.freeTurns - absTurnCount, 0)
        
        require(ship.coal >= usedCoal) {
            throw InvalidMoveException(TurnException.NOT_ENOUGH_COAL_FOR_ROTATION)
        }
        usedCoal.takeIf { it > 0 }?.let { ship.coal -= it }
        
        ship.direction = direction
    }
    
    override fun toString(): String = "Drehe nach $direction"
}