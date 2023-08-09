package sc.plugin2024.actions

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.CubeDirection
import sc.plugin2024.*
import sc.plugin2024.exceptions.AdvanceException
import sc.shared.InvalidMoveException
import java.util.*
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
): Action {
    
    @Throws(InvalidMoveException::class)
    override fun perform(state: GameState, ship: Ship) {
        if(distance < 0 && state.board[ship.position] != Field.SANDBANK || distance > 6)
            throw InvalidMoveException(AdvanceException.INVALID_DISTANCE)
        
        val result = state.checkAdvanceLimit(ship.position, if(distance > 0) ship.direction else ship.direction.opposite(), ship.movement)
        if(result.distance < distance.absoluteValue)
            throw InvalidMoveException(result.problem)
        
        result.extraCost.clear(distance + 1, 999)
        ship.position += ship.direction.vector * distance
        ship.movement -= result.costUntil(distance)
        // TODO test this oof
    }
    
    override fun toString(): String = if(distance >= 0) "Gehe $distance Felder vor" else "Gehe $distance Felder zurück"
}
