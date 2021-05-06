package sc.networking.clients

import sc.protocol.requests.FreeReservationRequest
import sc.protocol.requests.ObservationRequest
import sc.protocol.requests.PrepareGameRequest
import sc.protocol.room.RoomMessage
import sc.shared.SlotDescriptor

class AdminClient(private val client: LobbyClient) {
    
    fun prepareGame(gameType: String) {
        client.send(PrepareGameRequest(gameType))
    }
    
    fun prepareGame(gameType: String, startPaused: Boolean) {
        client.send(PrepareGameRequest(
                gameType,
                SlotDescriptor("player1", false),
                SlotDescriptor("player2", false),
                startPaused)
        )
    }
    
    /** Takes control of the game in the given room.
     * @param isPaused whether the game to observe is already paused.
     */
    fun observeAndControl(roomId: String, isPaused: Boolean): IControllableGame {
        val controller = ControllingClient(client, roomId, isPaused)
        observe(roomId) {}
        return controller
    }
    
    fun observe(roomId: String, listener: (RoomMessage) -> Unit) {
        client.observeRoom(roomId, listener)
        client.send(ObservationRequest(roomId))
    }
    
    fun freeReservation(reservation: String) {
        client.send(FreeReservationRequest(reservation))
    }
    
}