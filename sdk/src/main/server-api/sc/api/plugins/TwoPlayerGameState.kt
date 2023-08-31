package sc.api.plugins

import com.thoughtworks.xstream.annotations.XStreamAsAttribute

abstract class TwoPlayerGameState<M: IMove>(
        @XStreamAsAttribute
        val startTeam: Team
) : IGameState {
    
    abstract val board: IBoard
    
    override val round: Int
        get() = (turn+1)/2
    
    /** @return das Team, das am Zug ist. */
    override val currentTeam
        get() = currentTeamFromTurn()
    
    /** @return das Team, das nicht dran ist. */
    val otherTeam
        get() = currentTeam.opponent()

    /** Letzter getaetigter Zug. */
    abstract val lastMove: M?
    
    /** Führe den gegebenen Zug in einer Kopie dieses Gamestate aus und gib ihn zurück. */
    fun performMove(move: M): IGameState =
        clone().also { it.performMoveDirectly(move) }
    
    /** Performs the Move on this GameState.
     * Might lead to inconsistent state for invalid Move! */
    abstract fun performMoveDirectly(move: M)
    
    /**
     * Mögliche Züge des aktuellen Teams in der aktuellen Situation.
     * Bei manchen Spielen wird aufgrund der unüberschaubaren Zahl möglicher Züge
     * nur ein Ausschnitt zurückgegeben.
     * */
    open fun getSensibleMoves(): List<M> = moves().take(64)
    
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
            if(turn.rem(2) == 0) startTeam else startTeam.opponent()

    override fun toString() =
            "GameState(turn=$turn,currentTeam=$currentTeam)"
    
    open fun longString() =
            "$this\nLast Move: $lastMove\n$board"
    
}