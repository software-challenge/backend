package sc.plugin2024

import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import com.thoughtworks.xstream.annotations.XStreamImplicit
import com.thoughtworks.xstream.annotations.XStreamOmitField
import sc.api.plugins.*
import sc.plugin2024.actions.Acceleration
import sc.plugin2024.exceptions.MoveException
import sc.plugin2024.util.PluginConstants
import sc.shared.InvalidMoveException

/**
 * Der aktuelle Spielstand.
 *
 * Er hält alle Informationen zur momentanen Runde,
 * mit deren Hilfe der nächste Zug berechnet werden kann.
 */
@XStreamAlias(value = "state")
data class GameState @JvmOverloads constructor(
        /** Das aktuelle Spielfeld. */
        override val board: Board = Board(),
        /** Die Anzahl an bereits getätigten Zügen. */
        @XStreamAsAttribute override var turn: Int = 0,
        /** Der zuletzt gespielte Zug. */
        override var lastMove: Move? = null,
): TwoPlayerGameState<Move>(Team.ONE) {
    
    override val currentTeam: Team
        get() = currentTeamFromTurn().run { takeIf { !immovable(it) } ?: opponent() }
    
    override val otherTeam: Team
        get() {
            TODO()
        }
    
    /**
     * Der Index des am weitesten vom Start entfernten Segmentes, welches bisher aufgedeckt wurde. Wird nur intern verwendet.
     */
    @XStreamOmitField
    private val latestTileIndex = 0
    
    /**
     * Wurde der Spieler im LastMove abgedrängt. Falls ja ist eine weitere Drehaktion möglich
     */
    @XStreamAsAttribute
    private val freeTurn = false
    
    /**
     * Liste von Aktionen aus denen der Zug besteht. Die Reihenfolge, in der die
     * Aktionen ausgeführt werden, wird NICHT durch die Reihenfolge in der Liste
     * bestimmt, sondern durch die Werte im order-Attribut jedes Action objektes:
     * Die Aktionen werden nach dem order-Attribut aufsteigend sortiert
     * ausgeführt.
     */
    @XStreamImplicit
    var actions: List<Action>? = null
    
    override fun performMove(move: Move) {
    
    }
    
    override fun getSensibleMoves(): List<IMove> {
        // TODO
    }
    
    fun getPossibleActions(): List<Action> {
    
    }
    
    private fun immovable(ship: ITeam) = true
    
    override val isOver: Boolean
        get() = immovable()
    
    /** Berechne die Punkteanzahl für das gegebene Team. */
    override fun getPointsForTeam(team: ITeam): IntArray {}
    
    override fun clone() = GameState()
    
    override fun toString(): String =
            ""
    
    override fun equals(other: Any?): Boolean {
    
    }
    
    override fun hashCode(): Int {
    
    }
    
}
