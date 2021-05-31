package sc.server

import org.slf4j.LoggerFactory
import sc.api.plugins.IMove
import sc.api.plugins.exceptions.GameRoomException
import sc.api.plugins.exceptions.RescuableClientException
import sc.protocol.ProtocolPacket
import sc.protocol.requests.*
import sc.protocol.responses.PlayerScoreResponse
import sc.protocol.responses.TestModeResponse
import sc.protocol.room.RoomPacket
import sc.server.gaming.GameRoomManager
import sc.server.gaming.ReservationManager
import sc.server.network.*
import sc.shared.Score
import java.io.Closeable
import java.io.IOException

/** The lobby joins clients into a game by finding open rooms or creating new ones. */
class Lobby: GameRoomManager(), Closeable, IClientRequestListener {
    private val logger = LoggerFactory.getLogger(Lobby::class.java)
    
    val clientManager = ClientManager(this)
    
    /** @see ClientManager.start */
    @Throws(IOException::class)
    fun start() {
        clientManager.start()
    }
    
    private fun notifyObservers(packet: ProtocolPacket) =
            clientManager.clients
                    .filter { it.isAdministrator }
                    .forEach { it.send(packet) }
    
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
                    is PlayerScoreRequest -> {
                        val displayName = packet.displayName
                        val score = getScoreOfPlayer(displayName)
                                    ?: throw IllegalArgumentException("Score for \"$displayName\" could not be found!")
                        logger.debug("Sending score of player \"{}\"", displayName)
                        source.send(PlayerScoreResponse(score))
                    }
                    is TestModeRequest -> {
                        val testMode = packet.testMode
                        logger.info("Setting Test mode to {}", testMode)
                        Configuration.set(Configuration.TEST_MODE, testMode.toString())
                        source.send(TestModeResponse(testMode))
                    }
                }
            }
            else -> throw RescuableClientException("Unhandled Packet of type: " + packet.javaClass)
        }
        callback.setProcessed()
    }
    
    private fun getScoreOfPlayer(displayName: String): Score? =
            scores.find { it.displayName == displayName }
    
    override fun close() = clientManager.close()
}
