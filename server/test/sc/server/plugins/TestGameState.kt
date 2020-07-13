package sc.server.plugins

import sc.api.plugins.IGameState
import sc.shared.ITeam
import sc.server.helpers.TestTeam

class TestGameState: IGameState {
    override var turn = 0
    var state = 0
    var lastPlayerIndex = 0
    var currentPlayer: ITeam
    var startPlayer: ITeam
    
    var red: TestPlayer
    var blue: TestPlayer
    
    init {
        this.currentPlayer = TestTeam.RED
        this.startPlayer = TestTeam.RED
        this.red = TestPlayer(TestTeam.RED)
        this.blue = TestPlayer(TestTeam.BLUE)
    }
    
    /** wechselt den Spieler, der aktuell an der Reihe ist anhand von `turn`  */
    fun switchCurrentPlayer() {
        currentPlayer = if (turn % 2 == 0) {
            TestTeam.RED
        } else {
            TestTeam.BLUE
        }
    }
    
}
