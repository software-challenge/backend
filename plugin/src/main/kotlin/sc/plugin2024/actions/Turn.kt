package sc.plugin2024.actions

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.plugin2024.*
import sc.plugin2024.exceptions.MoveException
import sc.shared.InvalidMoveException
import kotlin.math.abs

@XStreamAlias(value = "turn")
class Turn(
    @XStreamAsAttribute
    var direction: Int,
    order: Int = 0
) : Action() {

    init {
        super.order = order
    }

    @Throws(InvalidMoveException::class)
    override fun perform(state: GameState?, player: Ship?) {
        require(!(direction == 0 || direction < -3 || direction > 3)) {
            throw InvalidMoveException(MoveException.INVALID_TURN)
        }
        requireNotNull(player?.getField(state!!.board)) {
            throw InvalidMoveException(MoveException.SANDBANK)
        }.takeIf { it.type === FieldType.SANDBANK }

        val newDirection: Direction = player!!.direction!!.getTurnedDirection(direction)
        val usedCoal: Int = (abs(direction.toDouble()) - player.freeTurns).toInt()

        player.freeTurns = if (player.freeTurns - abs(direction.toDouble()) <= 0) 0 else 1

        if (usedCoal > 0) {
            require(player.coal >= usedCoal) {
                throw InvalidMoveException(MoveException.COAL)
            }
            player.coal -= usedCoal
        }
        player.direction = newDirection
    }

    override fun equals(other: Any?): Boolean = other is Turn && direction == other.direction

    override fun toString(): String = "Drehe um $direction"

    override fun hashCode(): Int = direction
}