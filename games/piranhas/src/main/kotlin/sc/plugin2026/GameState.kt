package sc.plugin2026

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.ITeam
import sc.api.plugins.Stat
import sc.api.plugins.Team
import sc.api.plugins.TwoPlayerGameState
import sc.framework.plugins.maxByNoEqual
import sc.plugin2026.util.GameRuleLogic
import sc.plugin2026.util.PiranhaConstants
import sc.plugin2026.util.PiranhasWinReason
import sc.shared.InvalidMoveException
import sc.shared.WinCondition
import sc.shared.WinReasonTie

/**
 * Die Klasse GameState repräsentiert den aktuellen Zustand des Spiels.
 *
 * Sie enthält alle Informationen über die aktuelle Runde,
 * um alle benötigten Daten für den nächsten Zug bereitzustellen.
 *
 * @param turn Die Anzahl der bereits gespielten Züge.
 * @param lastMove Der zuletzt gespielte Zug.
 * @param board Das aktuelle Spielfeld.
 * @property board Das aktuelle Spielfeld.
 * @property turn Die Anzahl der bereits gespielten Züge.
 * @property lastMove Der zuletzt gespielte Zug.
 */
@XStreamAlias(value = "state")
data class GameState @JvmOverloads constructor(
    /** Die Anzahl an bereits getätigten Zügen. */
    @XStreamAsAttribute override var turn: Int = 0,
    /** Der zuletzt gespielte Zug. */
    override var lastMove: Move? = null,
    /** Das aktuelle Spielfeld. */
    override val board: Board = Board(),
    /** Team, das als erstes seinen vollständigen Schwarm gebildet hat (alle eigenen Fische zusammenhängend). */
    @XStreamAsAttribute var firstUnion: Team? = null,
): TwoPlayerGameState<Move>(Team.ONE) {
    
    /** Berechnet die Punktwerte für das angegebene [team]. */
    override fun getPointsForTeam(team: ITeam): IntArray =
        intArrayOf(GameRuleLogic.greatestSwarmSize(board, team))
    
    // TODO test if one player is unable to move he loses e.g. in corner
    /** Gibt an, ob das Spiel beendet ist. */
    override val isOver: Boolean
        get() = (Team.values().any { GameRuleLogic.isSwarmConnected(board, it) } && turn.mod(2) == 0) ||
                this.getSensibleMoves().isEmpty() ||
                turn / 2 >= PiranhaConstants.ROUND_LIMIT
    
    /** Liefert die aktuelle Gewinnbedingung oder null, falls das Spiel noch nicht entschieden ist. */
    override val winCondition: WinCondition?
        get() =
            if(Team.values().any { team -> GameRuleLogic.isSwarmConnected(board, team) }) {
                // Bestimme Team mit eindeutig größtem Schwarm oder nutze Tie‑Breaker FIRST_UNION, sonst Unentschieden
                val best = Team.values().toList().maxByNoEqual { team -> GameRuleLogic.greatestSwarmSize(board, team) }
                best?.let { WinCondition(it, PiranhasWinReason.BIGGER_SWARM) }
                             ?: firstUnion?.let { WinCondition(it, PiranhasWinReason.FIRST_UNION) }
                             ?: WinCondition(null, WinReasonTie)
            } else if (this.getSensibleMoves().isEmpty()) {
                val team = this.currentTeam.opponent()
                WinCondition(team, PiranhasWinReason.BLOCKED)
            } else {
                null
            }
    
    /** Führt einen gültigen [move] direkt aus und aktualisiert Spielfeld sowie Zähler. */
    override fun performMoveDirectly(move: Move) {
        if(board.getTeam(move.from) != currentTeam) {
            throw InvalidMoveException(PiranhaMoveMistake.WRONG_START, move)
        }
        GameRuleLogic.checkMove(board, move)?.let { throw InvalidMoveException(it, move) }
        val distance = GameRuleLogic.movementDistance(board, move)
        board[move.from + move.direction.vector * distance] = board[move.from]
        board[move.from] = FieldState.EMPTY
        turn++
        lastMove = move
        // Nach dem Zug prüfen, ob ein Team erstmals vollständig verbunden ist
        if(firstUnion == null) {
            for(team in Team.values()) {
                if(GameRuleLogic.isSwarmConnected(board, team)) {
                    firstUnion = team
                    break
                }
            }
        }
    }
    
    /** Gibt alle sinnvollen Züge für den aktuellen Spieler auf dem aktuellen [board] zurück. */
    override fun getSensibleMoves(): List<Move> {
        val piranhas = board.filterValues { field -> field.team == currentTeam }
        val moves = ArrayList<Move>(piranhas.size * 2)
        for(piranha in piranhas) {
            moves.addAll(GameRuleLogic.possibleMovesFor(board, piranha.key))
        }
        return moves
    }
    
    /** Iteriert über alle sinnvollen Züge des aktuellen Spielers. */
    override fun moveIterator(): Iterator<Move> =
        getSensibleMoves().iterator()
    
    /** Erstellt eine tiefe Kopie dieses Spielzustands inklusive geklontem [board]. */
    override fun clone(): GameState =
        copy(board = board.clone())
    
    /** Ermittelt zusammenfassende Statistiken für das angegebene [team]. */
    override fun teamStats(team: ITeam): List<Stat> =
        listOf(
            Stat("Anzahl Fische", board.fieldsForTeam(team).size),
            Stat("Größter Schwarm", GameRuleLogic.greatestSwarmSize(board, team))
        )
    
}
