package sc.server.plugins

import sc.api.plugins.IGameState
import sc.framework.plugins.Player
import sc.server.helpers.TestTeam

data class TestGameState(
        override var turn: Int = 0,
        var state: Int = 0,
        var currentPlayer: TestTeam = TestTeam.RED,
        val startPlayer: TestTeam = TestTeam.RED,
        val red: Player = Player(TestTeam.RED),
        val blue: Player = Player(TestTeam.BLUE),
): IGameState {
    override val round get() = turn / 2
    
    /** wechselt den Spieler, der aktuell an der Reihe ist anhand von `turn`  */
    fun switchCurrentPlayer() {
        currentPlayer = TestTeam.values()[(turn + startPlayer.index) % 2]
    }
    
    override fun clone() = TestGameState(turn, state, currentPlayer, startPlayer, red, blue)
}
