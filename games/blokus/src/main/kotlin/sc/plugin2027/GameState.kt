package sc.plugin2027

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.ITeam
import sc.api.plugins.Stat
import sc.api.plugins.Team
import sc.api.plugins.TwoPlayerGameState
import sc.plugin2027.util.BlokusWinReason
import sc.plugin2027.util.GameRuleLogic
import sc.plugin2027.util.Constants
import sc.plugin2027.util.GameRuleLogic.validateMoveColor
import sc.shared.WinCondition
import sc.shared.WinReasonTie

/**
 * Der aktuelle Spielstand.
 *
 * Er hält alle Informationen zur momentanen Runde,
 * mit deren Hilfe der nächste Zug berechnet werden kann.
 */
@XStreamAlias(value = "state")
data class GameState @JvmOverloads constructor(
    /** Die Anzahl an bereits getätigten Zügen. */
    @XStreamAsAttribute override var turn: Int = 0,
    /** Der zuletzt gespielte Zug. */
    override var lastMove: Move? = null,
    /** Das aktuelle Spielfeld. */
    override val board: Board = Board(),
    /** Der Spielstein, der in der ersten Runde gesetzt werden muss. */
    @XStreamAsAttribute val startPiece: PieceShape = GameRuleLogic.getRandomStartPentomino(),
    /** Speichert für jede Farbe, die alle Steine gelegt hat, ob das Monomino zuletzt gelegt wurde. */
    @XStreamAsAttribute
    val lastMoveMono: HashMap<Color, Boolean> = HashMap(),
    private val blueShapes: LinkedHashSet<PieceShape> = PieceShape.entries.toTypedArray().toCollection(LinkedHashSet()),
    private val yellowShapes: LinkedHashSet<PieceShape> = PieceShape.entries.toTypedArray().toCollection(LinkedHashSet()),
    private val redShapes: LinkedHashSet<PieceShape> = PieceShape.entries.toTypedArray().toCollection(LinkedHashSet()),
    private val greenShapes: LinkedHashSet<PieceShape> = PieceShape.entries.toTypedArray().toCollection(LinkedHashSet()),
    private val validColors: ArrayList<Color> = Color.entries.toCollection(ArrayList(Color.entries.size))
    
): TwoPlayerGameState<Move>(Team.ONE) {
    
    /**
     * Erstellt eine Kopie des übergebenen Spielzustands.
     * Das Spielfeld ist dabei eine tiefe Kopie, während alle anderen Informationen (z.B. die Liste der ungedeckten Steine) flach kopiert werden.
     *
     * @param other der zu kopierende Spielzustand
     */
    constructor(other: GameState): this(
        other.turn,
        other.lastMove,
        other.board.clone(),
        other.startPiece,
        HashMap(other.lastMoveMono),
        LinkedHashSet(other.blueShapes),
        LinkedHashSet(other.yellowShapes),
        LinkedHashSet(other.redShapes),
        LinkedHashSet(other.greenShapes),
        ArrayList(other.validColors)
    )
    
    /**
     * Gibt die Liste der noch nicht von [Color] gesetzten Steine zurück, die modifiziert werden kann.
     *
     * @param color die Spielerfarbe, standardmäßig die Farbe am Zug
     * @return die modifizierbare Liste der noch nicht von [Color] gesetzten Steine
     */
    private fun mutableUndeployedPieceShapes(color: Color = currentColor) = when (color) {
        Color.BLUE -> blueShapes
        Color.YELLOW -> yellowShapes
        Color.RED -> redShapes
        Color.GREEN -> greenShapes
    }
    
    /**
     * Gibt die Liste der noch nicht von [Color] gesetzten Steine zurück.
     *
     * @param color die Spielerfarbe, standardmäßig die Farbe am Zug
     * @return Liste der noch nicht von [Color] gesetzten Steine.
     */
    fun undeployedPieceShapes(color: Color = currentColor): Collection<PieceShape> =
        mutableUndeployedPieceShapes(color)
    
    /**
     * Entfernt den gegebenen Stein aus der Liste der noch nicht von [Color] gesetzten Steine.
     * Dies wird intern genutzt wenn ein [SetMove] ausgeführt wird.
     *
     * @param piece der zu entfernende Stein
     * @return true, wenn der Stein erfolgreich entfernt wurde, sonst false
     */
    fun removeUndeployedPiece(piece: Piece) =
        mutableUndeployedPieceShapes(piece.color).remove(piece.kind)
    
    /**
     * Berechnet die aktuelle Rundenzahl basierend auf der Anzahl der bereits getätigten Züge.
     * Die erste Runde beginnt hier mit 1, während der erste Zug ([turn]) mit 0 beginnt.
     *
     * @param turn die Anzahl der bereits getätigten Züge
     * @return die aktuelle Rundenzahl
     */
    fun roundFromTurn(turn: Int) = 1 + turn / Constants.COLORS
    
    
    /**
     * Setzt die Anzahl der bereits getätigten Züge und berechnet die aktuelle Rundenzahl entsprechend.
     * Diese Methode wird nur für Tests benötigt.
     * @param turn die Anzahl der bereits getätigten Züge, muss größer oder gleich 0 sein
     */
    fun setTurnAndRound(turn: Int) {
        if (turn < 0) throw IndexOutOfBoundsException("Can't go back in turns (request was $turn to $turn)")
        this.turn = turn
        round = roundFromTurn(turn)
    }
    
    /**
     * Die Rundenzahl.
     * Diese wird für Tests und in der GUI benötigt.
     */
    @XStreamAsAttribute
    var round: Int = 1
        get() = roundFromTurn(turn)
        private set
    
    /**
     * Das Team, das am Zug ist.
     */
    override val currentTeam
        get() = currentColor.team
    
    /**
     * Liste aller Farben in ihrer Zugreihenfolge.
     *
     * @return Liste aller Farben in ihrer Zugreihenfolge: Blau, Gelb, Rot, Grün.
     */
    val orderedColors: List<Color>
        get() = Color.values().toList()
    
    /**
     * Die Farbe, die am Zug ist.
     * Dies berechnet sich aus dem aktuellen Zug.
     */
    val currentColor: Color
        get() = orderedColors[turn % Constants.COLORS]
    
    /**
     * Prüft, ob es noch gültige Farben im Spiel gibt, d.h. ob es noch Spieler gibt, die Steine setzen können.
     * Dies wird intern genutzt um zu entscheiden, ob das Spiel vorbei ist und
     * ob eine Spielerfarbe übersprungen werden kann.
     *
     * @return true, wenn noch Farben im Spiel sind.
     */
    fun hasValidColors() =
        validColors.isNotEmpty()
    
    /**
     * Überprüft, ob die gegebene Farbe noch im Spiel ist.
     * Das heißt, ob die gegebene Farbe noch Steine setzen kann oder bereits alle ihre Steine gesetzt hat und damit übersprungen wird.
     *
     * @param color zu prüfende Farbe, standardmäßig die Farbe am Zug
     * @return ob die gegebene Farbe noch im Spiel ist.
     */
    @JvmOverloads
    fun isValidColor(color: Color = currentColor) =
        validColors.contains(color)
    
    /**
     * Geht zum Zug der nächsten noch im Spiel befindlichen Farbe über.
     * Dies wird intern genutzt um einen Zug auszuführen.
     *
     * @param turns wie viele Züge mindestens weiter gerückt werden soll
     * @return ob das Spiel vorgerückt oder bereits zu Ende ist.
     */
    @JvmOverloads
    fun advance(turns: Int = 1): Boolean {
        if(!hasValidColors())
            return false
        turn += turns
        while(!isValidColor())
            turn++
        round = roundFromTurn(turn)
        return true
    }
    
    /**
     * Berechne die Punkteanzahl für das gegebene Team.
     *
     * @param team das Team, für das die Punkte berechnet werden sollen
     * @return ein Array mit einem Element, das die Punkteanzahl für das Team enthält
     */
    override fun getPointsForTeam(team: ITeam): IntArray =
        // Sum all points for all colors of the team.
        intArrayOf(Color.entries.filter { it.team == team }.sumOf { getPointsForColor(it) })
    
    /**
     * Das Spiel ist vorbei, wenn kein Spieler mehr ziehen kann oder die maximale Rundenzahl erreicht wurde.
     *
     * @return true, wenn das Spiel vorbei ist, sonst false
     */
    override val isOver: Boolean
        get() = !hasValidColors() ||
                turn / 2 >= Constants.ROUND_LIMIT
    
    /**
     * Die Gewinnbedingung für Blokus ist, dass das Team mit den meisten Punkten gewinnt.
     *
     * @return die Gewinnbedingung, die entweder das Gewinnerteam oder null im Falle eines Unentschiedens enthält
     */
    override val winCondition: WinCondition?
        get() =
            // The winner is the player with the most points.
            if (getPointsForTeam(Team.ONE)[0] > getPointsForTeam(Team.TWO)[0]) {
                WinCondition(Team.ONE, BlokusWinReason.DIFFERING_SCORES)
            } else if (getPointsForTeam(Team.ONE)[0] < getPointsForTeam(Team.TWO)[0]) {
                WinCondition(Team.TWO, BlokusWinReason.DIFFERING_SCORES)
            } else {
                WinCondition(null, WinReasonTie)
            }
    
    /**
     * Berechnet die Punkteanzahl für die gegebene Farbe.
     * Diese ergibt sich aus der Anzahl der noch nicht gesetzten Steine und
     * ob das Monomino zuletzt gesetzt wurde.
     *
     * @param color die Spielerfarbe, für die die Punkte berechnet werden sollen
     * @return die Punkteanzahl für die gegebene Farbe
     */
    private fun getPointsForColor(color: Color): Int {
        val pieces = undeployedPieceShapes(color)
        val lastMono = lastMoveMono[color] ?: false
        return GameRuleLogic.getPointsFromUndeployed(pieces, lastMono)
    }
    
    /**
     * Entferne die Farbe, die momentan am Zug ist.
     * @return ob noch Farben im Spiel sind
     */
    internal fun removeActiveColor(): Boolean {
        validColors.remove(currentColor)
        return advance()
    }
    
    /**
     * Erstellt eine Kopie dieses Spielzustands.
     */
    override fun clone() = GameState(this)
    
    /**
     * Vergleicht diesen Spielzustand mit einem anderen Objekt auf Gleichheit.
     * Zwei Spielzustände sind gleich, wenn
     * * sie dasselbe Objekt sind oder
     * * ihre Startsteine
     * * ihre Spielbretter
     * * ihre letzten Züge
     * * ihre Informationen darüber, ob das Monomino zuletzt von jeder Farbe gelegt
     * * ihre Zugnummern
     * * ihre Listen der noch nicht gesetzten Steine für jede Farbe
     * * ihre Liste der gültigen Farben
     * übereinstimmen.
     */
    override fun equals(other: Any?): Boolean =
        this === other ||
                (other is GameState
                        && startPiece == other.startPiece
                        && board == other.board
                        && lastMove == other.lastMove
                        && lastMoveMono == other.lastMoveMono
                        && turn == other.turn
                        && blueShapes == other.blueShapes
                        && yellowShapes == other.yellowShapes
                        && redShapes == other.redShapes
                        && greenShapes == other.greenShapes
                        && validColors == other.validColors
                        )
    
    override fun hashCode(): Int {
        var result = startPiece.hashCode()
        result = 31 * result + board.hashCode()
        result = 31 * result + (lastMove?.hashCode() ?: 0)
        result = 31 * result + lastMoveMono.hashCode()
        result = 31 * result + turn
        result = 31 * result + blueShapes.hashCode()
        result = 31 * result + yellowShapes.hashCode()
        result = 31 * result + redShapes.hashCode()
        result = 31 * result + greenShapes.hashCode()
        result = 31 * result + validColors.hashCode()
        return result
    }
    
    /**
     * Gibt eine ausführliche String-Darstellung des Spielzustands zurück, die nur Zug, Team am Zug und Spielerfarbe anzeigt.
     * FIXME when is this used?
     */
    override fun toString() =
        "GameState(turn=$turn,currentTeam=$currentTeam,currentPlayer=$currentColor)"
    
    /**
     * Gibt eine ausführliche String-Darstellung des Spielzustands zurück, die alle relevanten Informationen enthält.
     * FIXME when is this used?
     */
    override fun longString(): String =
        "GameState(turn=$turn, validColors=$validColors, startPiece=$startPiece, lastMove=$lastMove, lastMoveMono=$lastMoveMono)\n" +
                "undeployedPieceShapes=${Color.entries.associateWith { undeployedPieceShapes(it) }}\n" +
                "$board"

    override fun performMoveDirectly(move: Move) {
        if (Constants.VALIDATE_MOVE)
            validateMoveColor(this, move)
        
        when (move) {
            is SkipMove -> GameRuleLogic.performSkipMove(this)
            is SetMove -> GameRuleLogic.performSetMove(this, move)
        }
        this.advance()
        lastMove = move
    }
    
    /**
     * Mögliche Züge der aktuellen Spielerfarbe des aktuellen Teams in der aktuellen Situation.
     * Diese Liste enthält für Blokus nur dann den [SkipMove] Zug, falls kein anderer Zug möglich ist.
     *
     * @return Liste der möglichen Züge der aktuellen Spielerfarbe.
     */
    override fun getSensibleMoves(): List<Move> {
        val possibleMoves = GameRuleLogic.getAllPossibleMoves(this)
        if (possibleMoves.isEmpty()) {
            return listOf(SkipMove(currentColor))
        }
        return possibleMoves
    }
    
    /**
     * Gibt progressiv alle möglichen Züge in der aktuellen Spielsituation zurück.
     */
    override fun moveIterator(): Iterator<Move> =
        getSensibleMoves().iterator()
    
    /**
     * Spielspezifische Informationen, für die GUI.
     * Für Blokus enthält dies die aktuelle Punkteanzahl für das gegebene Team.
     *
     * @param team das Team
     * @return eine einelementige Liste mit der Punktzahl des Teams.
     */
    override fun teamStats(team: ITeam): List<Stat> =
        listOf(
            Stat("Punkte", getPointsForTeam(team)[0]),
        )
    
}