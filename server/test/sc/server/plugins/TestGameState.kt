package sc.server.plugins

import sc.api.plugins.IGameState
import sc.api.plugins.ITeam
import sc.server.helpers.TestTeam

class TestGameState: IGameState {
    override var turn = 0
    override val round get() = turn / 2
    var state = 0
    var lastPlayerIndex = 0
    var currentPlayer = TestTeam.RED
    val startPlayer = TestTeam.RED
    val red = TestPlayer(TestTeam.RED)
    val blue = TestPlayer(TestTeam.BLUE)
    
    /** wechselt den Spieler, der aktuell an der Reihe ist anhand von `turn`  */
    fun switchCurrentPlayer() {
        currentPlayer = TestTeam.values()[(turn + startPlayer.index) % 2]
    }
    
}
