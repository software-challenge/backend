package sc.api.plugins

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamOmitField
import org.slf4j.LoggerFactory
import sc.framework.plugins.AbstractPlayer
import sc.shared.PlayerColor

@XStreamAlias(value = "state")
abstract class TwoPlayerGameState<P : AbstractPlayer, M : IMove> : IGameState {

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

    /** letzter getaetigter Zug  */
    var lastMove: M? = null

    fun getOpponent(player: P) =
            getPlayer(player.playerColor.opponent())

    fun getPlayer(color: PlayerColor): P =
            if (color == PlayerColor.RED) red else blue

    /** Gibt die angezeigte Punktzahl des Spielers zurueck. */
    abstract fun getPointsForPlayer(playerColor: PlayerColor): Int

    override fun toString() =
            ("GameState{turn=" + turn + ",currentPlayer=" + currentPlayer + "}"
                    + red + blue
                    + board
                    + lastMove)

}