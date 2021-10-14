package sc.networking.clients

import sc.api.plugins.IGameState
import sc.protocol.responses.ErrorPacket
import sc.protocol.responses.GamePreparedResponse
import sc.protocol.room.RoomMessage
import sc.shared.GameResult

// FIXME this is bound to a room anyways, can remove roomId from all signatures
/** Receives updates within a GameRoom on the client-side. */
interface ILobbyClientListener {
    fun onNewState(roomId: String, state: IGameState)
    fun onGameOver(roomId: String, data: GameResult)
    fun onRoomMessage(roomId: String, data: RoomMessage)
    fun onError(error: ErrorPacket)
    
    fun onGamePrepared(response: GamePreparedResponse)
    fun onGameJoined(roomId: String)
    fun onGameObserved(roomId: String)
}
