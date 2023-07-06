package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.IMove
import sc.shared.InvalidMoveException

@XStreamAlias(value = "action")
abstract class Action : IMove, Comparable<Action?> {
    /**
     * Nummer der Aktion. Aktionen werden aufsteigend sortiert nach ihrer Nummer
     * ausgeführt.
     */
    @XStreamAsAttribute
    var order = 0

    @Throws(InvalidMoveException::class)
    abstract fun perform(state: GameState?, player: Ship?)
    override operator fun compareTo(other: Action?): Int {
        return order.compareTo(other!!.order)
    }
}
