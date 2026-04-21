package sc.plugin2098

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.api.plugins.ITeam
import sc.api.plugins.Stat
import sc.api.plugins.Team
import sc.api.plugins.TwoPlayerGameState
import sc.framework.plugins.maxByNoEqual
import sc.plugin2098.util.GameRuleLogic
import sc.plugin2098.util.Connect4Constants
import sc.plugin2098.util.Connect4WinReason
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
): TwoPlayerGameState<Move>(Team.ONE) {
    
    /** Berechnet die Punktwerte für das angegebene [team]. */
    // TODO: Punktebewertung implementieren
    override fun getPointsForTeam(team: ITeam): IntArray =
        intArrayOf(1)

    /** Gibt an, ob das Spiel beendet ist. */
    // TODO: Implementieren
    override val isOver: Boolean
        get() = false
        /*get() = (Team.values().any { GameRuleLogic.isSwarmConnected(board, it) } && turn.mod(2) == 0) ||
                turn / 2 >= Connect4Constants.ROUND_LIMIT*/

    /** Liefert die aktuelle Gewinnbedingung oder null, falls das Spiel noch nicht entschieden ist. */
    // TODO: Win Condition implementieren
    override val winCondition: WinCondition?
        get() =
            if(Team.entries.any { team -> GameRuleLogic.is4Connected(board, team) }) {
//                Team.entries.maxByNoEqual { team -> GameRuleLogic.is4Connected(board, team) }
//                           ?.let { WinCondition(it, Connect4WinReason.BIGGER_SWARM) }
//                       ?: WinCondition(null, WinReasonTie)
                WinCondition(null, WinReasonTie)
            } else {
                null
            }
    
    /** Führt einen gültigen [move] direkt aus und aktualisiert Spielfeld sowie Zähler. */
    override fun performMoveDirectly(move: Move) {
        GameRuleLogic.checkMove(board, move)?.let { throw InvalidMoveException(it, move) }

        if(this.currentTeam == Team.ONE) {
            board[move.position] = FieldState.RED
        } else {
            board[move.position] = FieldState.YELLOW
        }

        turn++
        lastMove = move
    }
    
    /** Gibt alle Züge für den aktuellen Spieler auf dem aktuellen [board] zurück. */
    override fun getSensibleMoves(): List<Move> {
        /*val piranhas = board.filterValues { field -> field.team == currentTeam }
        val moves = ArrayList<Move>(piranhas.siz * 2)
        for(piranha in piranhas) {
            moves.addAll(GameRuleLogic.possibleMovesFor(board, piranha.key))
        }*/

        val moves = ArrayList<Move>(Connect4Constants.BOARD_WIDTH)

        return moves
    }
    
    /** Iteriert über alle sinnvollen Züge des aktuellen Spielers. */
    override fun moveIterator(): Iterator<Move> =
        getSensibleMoves().iterator()
    
    /** Erstellt eine tiefe Kopie dieses Spielzustands inklusive geklontem [board]. */
    override fun clone(): GameState =
        copy(board = board.clone())
    
    /** Ermittelt zusammenfassende Statistiken für das angegebene [team]. */
    // TODO: Implementieren
    override fun teamStats(team: ITeam): List<Stat> =
        listOf(
            /*Stat("Anzahl Fische", board.fieldsForTeam(team).size),*/
            Stat("Größter Schwarm", 1)
        )
    
}
