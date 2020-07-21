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
        override var lastMove: Move? = null,
        startColor: Color = Color.BLUE
): TwoPlayerGameState<Player>(Team.ONE) {
    
    @XStreamAsAttribute
    override val board: Board = Board()
    
    @XStreamOmitField
    val undeployedPieceShapes: Map<Color, MutableMap<Int, PieceShape>> = mapOf(
            Color.BLUE   to PieceShape.shapes.toMutableMap(),
            Color.YELLOW to PieceShape.shapes.toMutableMap(),
            Color.RED    to PieceShape.shapes.toMutableMap(),
            Color.GREEN  to PieceShape.shapes.toMutableMap(),
            Color.NONE   to mutableMapOf<Int, PieceShape>()
    )
    @XStreamAsAttribute
    val deployedPieces: Map<Color, MutableList<Piece>> = mapOf(
            Color.BLUE   to mutableListOf<Piece>(),
            Color.YELLOW to mutableListOf<Piece>(),
            Color.RED    to mutableListOf<Piece>(),
            Color.GREEN  to mutableListOf<Piece>(),
            Color.NONE   to mutableListOf<Piece>()
    )
    
    @XStreamAsAttribute
    override var currentTeam = currentPlayerFromTurn() as Team
        private set
    
    @XStreamOmitField
    val orderedColors: MutableList<Color> = mutableListOf()
    
    @XStreamAsAttribute
    var currentColor: Color
    
    @XStreamAsAttribute
    override var turn: Int = turn
        set(value) {
            field = value
            currentTeam = currentPlayerFromTurn() as Team
            currentColor = currentColorFromTurn()
        }
    
    init {
        var _startColor = startColor
        for (x in 0 until 4) {
            orderedColors.add(_startColor)
            _startColor = _startColor.next
        }
        currentColor = currentColorFromTurn()
    }
    
    fun currentColorFromTurn(): Color =
            orderedColors[turn % 4]
    
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
