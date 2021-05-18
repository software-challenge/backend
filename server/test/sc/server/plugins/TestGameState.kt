package sc.server.plugins

import sc.api.plugins.IGameState
import sc.api.plugins.ITeam
import sc.framework.plugins.Player
import sc.server.helpers.TestTeam

data class TestGameState(
        override var turn: Int = 0,
        var state: Int = 0,
        override var currentTeam: TestTeam = TestTeam.RED,
        val startPlayer: TestTeam = TestTeam.RED
): IGameState {
    override val round get() = turn / 2
    val red = Player(TestTeam.RED)
    val blue = Player(TestTeam.BLUE)
}
