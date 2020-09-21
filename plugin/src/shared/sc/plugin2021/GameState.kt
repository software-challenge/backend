package sc.plugin2021

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamOmitField
import org.slf4j.LoggerFactory
import sc.api.plugins.TwoPlayerGameState
import sc.framework.plugins.Player
import sc.api.plugins.ITeam
import sc.api.plugins.exceptions.GameLogicException
import sc.plugin2021.util.Constants
import sc.plugin2021.util.GameRuleLogic
import java.lang.Exception

@XStreamAlias(value = "state")
class GameState @JvmOverloads constructor(
        override var first: Player = Player(Team.ONE),
        override var second: Player = Player(Team.TWO),
        override var lastMove: Move? = null,
        startTurn: Int = 0,
        val startColor: Color = Color.BLUE,
        @XStreamAsAttribute val startPiece: PieceShape = GameRuleLogic.getRandomPentomino()
): TwoPlayerGameState<Player>(Team.ONE) {
    
    companion object {
        val logger = LoggerFactory.getLogger(GameState::class.java)
    }
    
    @XStreamAsAttribute
    override val board: Board = Board()
    
    @XStreamAsAttribute
    val undeployedPieceShapes: Map<Color, MutableSet<PieceShape>> = Color.values().map {
        it to PieceShape.values().toMutableSet()
    }.toMap()
    
    @XStreamOmitField
    val deployedPieces: Map<Color, MutableList<Piece>> = Color.values().map {
        it to mutableListOf<Piece>()
    }.toMap()
    
    @XStreamAsAttribute
    val lastMoveMono: MutableMap<Color, Boolean> = mutableMapOf()
    
    override val currentTeam
        get() = when(currentColor) {
            Color.BLUE, Color.RED -> Team.ONE
            Color.YELLOW, Color.GREEN -> Team.TWO
        }
//        get() = currentColor.team
    
    override val currentPlayer
        get() = getPlayer(currentTeam)!!
    
    @XStreamAsAttribute
    val orderedColors: MutableList<Color> = mutableListOf()
    
    @XStreamAsAttribute
    private var currentColorIndex: Int = 0
    
    val currentColor: Color
        get() = try {
            orderedColors[currentColorIndex]
        } catch (e: IndexOutOfBoundsException) {
            logger.error("""
                :-- Exception on accessing currentColor --:
                orderedColors:     $orderedColors
                currentColorIndex: $currentColorIndex
            """.trimIndent())
            throw GameLogicException("Trying to access the currently active color with invalid index")
        }
    
    @XStreamAsAttribute
    override var turn: Int = 0
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
        
        if (orderedColors.isEmpty())
            throw GameLogicException("Game has already ended - can't proceed to next turn")
        
        val roundIncrementHelper = currentColorIndex + turns
        currentColorIndex = roundIncrementHelper % orderedColors.size
        round += (roundIncrementHelper - currentColorIndex) / orderedColors.size
    }

    fun tryAdvance(turns: Int = 1): Boolean = try {
        turn += turns
        true
    } catch (e: Exception) {
        false
    }

    fun addPlayer(player: Player) {
        when(player.color) {
            Team.ONE -> first = player
            Team.TWO -> second = player
        }
    }
    
    override fun getPointsForPlayer(team: ITeam): Int =
            (team as Team).colors.map { getPointsForColor(it) }.sum()
    
    private fun getPointsForColor(color: Color): Int {
        val pieces = undeployedPieceShapes[color] ?: return GameRuleLogic.SMALLEST_SCORE_POSSIBLE
        val lastMono = lastMoveMono[color] ?: false
        return GameRuleLogic.getPointsFromUndeployed(pieces, lastMono)
    }
    
    /** Removes the currently active color from the queue.
     *  The resulting active color will be the previous one; note that this is but a temporary value.
     *  Do not do anything with currentColor before the turn is done for good.
     */
    fun removeActiveColor() {
        logger.info("Removing $currentColor from the game")
        orderedColors.removeAt(currentColorIndex)
        if (orderedColors.isNotEmpty())
            currentColorIndex = (currentColorIndex + orderedColors.size) % orderedColors.size
        logger.debug("Remaining Colors: $orderedColors")
    }
    
    override fun toString(): String =
            if (orderedColors.isNotEmpty())
                "GameState $round/$turn -> $currentColor ${if (GameRuleLogic.isFirstMove(this)) "(Start Piece: $startPiece)" else ""}"
            else "GameState finished at $round/$turn"
    
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
        result = 31 * result + currentTeam.hashCode()
        result = 31 * result + turn
        return result
    }
    
}
