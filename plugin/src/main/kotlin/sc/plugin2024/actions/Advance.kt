package sc.plugin2024.actions

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.CubeDirection
import sc.plugin2024.*
import sc.plugin2024.mistake.AdvanceProblem
import sc.plugin2024.util.PluginConstants
import kotlin.math.absoluteValue

/**
 * Das Schiff soll in Fahrtrichtung vorrücken.
 *
 * 1. Eine [Advance]-[Action] ohne Bewegungspunkte ist ungültig.
 * 2. Wenn ein [Ship] sich auf ein [Field.SANDBANK] befindet, dann ist diesem [Ship] genau __ein__ Schritt vor __oder__ zurück erlaubt.
 * In jedem Fall muss das [Field.isEmpty] sein und es werden bei Erfolg alle [Ship.movement] aufgebraucht.
 * Ausschließlich bei einem Rückzug wird eine [Ship.coal] verbraucht.
 * 4. Für jedes [Field] der Distanz, die das [Ship] zurücklegen möchte, wird überprüft, ob das nächste [Field] vorhanden und [Field.isEmpty] ist
 * und ob genügend [Ship.movement] vorhanden sind, um es zu erreichen.
 * Bei Erreichen einer [Field.SANDBANK] endet der Zug des Spielers sofort.
 * 5. Das Bewegen auf das (oder durch das) [Field] des Gegners ist nur möglich, wenn es sich um das Endfeld der Bewegungsaktion handelt.
 * 6. Die [CubeDirection] kann nicht innerhalb einer [Advance]-[Action] geändert werden - das [Ship] bewegt sich immer in der aktuellen [Ship.direction].
 *
 * @property distance Die Anzahl der [Field]s, die vorzurücken sind.
 */
@XStreamAlias(value = "advance")
data class Advance(
        /** Anzahl der Felder, die zurückgelegt werden. */
        @XStreamAsAttribute val distance: Int,
): Action, Addable<Advance> {
    
    override fun perform(state: GameState): AdvanceProblem? {
        if(distance < PluginConstants.MIN_SPEED &&
           state.board[state.currentShip.position] != Field.SANDBANK ||
           distance > PluginConstants.MAX_SPEED)
            return AdvanceProblem.INVALID_DISTANCE
        if(distance > state.currentShip.movement)
            return AdvanceProblem.MOVEMENT_POINTS_MISSING
        
        val result = state.checkAdvanceLimit(state.currentShip.position, if(distance > 0) state.currentShip.direction else state.currentShip.direction.opposite(), state.currentShip.movement)
        if(result.distance < distance.absoluteValue)
            return result.problem
        
        state.currentShip.position += state.currentShip.direction.vector * distance
        state.currentShip.movement -= result.costUntil(distance)
        return null
    }
    
    override fun toString(): String = if(distance >= 0) "Gehe $distance Felder vor" else "Gehe $distance Felder zurück"
    
    override operator fun plus(other: Advance) = Advance(distance + other.distance)
}
