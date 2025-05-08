package sc.server

import sc.api.plugins.IMove
import sc.api.plugins.exceptions.GameRoomException
import sc.api.plugins.exceptions.RescuableClientException
import sc.protocol.ProtocolPacket
import sc.protocol.requests.*
import sc.protocol.room.RoomPacket
import sc.server.gaming.GameRoomManager
import sc.server.gaming.ReservationManager
import sc.server.network.*
import java.io.Closeable
import java.io.IOException

/** The lobby joins clients into a game by finding open rooms or creating new ones. */
class Lobby: GameRoomManager(), Closeable, IClientRequestListener {
    val clientManager = ClientManager(this)
    
    /** @see ClientManager.start */
    @Throws(IOException::class)
    fun start() {
        clientManager.start()
    }
    
    private fun notifyObservers(packet: ProtocolPacket) =
            clientManager.clients
                    .filter(Client::isAdministrator)
                    .forEach { admin -> admin.send(packet) }
    
    /** Handle requests or moves of clients.
     * @throws RescuableClientException if something goes wrong.
     *         Usually results in termination of the connection to the offending client. */
    @Throws(RescuableClientException::class)
    override fun onRequest(source: Client, callback: PacketCallback) {
        when (val packet = callback.packet) {
            is RoomPacket -> {
                // i.e. new move
                val room = this.findRoom(packet.roomId)
                val move = packet.data
                if(move !is IMove)
                    throw GameRoomException("Received non-move packet: $packet")
                room.onEvent(source, move)
            }
            is JoinPreparedRoomRequest ->
                ReservationManager.redeemReservationCode(source, packet.reservationCode)
            is JoinRoomRequest ->
                if(!this.findRoom(packet.roomId).join(source))
                    throw GameRoomException("Room ${packet.roomId} is already full!")
            is JoinGameRequest -> {
                joinOrCreateGame(source, packet.gameType)
                        ?.let { notifyObservers(it) }
            }
            is AuthenticateRequest -> source.authenticate(packet.password)
            is AdminLobbyRequest -> {
                if (!source.isAdministrator)
                    throw UnauthenticatedException(packet)
                when (packet) {
                    is PrepareGameRequest -> {
                        source.send(this.prepareGame(packet))
                    }
                    is ObservationRequest -> {
                        val room = this.findRoom(packet.roomId)
                        room.addObserver(source)
                    }
                    is PauseGameRequest -> {
                        val room = this.findRoom(packet.roomId)
                        room.pause(packet.pause)
                    }
                    is StepRequest -> {
                        // TODO check for a prior pending StepRequest
                        val room = this.findRoom(packet.roomId)
                        room.step(packet.forced)
                    }
                    is CancelRequest -> {
                        requireNotNull(packet.roomId) { "Can't cancel a game with roomId null!" }
                        val room = this.findRoom(packet.roomId)
                        room.cancel()
                    }
                }
            }
            else -> throw RescuableClientException("Unhandled Packet of type: " + packet.javaClass)
        }
        callback.setProcessed()
    }
    
    override fun close() = clientManager.close()
}
