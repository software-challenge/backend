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

/**
 * Der aktuelle Spielstand.
 *
 * Er hält alle Informationen zur momentanen Runde,
 * mit deren Hilfe der nächste Zug berechnet werden kann.
 */
@XStreamAlias(value = "state")
class GameState @JvmOverloads constructor(
        /** Das erste Team, @see [Team]. */
        override var first: Player = Player(Team.ONE),
        /** Das zweite Team, @see [Team]. */
        override var second: Player = Player(Team.TWO),
        /** Der zuletzt gespielte Zug. */
        override var lastMove: Move? = null,
        startTurn: Int = 0,
        /** Die Farbe, die anfängt. */
        val startColor: Color = Color.BLUE,
        /** Der Spielstein, der in der ersten Runde gesetzt werden muss. */
        @XStreamAsAttribute val startPiece: PieceShape = GameRuleLogic.getRandomPentomino()
): TwoPlayerGameState<Player>(Team.ONE) {
    
    companion object {
        val logger = LoggerFactory.getLogger(GameState::class.java)
    }
    
    /** Das aktuelle Spielfeld. */
    @XStreamAsAttribute
    override val board: Board = Board()
    
    /** Gib eine Liste aller nicht gesetzter Steine der [Color] zurück. */
    fun undeployedPieceShapes(color: Color): MutableSet<PieceShape> = when (color) {
        Color.BLUE -> blueShapes
        Color.YELLOW -> yellowShapes
        Color.RED -> redShapes
        Color.GREEN -> greenShapes
    }
    
    private val blueShapes: MutableSet<PieceShape> = PieceShape.values().toMutableSet()
    private val yellowShapes: MutableSet<PieceShape> = PieceShape.values().toMutableSet()
    private val redShapes: MutableSet<PieceShape> = PieceShape.values().toMutableSet()
    private val greenShapes: MutableSet<PieceShape> = PieceShape.values().toMutableSet()
    
    /** Speichert für jede Farbe, die alle Steine gelegt hat, ob das Monomino zuletzt gelegt wurde. */
    @XStreamAsAttribute
    val lastMoveMono: MutableMap<Color, Boolean> = mutableMapOf()
    
    /** Das Team, das am Zug ist. */
    override val currentTeam
        get() = currentColor.team
    
    /** Der Spieler, der am Zug ist. */
    override val currentPlayer
        get() = getPlayer(currentTeam)!!
    
    /** Eine Liste aller Farben, die momentan im Spiel sind. */
    @XStreamAsAttribute
    val orderedColors: MutableList<Color> =
            Constants.COLORS.downTo(2).fold(mutableListOf(startColor), { acc, _ -> acc.add(acc.last().next); acc })
    
    @XStreamAsAttribute
    private var currentColorIndex: Int = startTurn % orderedColors.size
    
    /** Die Farbe, die am Zug ist. */
    val currentColor: Color
        get() = if (orderedColors.isEmpty()) {
            throw IndexOutOfBoundsException("Trying to access currentColor while there are no colors in the game!")
        } else {
            orderedColors[currentColorIndex]
        }
    
    /** Die Anzahl an bereits getätigten Zügen. */
    @XStreamAsAttribute
    override var turn: Int = startTurn
        set(value) {
            advance(value - field)
            field = value
        }
    
    /** Die Rundenzahl. */
    @XStreamAsAttribute
    override var round: Int = 1 + startTurn / orderedColors.size
    
    private fun advance(turns: Int) {
        if (turns < 0) throw IndexOutOfBoundsException("Can't go back in turns (request was $turns): Expected value bigger than $turn")
        
        if (orderedColors.isEmpty())
            throw GameLogicException("Game has already ended - can't proceed to next turn")
        
        val roundIncrementHelper = currentColorIndex + turns
        currentColorIndex = roundIncrementHelper % orderedColors.size
        round += (roundIncrementHelper - currentColorIndex) / orderedColors.size
    }
    
    /**
     * Versuche, zum nächsten Zug überzugehen.
     * Schlage fehl, wenn das Spiel bereits zu ende ist.
     * @return Ob es erfolgreich war.
     */
    fun tryAdvance(turns: Int = 1): Boolean = try {
        turn += turns
        true
    } catch (e: Exception) {
        false
    }
    
    fun addPlayer(player: Player) {
        when (player.color) {
            Team.ONE -> first = player
            Team.TWO -> second = player
        }
    }
    
    /** Berechne die Punkteanzahl für das gegebene Team. */
    override fun getPointsForPlayer(team: ITeam): Int =
            (team as Team).colors.map { getPointsForColor(it) }.sum()
    
    private fun getPointsForColor(color: Color): Int {
        val pieces = undeployedPieceShapes(color)
        val lastMono = lastMoveMono[color] ?: false
        return GameRuleLogic.getPointsFromUndeployed(pieces, lastMono)
    }
    
    /**
     * Entferne die Farbe, die momentan am Zug ist.
     * Die resultierende aktive Farbe wird dann die des letzten Zuges sein.
     *
     * Diese Funktion wird von der [GameRuleLogic] benötigt und sollte nie so aufgerufen werden.
     */
    internal fun removeActiveColor() {
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
               first == other.first &&
               second == other.second &&
               board == other.board &&
               turn == other.turn &&
               currentTeam == other.currentTeam
    }
    
    override fun hashCode(): Int {
        var result = first.hashCode()
        result = 31 * result + second.hashCode()
        result = 31 * result + (lastMove?.hashCode() ?: 0)
        result = 31 * result + startColor.hashCode()
        result = 31 * result + startPiece.hashCode()
        result = 31 * result + board.hashCode()
        result = 31 * result + blueShapes.hashCode()
        result = 31 * result + yellowShapes.hashCode()
        result = 31 * result + redShapes.hashCode()
        result = 31 * result + greenShapes.hashCode()
        result = 31 * result + lastMoveMono.hashCode()
        result = 31 * result + orderedColors.hashCode()
        result = 31 * result + currentColorIndex
        result = 31 * result + turn
        result = 31 * result + round
        return result
    }
}
