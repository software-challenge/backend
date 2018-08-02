package sc.server.plugins

import sc.api.plugins.IGameState
import sc.shared.PlayerColor

class TestGameState : IGameState {
    override var turn = 0
    var state = 0
    var lastPlayerIndex = 0
    var currentPlayer: PlayerColor
    var startPlayer: PlayerColor

    var red: TestPlayer
    var blue: TestPlayer

    init {
        this.currentPlayer = PlayerColor.RED
        this.startPlayer = PlayerColor.RED
        this.red = TestPlayer(PlayerColor.RED)
        this.blue = TestPlayer(PlayerColor.BLUE)
    }

    /** wechselt den Spieler, der aktuell an der Reihe ist anhand von `turn`  */
    fun switchCurrentPlayer() {
        currentPlayer = if (turn % 2 == 0) {
            PlayerColor.RED
        } else {
            PlayerColor.BLUE
        }
    }

}
