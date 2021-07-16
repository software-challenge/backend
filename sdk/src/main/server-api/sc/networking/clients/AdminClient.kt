package sc.networking.clients

import sc.protocol.requests.ObservationRequest
import sc.protocol.requests.PrepareGameRequest
import sc.protocol.room.ObservableRoomMessage

class AdminClient(private val client: LobbyClient) {
    val closed: Boolean
        get() = client.isClosed
    
    fun prepareGame(request: PrepareGameRequest) {
        client.send(request)
    }
    
    /** Returns an [IGameController] to control the given room. */
    fun control(roomId: String): IGameController =
            GameController(roomId, client)
    
    /** Registers [listener] onto the given room. */
    fun observe(roomId: String, listener: (ObservableRoomMessage) -> Unit) {
        client.observeRoom(roomId, listener)
        client.send(ObservationRequest(roomId))
    }
    
}