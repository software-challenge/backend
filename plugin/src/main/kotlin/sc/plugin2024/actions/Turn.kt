package sc.plugin2024.actions

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.CubeDirection
import sc.plugin2024.*
import sc.plugin2024.exceptions.TurnException
import sc.shared.InvalidMoveException
import kotlin.math.absoluteValue

/**
 * Stellt eine [Turn]-[Action] dar, die von einem [Ship] im Spiel durchgeführt werden kann.
 *
 * 1. Wenn das [Ship] sich auf [Field.SANDBANK] befindet, ist die [Turn]-[Action] nicht erlaubt.
 * 2. Die benötigte Kohle für den [Turn] berechnet sich aus dem absoluten Wert der [direction] minus der [Ship.direction],
 * minus den verfügbaren [Ship.freeTurns] des [Ship].
 * 4. Nach erfolgreichem [Turn] wird die [Ship.direction] auf die neue Richtung aktualisiert.
 *
 * @property direction Die [CubeDirection], in die das Schiff gedreht werden soll.
 * @constructor Erzeugt eine neue Instanz der Klasse [Turn].
 * @see [CubeDirection.turnCountTo]
 */
@XStreamAlias(value = "turn")
data class Turn(
        @XStreamAsAttribute val direction: CubeDirection,
): Action {
    
    @Throws(InvalidMoveException::class)
    override fun perform(state: GameState, ship: Ship) {
        val turnCount = ship.direction.turnCountTo(direction)
        
        val absTurnCount = turnCount.absoluteValue
        val usedCoal: Int = absTurnCount - ship.freeTurns
        
        ship.freeTurns = maxOf(ship.freeTurns - absTurnCount, 0)
        
        when {
            state.board[ship.position] == null -> throw InvalidMoveException(TurnException.ROTATION_ON_NON_EXISTING_FIELD)
            state.board[ship.position] == Field.SANDBANK -> throw InvalidMoveException(TurnException.ROTATION_ON_SANDBANK_NOT_ALLOWED)
            ship.coal < usedCoal -> throw InvalidMoveException(TurnException.NOT_ENOUGH_COAL_FOR_ROTATION)
            usedCoal > 0 -> ship.coal -= usedCoal
        }
        
        ship.direction = direction
    }
    
    fun coalCost(ship: Ship) =
            ship.direction.turnCountTo(direction).absoluteValue - ship.freeTurns
    
    override fun toString(): String = "Drehe nach $direction"
}