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
    fun undeployedPieceShapes(color: Color = currentColor): MutableSet<PieceShape> = when (color) {
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
    val orderedColors: List<Color>
        get() = Color.values().toList()

    @XStreamOmitField
    internal val validColors: MutableSet<Color> = Color.values().toMutableSet()

    /** Die Farbe, die am Zug ist. */
    val currentColor: Color
        get() = orderedColors[turn % Constants.COLORS]

    /** Die Anzahl an bereits getätigten Zügen. */
    @XStreamAsAttribute
    override var turn: Int = 0
        set(value) {
            if (value < 0) throw IndexOutOfBoundsException("Can't go back in value (request was $turn to $value)")
            field = value
        }
    
    /** Die Rundenzahl. */
    @XStreamAsAttribute
    override var round: Int = 1
        private set
        get() {
            field = 1 + turn / orderedColors.size
            return field
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
    internal fun remove(color: Color = currentColor) {
        logger.info("Removing $color from the game")
        validColors.remove(color)
        logger.debug("Remaining Colors: $validColors")
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
        result = 31 * result + startPiece.hashCode()
        result = 31 * result + board.hashCode()
        result = 31 * result + lastMoveMono.hashCode()
        result = 31 * result + turn
        return result
    }
}
