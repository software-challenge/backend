package sc.plugin2024.actions

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.plugin2024.Action
import sc.plugin2024.Field
import sc.plugin2024.GameState
import sc.plugin2024.Ship
import sc.plugin2024.Move
import sc.plugin2024.exceptions.AccException
import sc.shared.InvalidMoveException
import kotlin.math.abs

/**
 * Stellt eine [Acceleration]-[Action] dar, die im Spiel ausgeführt werden kann.
 *
 * 1. Eine [Acceleration] kann nur als erste [Action] eines [Move] ausgeführt werden.
 * 2. Die [Acceleration] um eine Geschwindigkeitseinheit pro [Move] ist frei, jede weitere kostet Kohle, die vom Kohlevorrat des [Ship] abgezogen wird.
 * 3. Die [Acceleration] gibt an, um wie viel die [Ship.movement] eines Spielers geändert wird.
 * 4. Eine [Acceleration] von 0 gesetzt ist nicht im Spiel erlaubt.
 * 5. Die [Ship.movement] eines [Ship] kann nicht höher als 6 sein.
 * 6. Die [Ship.movement] eines [Ship] kann nicht niedriger als 1 sein.
 * 7. Ein [Ship] kann nicht beschleunigen, während er sich an einer [Field.SANDBANK] befindet.
 * 9. Nachdem die [Action] abgeschlossen ist, wird die [Ship.movement] des Spielers aktualisiert und die [Ship.freeAcc] des [Ship] auf 0 zurückgesetzt.
 *
 * @property acc Das Ausmaß der [Acceleration].
 *               Ein negativer Wert bedeutet Abbremsen.
 *               Der Wert für die Beschleunigung darf nicht 0 sein.
 *
 * @constructor Erzeugt eine Instanz der Klasse [Acceleration].
 */
@XStreamAlias(value = "acceleration")
data class Acceleration(
        /**
         * Gibt an, um wie viel beschleunigt wird. Negative Zahl bedeutet, dass entsprechend gebremst wird.
         * Darf nicht 0 sein.
         */
        @XStreamAsAttribute val acc: Int,
): Action {
    
    /**
     *
     * @param state [GameState], auf dem die Beschleunigung ausgeführt wird
     * @param ship [Ship], für welches die Beschleunigung ausgeführt wird
     */
    @Throws(InvalidMoveException::class)
    override fun perform(state: GameState, ship: Ship) {
        var speed: Int = ship.speed
        speed += acc
        when {
            acc == 0 -> throw InvalidMoveException(AccException.ZERO_ACC)
            speed > 6 -> throw InvalidMoveException(AccException.ABOVE_MAX_SPEED)
            speed < 1 -> throw InvalidMoveException(AccException.BELOW_MIN_SPEED)
            state.board[ship.position] == Field.SANDBANK -> throw InvalidMoveException(AccException.ON_SANDBANK)
        }
        
        val usedCoal: Int = (abs(acc.toDouble()) - ship.freeAcc).toInt()
        if(ship.coal < usedCoal) throw InvalidMoveException(AccException.INSUFFICIENT_COAL)
        usedCoal.takeIf { it > 0 }?.let { ship.coal -= it }
        ship.speed = speed
        ship.movement += acc
        ship.freeAcc = 0
        return
    }
    
    override fun toString(): String = "Beschleunige um $acc"
}
