package sc.networking.clients

import sc.protocol.ProtocolPacket
import sc.protocol.requests.AuthenticateRequest
import sc.protocol.requests.FreeReservationRequest
import sc.protocol.requests.ObservationRequest
import sc.protocol.requests.PrepareGameRequest
import sc.shared.SlotDescriptor

class AdminClient(host: String, port: Int, password: String, private val messageHandler: (ProtocolPacket) -> Unit): XStreamClient(createTcpNetwork(host, port)) {
    
    init {
        start()
        send(AuthenticateRequest(password))
    }
    
    override fun onObject(packet: ProtocolPacket) {
        messageHandler(packet)
    }
    
    fun prepareGame(gameType: String) {
        send(PrepareGameRequest(gameType))
    }
    
    fun prepareGame(gameType: String, startPaused: Boolean) {
        send(PrepareGameRequest(
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
        val controller = ControllingClient(this, roomId, isPaused)
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
        start()
        send(ObservationRequest(roomId))
    }
    
    fun freeReservation(reservation: String) {
        send(FreeReservationRequest(reservation))
    }
    
}