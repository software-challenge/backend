package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.TwoPlayerGameState
import sc.framework.plugins.Player
import sc.api.plugins.ITeam
import sc.plugin2021.util.Constants
import sc.plugin2021.util.GameRuleLogic

@XStreamAlias(value = "state")
class GameState @JvmOverloads constructor(
        override var first: Player = Player(Team.ONE),
        override var second: Player = Player(Team.TWO),
        override var lastMove: Move? = null,
        startTurn: Int = 1,
        val startColor: Color = Color.BLUE,
        @XStreamAsAttribute val startPiece: PieceShape = GameRuleLogic.getRandomPentomino()
): TwoPlayerGameState<Player>(Team.ONE) {
    
    @XStreamAsAttribute
    override val board: Board = Board()
    
    @XStreamAsAttribute
    val undeployedPieceShapes: Map<Color, MutableSet<PieceShape>> = Color.values().map {
        it to PieceShape.values().toMutableSet()
    }.toMap()
    
    @XStreamAsAttribute
    val deployedPieces: Map<Color, MutableList<Piece>> = Color.values().map {
        it to mutableListOf<Piece>()
    }.toMap()
    
    override val currentTeam
        get() = currentColor.team
    
    @XStreamAsAttribute
    val orderedColors: MutableList<Color> = mutableListOf()
    
    @XStreamAsAttribute
    private var currentColorIndex: Int = 0
    
    val currentColor: Color
        get() = orderedColors[currentColorIndex]
    
    @XStreamAsAttribute
    override var turn: Int = 1
        set(value) {
            advance(value - field)
            field = value
        }
    
    @XStreamAsAttribute
    override var round: Int = 1
    
    init {
        var colorIter = startColor
        for (x in 0 until Constants.COLORS) {
            orderedColors.add(colorIter)
            colorIter = colorIter.next
        }
        turn = startTurn
    }
    
    private fun advance(turns: Int) {
        if (turns < 0) throw IndexOutOfBoundsException("Can't go back in turns (Request was $turns), expected value bigger than $turn")
        
        val roundIncrementHelper = currentColorIndex + turns
        currentColorIndex = roundIncrementHelper % orderedColors.size
        round += (roundIncrementHelper - currentColorIndex) / orderedColors.size
    }
    
    fun addPlayer(player: Player) {
        when(player.color) {
            Team.ONE -> first = player
            Team.TWO -> second = player
        }
    }
    
    override fun getPointsForPlayer(team: ITeam<*>): Int =
            (team as Team).colors.map { getPointsForColor(it) }.sum()
    
    private fun getPointsForColor(color: Color): Int {
            val pieces = deployedPieces[color].let{listOf<Piece>()}
            return GameRuleLogic.getPointsFromDeployedPieces(pieces)
    }
    
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
