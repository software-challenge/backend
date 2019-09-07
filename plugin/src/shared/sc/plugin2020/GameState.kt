package sc.plugin2020

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.IMove
import sc.api.plugins.TwoPlayerGameState
import sc.framework.plugins.Player
import sc.plugin2020.util.Constants
import sc.shared.PlayerColor
import java.util.*

@XStreamAlias(value = "state")
class GameState(
        override var red: Player = Player(PlayerColor.RED),
        override var blue: Player = Player(PlayerColor.BLUE),
        override var board: Board = Board()) : TwoPlayerGameState<Player, IMove>(), Cloneable {
    @XStreamAsAttribute
    override var turn: Int = 0
    private val undeployedRedPieces: ArrayList<Piece> = parsePiecesString(Constants.STARING_PIECES, PlayerColor.BLUE)
    private val undeployedBluePieces: ArrayList<Piece> = parsePiecesString(Constants.STARING_PIECES, PlayerColor.BLUE)

    override val round: Int
        get() = turn / 2

    // TODO
    val gameStats: Array<IntArray>
        get() = Array(1) { IntArray(1) }

    private fun parsePiecesString(s: String, p: PlayerColor): ArrayList<Piece> {
        val l = ArrayList<Piece>()
        for(c in s.toCharArray()) {
            when(c) {
                'Q' -> l.add(Piece(p, PieceType.BEE))
                'S' -> l.add(Piece(p, PieceType.SPIDER))
                'G' -> l.add(Piece(p, PieceType.GRASSHOPPER))
                'B' -> l.add(Piece(p, PieceType.BEETLE))
                'A' -> l.add(Piece(p, PieceType.ANT))
            }
        }
        return l
    }

    fun getUndeployedPieces(owner: PlayerColor): ArrayList<Piece> {
        return if(owner === PlayerColor.RED)
            undeployedRedPieces
        else
            undeployedBluePieces
    }

    fun addPlayer(player: Player) {
        if(player.color === PlayerColor.BLUE)
            blue = player
        else
            red = player
    }

    override fun getPointsForPlayer(playerColor: PlayerColor): Int {
        return turn
    }

    fun getPlayerStats(p: Player): IntArray {
        return getPlayerStats(p.color)
    }

    fun getPlayerStats(p: PlayerColor): IntArray {
        val tmp = IntArray(1)
        tmp[0] = turn
        return tmp
    }

    override fun toString(): String {
        return String.format("GameState Zug %d", this.turn)
    }
}
