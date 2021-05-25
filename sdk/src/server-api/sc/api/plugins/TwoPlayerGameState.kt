package sc.api.plugins

import com.thoughtworks.xstream.annotations.XStreamAsAttribute
import sc.framework.plugins.Player

abstract class TwoPlayerGameState<P : Player>(
        @XStreamAsAttribute val startTeam: ITeam
) : IGameState {
    
    abstract val first: P
    abstract val second: P
    abstract val board: IBoard
    
    /** Der Spieler, der das Spiel begonnen hat. */
    val startPlayer: P
        get() = getPlayer(startTeam)
    
    /** Liste aller Spieler in Reihenfolge. */
    val players: List<P>
        get() = listOf(first, second)
    
    /** @returns die Namen aller Spieler in Reihenfolge. */
    val playerNames: Array<String>
        get() = arrayOf(first.displayName, second.displayName)
    
    /** @return das Team, das dran ist. */
    abstract val currentTeam: ITeam
    
    /** @return das Team, das nicht dran ist. */
    val otherTeam: ITeam
        get() = currentTeam.opponent()
    
    /** Der Spieler, der am Zug ist. */
    open val currentPlayer: P
        get() = getPlayer(currentTeam)

    /** Der Spieler, der nicht am Zug ist. */
    val otherPlayer: P
        get() = getPlayer(otherTeam)

    /** Letzter getaetigter Zug. */
    abstract val lastMove: IMove?

    fun getOpponent(player: P) =
            getPlayer(player.color.opponent())

    fun getPlayer(team: ITeam): P = players[team.index]
    
    /** Calculates the color of the current player from the [turn] and the [startTeam].
     * Based on the assumption that the current player switches every turn. */
    fun currentPlayerFromTurn(): ITeam =
            if(turn.rem(2) == 0) startTeam else startTeam.opponent()

    /** Gibt die angezeigte Punktzahl des Spielers zurueck. */
    abstract fun getPointsForPlayer(team: ITeam): Int

    override fun toString() =
            "GameState(turn=$turn,currentPlayer=${currentPlayer.color})"
    
}