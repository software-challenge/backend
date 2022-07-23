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
    
    override fun getPointsForTeam(team: ITeam): IntArray {
        TODO("Not yet implemented")
    }
    
    override fun getPossibleMoves(): Collection<IMove> {
        TODO("Not yet implemented")
    }
    
    override val round get() = turn / 2
    
    val red = Player(Team.ONE)
    val blue = Player(Team.TWO)
    
    override fun clone() = TestGameState(turn, state)
}
