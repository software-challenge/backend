package sc.networking.clients

import sc.protocol.requests.FreeReservationRequest
import sc.protocol.requests.ObservationRequest
import sc.protocol.requests.PrepareGameRequest
import sc.shared.SlotDescriptor

class AdminClient(private val client: XStreamClient) {
    
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
    
    /** Takes control of the game in the given room and pauses it.  */
    fun observeAndControl(roomId: String): IControllableGame {
        val controller = observeAndControl(roomId, true)
        controller.pause()
        return controller
    }
    
    /** Takes control of the game in the given room.
     * @param isPaused whether the game to observe is already paused.
     */
    fun observeAndControl(roomId: String, isPaused: Boolean): IControllableGame {
        val controller = ControllingClient(client, roomId, isPaused)
        requestObservation(roomId)
        return controller
    }
    
    fun observe(roomId: String): ObservingClient {
        return observe(roomId, false)
    }
    
    fun observe(roomId: String, isPaused: Boolean): ObservingClient {
        val observer = ObservingClient(roomId, isPaused)
        requestObservation(roomId)
        return observer
    }
    
    private fun requestObservation(roomId: String) {
        client.send(ObservationRequest(roomId))
    }
    
    fun freeReservation(reservation: String) {
        client.send(FreeReservationRequest(reservation))
    }
    
}