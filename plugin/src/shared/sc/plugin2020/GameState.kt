package sc.plugin2020

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamOmitField
import sc.api.plugins.IMove
import sc.api.plugins.TwoPlayerGameState
import sc.framework.plugins.Player
import sc.plugin2020.util.Constants
import sc.plugin2020.util.GameRuleLogic
import sc.shared.PlayerColor

@XStreamAlias(value = "state")
data class GameState(
        override var red: Player = Player(PlayerColor.RED),
        override var blue: Player = Player(PlayerColor.BLUE),
        override var board: Board = Board(),
        @XStreamAsAttribute
        override var turn: Int = 0,
        private val undeployedRedPieces: MutableList<Piece> = parsePiecesString(Constants.STARTING_PIECES, PlayerColor.RED),
        private val undeployedBluePieces: MutableList<Piece> = parsePiecesString(Constants.STARTING_PIECES, PlayerColor.BLUE)
): TwoPlayerGameState<Player, IMove>(), Cloneable {
    
    @XStreamOmitField
    private val allPieces: Collection<Piece> = undeployedBluePieces + undeployedRedPieces + board.getPieces()
    
    val gameStats: Array<IntArray>
        get() = players.map { getPlayerStats(it) }.toTypedArray()
    
    fun getUndeployedPieces(owner: PlayerColor): MutableList<Piece> {
        return when(owner) {
            PlayerColor.RED -> undeployedRedPieces
            PlayerColor.BLUE -> undeployedBluePieces
        }
    }
    
    fun getDeployedPieces(owner: PlayerColor): List<Piece> {
        val ownerPieces = allPieces.filterTo(ArrayList()) { it.owner == owner }
        getUndeployedPieces(owner).forEach { ownerPieces.remove(it) }
        return ownerPieces
    }
    
    fun addPlayer(player: Player) {
        when(player.color) {
            PlayerColor.RED -> red = player
            PlayerColor.BLUE -> blue = player
        }
    }
    
    override fun getPointsForPlayer(playerColor: PlayerColor): Int {
        return GameRuleLogic.freeBeeNeighbours(this.board, playerColor)
    }
    
    fun getPlayerStats(p: Player): IntArray {
        return getPlayerStats(p.color)
    }
    
    fun getPlayerStats(playerColor: PlayerColor): IntArray =
            intArrayOf(this.getPointsForPlayer(playerColor))
    
    override fun toString(): String = "GameState Zug $turn"
    
    companion object {
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
    }
}
