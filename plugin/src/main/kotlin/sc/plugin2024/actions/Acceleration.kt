package sc.plugin2024.actions

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.plugin2024.Action
import sc.plugin2024.FieldType
import sc.plugin2024.GameState
import sc.plugin2024.Ship
import sc.plugin2024.exceptions.AccException
import sc.plugin2024.exceptions.MoveException
import sc.shared.InvalidMoveException
import kotlin.math.abs

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
            state.board[ship.position] == FieldType.SANDBANK -> throw InvalidMoveException(AccException.ON_SANDBANK)
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
