package sc.server.client

import sc.api.plugins.IGameState
import sc.framework.plugins.Player
import sc.networking.clients.ILobbyClientListener
import sc.protocol.responses.GamePreparedResponse
import sc.protocol.responses.ProtocolErrorMessage
import sc.protocol.responses.ProtocolMessage
import sc.shared.GameResult

class TestPreparedGameResponseListener : ILobbyClientListener {
    lateinit var response: GamePreparedResponse

    override fun onGamePrepared(response: GamePreparedResponse) {
        this.response = response
    }

    override fun onNewState(roomId: String, state: IGameState) {}
    override fun onError(roomId: String, error: ProtocolErrorMessage) {}
    override fun onRoomMessage(roomId: String, data: ProtocolMessage) {}
    override fun onGameLeft(roomId: String) {}
    override fun onGameJoined(roomId: String) {}
    override fun onGameOver(roomId: String, data: GameResult) {}
    override fun onGamePaused(roomId: String, nextPlayer: Player) {}
    override fun onGameObserved(roomId: String) {}
}
