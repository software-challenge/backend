package sc.server.helpers

import sc.api.plugins.IGameState
import sc.api.plugins.IMove
import sc.player.IGameHandler
import sc.server.plugins.TestGameState
import sc.shared.GameResult
import java.util.concurrent.CompletableFuture

class TestGameHandler: IGameHandler {
    var state: TestGameState? = null
    var moveRequest: CompletableFuture<IMove>? = null
    var gameResult: GameResult? = null
    var error: String? = null
    
    override fun onUpdate(gameState: IGameState) {
        state = gameState as TestGameState
    }
    
    override fun calculateMove(): IMove {
        moveRequest = CompletableFuture()
        return moveRequest!!.get()
    }
    
    override fun onGameOver(data: GameResult) {
        gameResult = data
    }
    
    override fun onError(error: String) {
        this.error = error
    }
}