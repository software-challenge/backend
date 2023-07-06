package sc.plugin2024.actions

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.plugin2024.Action
import sc.plugin2024.FieldType
import sc.plugin2024.GameState
import sc.plugin2024.Ship
import sc.plugin2024.exceptions.MoveException
import sc.shared.InvalidMoveException
import kotlin.math.abs

@XStreamAlias(value = "acceleration")
data class Acceleration(
        /**
         * Gibt an, um wie viel beschleunigt wird. Negative Zahl bedeutet, dass entsprechend gebremst wird.
         * Darf nicht 0 sein, wirft sonst InvalidMoveException beim Ausführen von perform
         */
        @XStreamAsAttribute val acc: Int): Action {

    /**
     *
     * @param state Gamestate, auf dem die Beschleunigung ausgeführt wird
     * @param ship Spieler, für den die Beschleunigung ausgeführt wird
     */
    @Throws(InvalidMoveException::class)
    override fun perform(state: GameState, ship: Ship) {
        var speed: Int = ship.speed
        speed += acc
        if (acc == 0) {
            throw InvalidMoveException(MoveException.ZERO_ACC)
        }
        if (speed > 6) {
            throw InvalidMoveException(MoveException.MAX_ACC)
        }
        if (speed < 1) {
            throw InvalidMoveException(MoveException.MIN_ACC)
        }
        if (ship.getField(state.board)!!.type == FieldType.SANDBANK) {
            throw InvalidMoveException(MoveException.SANDBANK)
        }
        val usedCoal: Int = (abs(acc.toDouble()) - ship.freeAcc).toInt()
        if (usedCoal > 0) {
            ship.coal -= usedCoal
        }
        ship.speed = speed
        ship.movement += acc
        ship.freeAcc = 0
        return
    }

    override fun toString(): String = "Beschleunige um $acc"
}
