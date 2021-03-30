package sc.networking.clients

import sc.api.plugins.IGameState
import sc.framework.plugins.Player
import sc.protocol.responses.GamePreparedResponse
import sc.protocol.responses.ProtocolErrorMessage
import sc.protocol.responses.ProtocolMessage
import sc.shared.GameResult

abstract class AbstractLobbyClientListener: ILobbyClientListener {
    override fun onNewState(roomId: String, state: IGameState) {}
    override fun onGameOver(roomId: String, data: GameResult) {}
    override fun onGamePaused(roomId: String, nextPlayer: Player) {}
    override fun onRoomMessage(roomId: String, data: ProtocolMessage) {}
    override fun onError(roomId: String?, error: ProtocolErrorMessage) {}
    
    override fun onGamePrepared(response: GamePreparedResponse) {}
    override fun onGameLeft(roomId: String) {}
    override fun onGameJoined(roomId: String) {}
    override fun onGameObserved(roomId: String) {}
}
