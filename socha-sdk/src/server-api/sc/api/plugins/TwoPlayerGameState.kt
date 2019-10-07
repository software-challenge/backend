package sc.api.plugins

import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamOmitField
import org.slf4j.LoggerFactory
import sc.shared.PlayerColor

abstract class TwoPlayerGameState<M : IMove> : IGameState {

    abstract val board: IBoard

    @XStreamOmitField
    private val logger = LoggerFactory.getLogger(TwoPlayerGameState::class.java)

    /** Farbe des Startspielers  */
    @XStreamAsAttribute
    open var startPlayer: PlayerColor = PlayerColor.RED

    /** Farbe des Spielers, der aktuell am Zug ist */
    @XStreamAsAttribute
    open var currentPlayer: PlayerColor = PlayerColor.RED

    /** Farbe des Spielers, der momentan nicht am Zug ist. */
    val otherPlayer: PlayerColor
        get() = currentPlayer.opponent()

    /** Letzter getaetigter Zug. */
    var lastMove: M? = null

    /** Gibt die angezeigte Punktzahl des Spielers zurueck. */
    abstract fun getPointsForPlayer(playerColor: PlayerColor): Int

    override fun toString() =
            "GameState(turn=$turn,currentPlayer=$currentPlayer)"

}