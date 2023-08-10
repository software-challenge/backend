package sc.plugin2024.actions

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.CubeDirection
import sc.plugin2024.Action
import sc.plugin2024.Field
import sc.plugin2024.GameState
import sc.plugin2024.Ship
import sc.plugin2024.mistake.TurnProblem
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
    
    override fun perform(state: GameState): TurnProblem? {
        val turnCount = state.currentShip.direction.turnCountTo(direction)
        
        val absTurnCount = turnCount.absoluteValue
        val usedCoal: Int = absTurnCount - state.currentShip.freeTurns
        
        state.currentShip.freeTurns = maxOf(state.currentShip.freeTurns - absTurnCount, 0)
        
        when {
            state.board[state.currentShip.position] == Field.SANDBANK -> return TurnProblem.ROTATION_ON_SANDBANK_NOT_ALLOWED
            state.currentShip.coal < usedCoal -> return TurnProblem.NOT_ENOUGH_COAL_FOR_ROTATION
            usedCoal > 0 -> state.currentShip.coal -= usedCoal
        }
        
        state.currentShip.direction = direction
        return null
    }
    
    fun coalCost(ship: Ship) =
            ship.direction.turnCountTo(direction).absoluteValue - ship.freeTurns
    
    override fun toString(): String = "Drehe nach $direction"
}