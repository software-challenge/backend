package sc.server.plugins

import sc.api.plugins.IGameState
import sc.framework.plugins.Player
import sc.server.helpers.TestTeam

data class TestGameState(
        override var turn: Int = 0,
        var state: Int = 0,
): IGameState {
    override val currentTeam: TestTeam
        get() = TestTeam.values()[turn % TestTeam.values().size]
    override val round get() = turn / 2
    val red = Player(TestTeam.RED)
    val blue = Player(TestTeam.BLUE)
    
    override fun clone() = TestGameState(turn, state)
}
