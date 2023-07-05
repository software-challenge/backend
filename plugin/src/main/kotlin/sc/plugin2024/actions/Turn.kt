package sc.plugin2024.actions

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.plugin2024.*
import sc.plugin2024.exceptions.TurnException
import sc.shared.InvalidMoveException
import kotlin.math.abs

@XStreamAlias(value = "turn")
class Turn : Action {
    /**
     * Wie viel gedreht wird.
     */
    @XStreamAsAttribute
    var direction: Int

    constructor() {
        order = 0
        direction = 0
    }

    /**
     * Legt einen neuen Drehzug an
     * @param direction Wert, um wie viel gedreht wird
     */
    constructor(direction: Int) {
        this.direction = direction
    }

    /**
     * Legt einen neuen Drehzug an
     * @param direction Wert, um wie viel gedreht wird
     * @param order Nummer der Aktion. Aktionen werden aufsteigend sortiert nach
     * ihrer Nummer ausgeführt.
     */
    constructor(direction: Int, order: Int) {
        this.direction = direction
        super.order = order
    }

    /**
     * @param state Gamestate
     * @param player Spieler der die Aktion ausführt
     */
    @Throws(InvalidMoveException::class)
    override fun perform(state: GameState?, player: MississippiPlayer?) {
        if (direction == 0 || direction < -3 || direction > 3) {
            throw InvalidMoveException(TurnException.INVALID_TURN)
        }
        if (player!!.getField(state!!.board)!!.type === FieldType.SANDBANK) {
            throw InvalidMoveException(TurnException.SANDBANK)
        }
        val newDirection: Direction = player!!.direction!!.getTurnedDirection(direction)
        val usedCoal: Int = (abs(direction.toDouble()) - player.freeTurns).toInt()
        val test: Int = (player.freeTurns - abs(direction.toDouble())).toInt()
        if (test <= 0) {
            player.freeTurns = 0
        } else {
            player.freeTurns = 1 // only possible, wenn freeTurn was 2 und player turns by 1
        }
        if (usedCoal > 0) {
            if (player.coal >= usedCoal) {
                player.coal -= usedCoal
            } else {
                throw InvalidMoveException(TurnException.COAL)
            }
        }
        player.direction = newDirection
        return
    }

    fun clone(): Turn {
        return Turn(direction, this.order)
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Turn) {
            direction == other.direction
        } else false
    }

    override fun toString(): String {
        return "Drehe um $direction"
    }

    override fun hashCode(): Int {
        return direction
    }
}
