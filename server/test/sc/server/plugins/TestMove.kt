package sc.server.plugins

import sc.api.plugins.IMove
import sc.server.helpers.TestTeam

data class TestMove(private val value: Int) : IMove {
    fun perform(state: TestGameState) {
        state.state = value
        state.turn++
    }
}