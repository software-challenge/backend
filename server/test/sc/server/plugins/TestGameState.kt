package sc.server.plugins

import sc.api.plugins.IGameState
import sc.shared.Team

class TestGameState: IGameState {
    override var turn = 0
    var state = 0
    var lastPlayerIndex = 0
    var currentPlayer: Team
    var startPlayer: Team
    
    var red: TestPlayer
    var blue: TestPlayer
    
    init {
        this.currentPlayer = Team.ONE
        this.startPlayer = Team.ONE
        this.red = TestPlayer(Team.ONE)
        this.blue = TestPlayer(Team.TWO)
    }
    
    /** wechselt den Spieler, der aktuell an der Reihe ist anhand von `turn`  */
    fun switchCurrentPlayer() {
        currentPlayer = if (turn % 2 == 0) {
            Team.ONE
        } else {
            Team.TWO
        }
    }
    
}
