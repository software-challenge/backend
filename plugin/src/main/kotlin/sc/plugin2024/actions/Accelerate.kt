package sc.plugin2024.actions

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.plugin2024.*
import sc.plugin2024.mistake.AccelerationProblem
import kotlin.math.abs

/**
 * Stellt eine [Accelerate]-[Action] dar, die im Spiel ausgeführt werden kann.
 *
 * 1. Eine [Accelerate] kann nur als erste [Action] eines [Move] ausgeführt werden.
 * 2. Die [Accelerate] um eine Geschwindigkeitseinheit pro [Move] ist frei, jede weitere kostet Kohle, die vom Kohlevorrat des [Ship] abgezogen wird.
 * 3. Die [Accelerate] gibt an, um wie viel die [Ship.movement] eines Spielers geändert wird.
 * 4. Eine [Accelerate] von 0 gesetzt ist nicht im Spiel erlaubt.
 * 5. Die [Ship.movement] eines [Ship] kann nicht höher als 6 sein.
 * 6. Die [Ship.movement] eines [Ship] kann nicht niedriger als 1 sein.
 * 7. Ein [Ship] kann nicht beschleunigen, während er sich an einer [Field.SANDBANK] befindet.
 * 9. Nachdem die [Action] abgeschlossen ist, wird die [Ship.movement] des Spielers aktualisiert und die [Ship.freeAcc] des [Ship] auf 0 zurückgesetzt.
 *
 * @property acc Das Ausmaß der [Accelerate].
 *               Ein negativer Wert bedeutet Abbremsen.
 *               Der Wert für die Beschleunigung darf nicht 0 sein.
 *
 * @constructor Erzeugt eine Instanz der Klasse [Accelerate].
 */
@XStreamAlias(value = "acceleration")
data class Accelerate(
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
    override fun perform(state: GameState, movementCheck: Boolean): AccelerationProblem? {
        var speed: Int = state.currentShip.speed
        speed += acc
        when {
            acc == 0 -> return AccelerationProblem.ZERO_ACC
            speed > 6 -> return AccelerationProblem.ABOVE_MAX_SPEED
            speed < 1 -> return AccelerationProblem.BELOW_MIN_SPEED
            state.board[state.currentShip.position] == Field.SANDBANK -> return AccelerationProblem.ON_SANDBANK
        }
        
        val usedCoal: Int = (abs(acc.toDouble()) - state.currentShip.freeAcc).toInt()
        if(state.currentShip.coal < usedCoal) return AccelerationProblem.INSUFFICIENT_COAL
        usedCoal.takeIf { it > 0 }?.let { state.currentShip.coal -= it }
        state.currentShip.speed = speed
        state.currentShip.movement += acc
        state.currentShip.freeAcc = 0
        return null
    }
    
    override fun toString(): String = "Beschleunige um $acc"
}
