package sc.plugin2024.actions

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.HexDirection
import sc.plugin2024.*
import sc.plugin2024.exceptions.MoveException
import sc.shared.InvalidMoveException
import kotlin.math.abs

@XStreamAlias(value = "turn")
data class Turn(
        @XStreamAsAttribute
        var direction: HexDirection,
): Action {
    
    @Throws(InvalidMoveException::class)
    override fun perform(state: GameState, ship: Ship) {
        val turnCount = ship.direction.turnCountTo(direction)
        require(!(turnCount == 0 || turnCount < -3 || turnCount > 3)) {
            throw InvalidMoveException(MoveException.INVALID_TURN)
        }
        
        requireNotNull(ship.position) {
            throw InvalidMoveException(MoveException.SANDBANK)
        }.takeIf { it.type == FieldType.SANDBANK }
        
        val absTurnCount = abs(turnCount.toDouble())
        val usedCoal: Int = (absTurnCount - ship.freeTurns).toInt()
        
        ship.freeTurns = if(ship.freeTurns - absTurnCount <= 0) 0 else 1
        
        if(usedCoal > 0) {
            require(ship.coal >= usedCoal) {
                throw InvalidMoveException(MoveException.COAL)
            }
            ship.coal -= usedCoal
        }
        ship.direction = direction
    }
    
    override fun toString(): String = "Drehe nach $direction"
}