package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.IMove
import sc.shared.InvalidMoveException

@XStreamAlias(value = "action")
interface Action: IMove {
    
    /**
     * Nummer der Aktion. Aktionen werden aufsteigend sortiert nach ihrer Nummer
     * ausgeführt.
     */
    var order: Int
    
    @Throws(InvalidMoveException::class)
    fun perform(state: GameState, ship: Ship)
}
