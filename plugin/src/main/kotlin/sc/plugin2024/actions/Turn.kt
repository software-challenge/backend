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
    var direction: Int,
) : Action {

    @Throws(InvalidMoveException::class)
    override fun perform(state: GameState, ship: Ship) {
        require(!(direction == 0 || direction < -3 || direction > 3)) {
            throw InvalidMoveException(MoveException.INVALID_TURN)
        }
        requireNotNull(ship?.getField(state!!.board)) {
            throw InvalidMoveException(MoveException.SANDBANK)
        }.takeIf { it.type === FieldType.SANDBANK }

        val newDirection: HexDirection = ship!!.direction!!.getTurnedDirection(direction)
        val usedCoal: Int = (abs(direction.toDouble()) - ship.freeTurns).toInt()

        ship.freeTurns = if (ship.freeTurns - abs(direction.toDouble()) <= 0) 0 else 1

        if (usedCoal > 0) {
            require(ship.coal >= usedCoal) {
                throw InvalidMoveException(MoveException.COAL)
            }
            ship.coal -= usedCoal
        }
        ship.direction = newDirection
    }

    override fun toString(): String = "Drehe um $direction"
}