package sc.networking.clients

import sc.api.plugins.IGameState
import sc.framework.plugins.Player
import sc.protocol.RoomMessage
import sc.protocol.responses.GamePreparedResponse
import sc.protocol.responses.ErrorPacket
import sc.shared.GameResult

// FIXME this is bound to a room anyways, can remove roomId from all signatures
/** Receives updates within a GameRoom on the client-side. */
interface ILobbyClientListener {
    fun onNewState(roomId: String, state: IGameState)
    fun onGameOver(roomId: String, data: GameResult)
    fun onGamePaused(roomId: String, nextPlayer: Player)
    fun onRoomMessage(roomId: String, data: RoomMessage)
    fun onError(error: ErrorPacket)
    
    fun onGamePrepared(response: GamePreparedResponse)
    fun onGameLeft(roomId: String)
    fun onGameJoined(roomId: String)
    fun onGameObserved(roomId: String)
}
