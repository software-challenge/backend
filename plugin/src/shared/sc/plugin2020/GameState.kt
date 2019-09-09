package sc.plugin2020

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.IMove
import sc.api.plugins.TwoPlayerGameState
import sc.framework.plugins.Player
import sc.plugin2020.util.Constants
import sc.shared.PlayerColor

@XStreamAlias(value = "state")
class GameState(
        override var red: Player = Player(PlayerColor.RED),
        override var blue: Player = Player(PlayerColor.BLUE),
        override var board: Board = Board()) : TwoPlayerGameState<Player, IMove>(), Cloneable {
    @XStreamAsAttribute
    override var turn: Int = 0
    private val undeployedRedPieces = parsePiecesString(Constants.STARING_PIECES, PlayerColor.RED)
    private val undeployedBluePieces = parsePiecesString(Constants.STARING_PIECES, PlayerColor.BLUE)
    private val allPieces = undeployedBluePieces + undeployedRedPieces

    override val round: Int
        get() = turn / 2

    val gameStats: Array<IntArray>
        get() = players.map { getPlayerStats(it) }.toTypedArray()

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

    fun getUndeployedPieces(owner: PlayerColor): MutableList<Piece> {
        return when(owner) {
            PlayerColor.RED -> undeployedRedPieces
            PlayerColor.BLUE -> undeployedBluePieces
        }
    }

    fun getDeployedPieces(owner: PlayerColor): List<Piece> {
        val ownerPieces = allPieces.filterTo(ArrayList()) { it.owner == owner }
        ownerPieces.removeAll(getUndeployedPieces(owner))
        return ownerPieces
    }

    fun addPlayer(player: Player) {
        when(player.color) {
            PlayerColor.RED -> red = player
            PlayerColor.BLUE -> blue = player
        }
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

    /**
     * For internal use only!
     */
    fun setUndeployedPieces(color: PlayerColor, pieces: List<Piece>) {
        val undeployedPieces = this.getUndeployedPieces(color)
        undeployedPieces.clear()
        undeployedPieces.addAll(pieces)
    }

}
