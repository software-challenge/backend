package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamOmitField
import sc.api.plugins.IBoard
import sc.api.plugins.TwoPlayerGameState
import sc.framework.plugins.Player
import sc.api.plugins.ITeam
import sc.plugin2021.util.GameRuleLogic

@XStreamAlias(value = "state")
class GameState @JvmOverloads constructor(
        override var first: Player = Player(Team.ONE),
        override var second: Player = Player(Team.TWO),
        turn: Int = 0,
        override var lastMove: Move? = null
): TwoPlayerGameState<Player>(Team.ONE) {
    
    @XStreamAsAttribute
    override val board: Board = Board()
    
    @XStreamOmitField
    val undeployedPieceShapes: MutableMap<Color, List<Pair<Int, PieceShape>>> = mutableMapOf(
            Color.BLUE   to PieceShape.shapes.toMutableList(),
            Color.YELLOW to PieceShape.shapes.toMutableList(),
            Color.RED    to PieceShape.shapes.toMutableList(),
            Color.GREEN  to PieceShape.shapes.toMutableList(),
            Color.NONE   to emptyList<Pair<Int, PieceShape>>()
    )
    @XStreamAsAttribute
    val deployedPieces: MutableMap<Color, List<Piece>> = mutableMapOf(
            Color.BLUE   to mutableListOf<Piece>(),
            Color.YELLOW to mutableListOf<Piece>(),
            Color.RED    to mutableListOf<Piece>(),
            Color.GREEN  to mutableListOf<Piece>(),
            Color.NONE   to emptyList<Piece>()
    )
    
    @XStreamAsAttribute
    override var currentTeam = currentPlayerFromTurn() as Team
        private set
    
    override var turn: Int = turn
        set(value) {
            field = value
            currentTeam = currentPlayerFromTurn() as Team
        }
    
    fun addPlayer(player: Player) {
        when(player.color) {
            Team.ONE -> first = player
            Team.TWO -> second = player
        }
    }
    
    override fun getPointsForPlayer(team: ITeam<*>): Int =
            (team as Team).colors.map { getPointsForColor(it) }.sum()
    
    private fun getPointsForColor(color: Color): Int =
            GameRuleLogic.getPointsFromDeployedPieces(deployedPieces[color]!!)
    
    override fun toString(): String = "GameState Zug $turn"
    
    override fun equals(other: Any?): Boolean {
        return !(this === other) &&
                other is GameState &&
                first       == other.first &&
                second      == other.second &&
                board       == other.board &&
                turn        == other.turn &&
                currentTeam == other.currentTeam
    }
    
    override fun hashCode(): Int {
        var result = first.hashCode()
        result = 31 * result + second.hashCode()
        result = 31 * result + (lastMove?.hashCode() ?: 0)
        result = 31 * result + board.hashCode()
        result = 31 * result + undeployedPieceShapes.hashCode()
        result = 31 * result + deployedPieces.hashCode()
        result = 31 * result + currentTeam.hashCode()
        result = 31 * result + turn
        return result
    }
    
}
