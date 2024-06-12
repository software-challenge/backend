package sc.server.plugins

import sc.api.plugins.IGameState
import sc.api.plugins.IMove
import sc.api.plugins.ITeam
import sc.api.plugins.Team
import sc.framework.plugins.Player

data class TestGameState(
        override var turn: Int = 0,
        var state: Int = 0,
): IGameState {
    
    override val currentTeam: Team
        get() = Team.values()[turn % Team.values().size]
    
    @Transient
    override val isOver = false
    
    override fun getPointsForTeam(team: ITeam): IntArray =
            intArrayOf(team.index, turn)
    
    override fun moveIterator(): Iterator<IMove> =
            throw NotImplementedError("TestGame has no Moves")
    
    override val round get() = turn / 2
    
    val red = Player(Team.ONE, "Fred")
    val blue = Player(Team.TWO, "Marta")
    
    override fun clone() = TestGameState(turn, state)
}
