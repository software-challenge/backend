package sc.api.plugins

import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamOmitField
import org.slf4j.LoggerFactory
import sc.framework.plugins.Player
import sc.shared.PlayerColor

abstract class TwoPlayerGameState<P : Player, M : IMove> : IGameState {

    abstract val red: P
    abstract val blue: P
    abstract val board: IBoard

    @XStreamOmitField
    private val logger = LoggerFactory.getLogger(TwoPlayerGameState::class.java)

    /** Farbe des Startspielers  */
    @XStreamAsAttribute
    open var startPlayerColor: PlayerColor = PlayerColor.RED

    /** Farbe des Spielers, der aktuell am Zug ist */
    @XStreamAsAttribute
    open var currentPlayerColor: PlayerColor = PlayerColor.RED

    /** Liste der Spieler. Reihenfolge: RED, BLUE */
    val players: List<P>
        get() = listOf(red, blue)

    /** Der Spieler, der momentan am Zug ist. */
    val currentPlayer: P
        get() = getPlayer(currentPlayerColor)

    /** Der Spieler, der momentan nicht am Zug ist. */
    val otherPlayer: P
        get() = getPlayer(otherPlayerColor)

    /** Farbe des Spielers, der momentan nicht am Zug ist. */
    val otherPlayerColor: PlayerColor
        get() = currentPlayerColor.opponent()

    /** Der Spieler, der das Spiel begonnen hat. */
    val startPlayer: P
        get() = getPlayer(startPlayerColor)

    /** Die Namen der beiden Spieler. */
    val playerNames: Array<String>
        get() = arrayOf(red.displayName, blue.displayName)

    /** Letzter getaetigter Zug. */
    var lastMove: M? = null

    fun getOpponent(player: P) =
            getPlayer(player.color.opponent())

    fun getPlayer(color: PlayerColor): P =
            when(color) {
                PlayerColor.RED -> red
                PlayerColor.BLUE -> blue
            }

    /** Gibt die angezeigte Punktzahl des Spielers zurueck. */
    abstract fun getPointsForPlayer(playerColor: PlayerColor): Int

    override fun toString() =
            "GameState(turn=$turn,currentPlayer=${currentPlayer.color})"

}