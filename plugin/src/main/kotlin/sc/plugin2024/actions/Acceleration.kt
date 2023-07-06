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
class Acceleration : Action {
    /**
     * Gibt an, um wie viel beschleunigt wird. Negative Zahl bedeutet, dass entsprechend gebremst wird.
     * Darf nicht 0 sein, wirft sonst InvalidMoveException beim Ausführen von perform
     */
    @XStreamAsAttribute
    var acc: Int

    constructor() {
        order = 0
        acc = 0
    }

    /**
     * Legt eine neue Beschleunigungaktion an
     * @param acc Wert, um den beschleunigt wird
     */
    constructor(acc: Int) {
        this.acc = acc
    }

    /**
     * Legt eine neue Beschleunigungaktion an
     * @param acc Wert, um den beschleunigt wird
     * @param order Nummer der Aktion. Aktionen werden aufsteigend sortiert nach ihrer Nummer ausgeführt.
     */
    constructor(acc: Int, order: Int) {
        this.acc = acc
        super.order = order
    }

    /**
     *
     * @param state Gamestate, auf dem die Beschleunigung ausgeführt wird
     * @param player Spieler, für den die Beschleunigung ausgeführt wird
     */
    @Throws(InvalidMoveException::class)
    override fun perform(state: GameState?, player: Ship?) {
        var speed: Int = player!!.speed
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
        if (player.getField(state!!.board)!!.type == FieldType.SANDBANK) {
            throw InvalidMoveException(MoveException.SANDBANK)
        }
        val usedCoal: Int = (abs(acc.toDouble()) - player.freeAcc).toInt()
        if (usedCoal > 0) {
            player.coal -= usedCoal
        }
        player.speed = speed
        player.movement += acc
        player.freeAcc = 0
        return
    }

    fun clone(): Acceleration {
        return Acceleration(acc, this.order)
    }

    override fun equals(o: Any?): Boolean {
        return if (o is Acceleration) {
            acc == o.acc
        } else false
    }

    override fun toString(): String {
        return "Beschleunige um $acc"
    }

    override fun hashCode(): Int {
        return acc
    }
}
