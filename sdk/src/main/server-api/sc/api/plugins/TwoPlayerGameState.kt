package sc.api.plugins

import com.thoughtworks.xstream.annotations.XStreamAsAttribute

const val SENSIBLE_MOVES_COUNT = 64

abstract class TwoPlayerGameState<M: IMove>(
        @XStreamAsAttribute
        override val startTeam: Team
) : IGameState {
    
    abstract val board: IBoard
    
    /** @return das Team, das am Zug ist. */
    override val currentTeam
        get() = currentTeamFromTurn()
    
    /** @return das Team, das nicht dran ist. */
    val otherTeam
        get() = currentTeam.opponent()

    /** Letzter getaetigter Zug. */
    abstract val lastMove: M?
    
    /**
     * Führt den gegebenen Zug in einer Kopie dieses Gamestate aus.
     * @return neuer GameState nach Ausführung des Zuges
     * */
    fun performMove(move: M): TwoPlayerGameState<M> =
        clone().also { it.performMoveDirectly(move) }
    
    /**
     * Führt den Zug direkt in diesem Spielstatus aus.
     *
     * Achtung: Wenn beim Ausführen des Zugs ein Fehler auftritt,
     * den GameState nicht weiter verwenden!
     * Primär für interne Verwendung -
     * generell wird [performMove] empfohlen.
     * */
    abstract fun performMoveDirectly(move: M)
    
    /**
     * Mögliche Züge des aktuellen Teams in der aktuellen Situation.
     * Bei manchen Spielen wird aufgrund der unüberschaubaren Zahl möglicher Züge
     * nur ein Ausschnitt zurückgegeben.
     * */
    open fun getSensibleMoves(): List<M> = moves().take(SENSIBLE_MOVES_COUNT)
    
    /**
     * Gibt progressiv alle möglichen Züge in der aktuellen Spielsituation zurück.
     * Sinnvollere Züge kommen tendenziell früher.
     * */
    abstract override fun moveIterator(): Iterator<M>
    
    /** Eine Instanz von Iterable, die [moveIterator] zurückgibt.
     * Für Zugriff auf Hilfsfunktionen. */
    fun moves(): Iterable<M> = object: Iterable<M> {
        override fun iterator(): Iterator<M> = moveIterator()
    }
    
    abstract override fun clone(): TwoPlayerGameState<M>
    
    /** Calculates the [Team] of the current player from [turn] and [startTeam].
     * Based on the assumption that the current player switches every turn. */
    protected fun currentTeamFromTurn(): Team =
            if(turn.mod(2) == 0) startTeam else startTeam.opponent()

    override fun toString() =
            "GameState(turn=$turn,currentTeam=$currentTeam)"
    
    open fun longString() =
            "$this\nLast Move: $lastMove\n$board"
    
}