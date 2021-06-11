package sc.networking.clients

import sc.api.plugins.IGameState
import sc.protocol.responses.ErrorPacket
import sc.protocol.responses.GamePreparedResponse
import sc.protocol.room.RoomMessage
import sc.shared.GameResult

abstract class AbstractLobbyClientListener: ILobbyClientListener {
    override fun onNewState(roomId: String, state: IGameState) {}
    override fun onGameOver(roomId: String, data: GameResult) {}
    override fun onRoomMessage(roomId: String, data: RoomMessage) {}
    
    override fun onError(error: ErrorPacket) {}
    override fun onGamePrepared(response: GamePreparedResponse) {}
    override fun onGameLeft(roomId: String) {}
    override fun onGameJoined(roomId: String) {}
    override fun onGameObserved(roomId: String) {}
}
