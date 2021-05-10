package sc.networking.clients

import sc.protocol.requests.FreeReservationRequest
import sc.protocol.requests.ObservationRequest
import sc.protocol.requests.PrepareGameRequest
import sc.protocol.room.RoomMessage

class AdminClient(private val client: LobbyClient) {
    
    fun prepareGame(request: PrepareGameRequest) {
        client.send(request)
    }
    
    /** Returns an [IGameController] to control the given room. */
    fun control(roomId: String): IGameController =
            GameController(roomId, client)
    
    /** Registers [listener] onto the given room. */
    fun observe(roomId: String, listener: (RoomMessage) -> Unit) {
        client.observeRoom(roomId, listener)
        client.send(ObservationRequest(roomId))
    }
    
    /** Opens up a previously reserved slot. */
    fun freeReservation(reservation: String) {
        client.send(FreeReservationRequest(reservation))
    }
    
}