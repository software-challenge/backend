package sc.player

import sc.networking.clients.IClient
import sc.protocol.requests.JoinGameRequest
import sc.protocol.requests.JoinPreparedRoomRequest
import sc.protocol.requests.JoinRoomRequest
import sc.protocol.room.ErrorMessage
import sc.protocol.room.MementoMessage
import sc.protocol.room.MoveRequest
import sc.protocol.room.RoomMessage
import sc.shared.GameResult
import java.util.function.Function

interface IPlayerClient {
    fun joinGameWithReservation(reservation: String)
    fun joinGameRoom(roomId: String)
    /** Join any game with the appropriate [gameType]. */
    fun joinGame(gameType: String?)
}

/**
 * Verbindet die Server-Kommunikation mit der Logik der Spieler.
 */
class PlayerClient(
        private val client: IClient,
        private val handler: IGameHandler,
): Function<RoomMessage, RoomMessage?>, IPlayerClient {
    
    override fun apply(msg: RoomMessage): RoomMessage? {
        when (msg) {
            is MoveRequest -> return handler.calculateMove()
            is MementoMessage -> handler.onUpdate(msg.state)
            is GameResult -> handler.onGameOver(msg)
            is ErrorMessage -> handler.onError(msg.logMessage)
        }
        return null
    }
    
    override fun joinGameWithReservation(reservation: String) {
        client.send(JoinPreparedRoomRequest(reservation))
    }
    
    override fun joinGameRoom(roomId: String) {
        client.send(JoinRoomRequest(roomId))
    }
    
    /** Join any game with the appropriate [gameType]. */
    override fun joinGame(gameType: String?) {
        client.send(JoinGameRequest(gameType))
    }
}
