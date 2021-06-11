package sc.networking.clients

import sc.api.plugins.IGameState
import sc.protocol.room.ErrorMessage
import sc.shared.GameResult

interface IHistoryListener {
    fun onNewState(roomId: String, state: IGameState)
    fun onGameOver(roomId: String, result: GameResult) {}
    fun onGameError(roomId: String, error: ErrorMessage) {}
}