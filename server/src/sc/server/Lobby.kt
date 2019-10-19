package sc.server

import org.slf4j.LoggerFactory
import sc.api.plugins.exceptions.RescuableClientException
import sc.protocol.requests.*
import sc.protocol.responses.PlayerScorePacket
import sc.protocol.responses.ProtocolErrorMessage
import sc.protocol.responses.RoomPacket
import sc.protocol.responses.TestModeMessage
import sc.server.gaming.GameRoomManager
import sc.server.gaming.PlayerRole
import sc.server.gaming.ReservationManager
import sc.server.network.Client
import sc.server.network.ClientManager
import sc.server.network.IClientListener
import sc.server.network.PacketCallback
import sc.shared.InvalidGameStateException
import sc.shared.Score
import java.io.IOException

/**
 * The lobby will help clients find an open game or create new games to play with
 * another client.
 */
class Lobby : IClientListener {
    private val logger = LoggerFactory.getLogger(Lobby::class.java)

    val gameManager: GameRoomManager = GameRoomManager()
    val clientManager: ClientManager = ClientManager(this)

    /**
     * Starts the ClientManager in it's own daemon thread. This method should be used only once.
     * ClientManager starts clientListener.
     * clientListener starts SocketListener on defined port to watch for new connecting clients.
     */
    @Throws(IOException::class)
    fun start() {
        this.clientManager.start()
    }

    /**
     * Add lobby as listener to client.
     * Prepare client for send and receive.
     *
     * @param client connected XStreamClient
     */
    fun onClientConnected(client: Client) {
        client.addClientListener(this)
        client.start()
    }

    override fun onClientDisconnected(source: Client) {
        logger.info("{} disconnected.", source)
        source.removeClientListener(this)
    }

    /** Handle requests or moves of clients. */
    @Throws(RescuableClientException::class, InvalidGameStateException::class)
    override fun onRequest(source: Client, callback: PacketCallback) {
        val packet = callback.packet
        if (packet is ILobbyRequest) {
            when (packet) {
                is JoinPreparedRoomRequest -> ReservationManager.redeemReservationCode(source, packet.reservationCode)
                is JoinRoomRequest -> {
                    val gameRoomMessage = this.gameManager.joinOrCreateGame(source, packet.gameType)
                    // null is returned if join was unsuccessful
                    if (gameRoomMessage != null) {
                        for (admin in clientManager.clients) {
                            if (admin.isAdministrator) {
                                admin.send(gameRoomMessage)
                            }
                        }
                    }
                }
                is AuthenticateRequest -> source.authenticate(packet.password)
                is PrepareGameRequest -> if (source.isAdministrator) {
                    source.send(this.gameManager.prepareGame(packet))
                }
                is FreeReservationRequest -> if (source.isAdministrator) {
                    ReservationManager.freeReservation(packet.reservation)
                }
                is RoomPacket -> {
                    // i.e. new move
                    val room = this.gameManager.findRoom(packet.roomId)
                    room.onEvent(source, packet.data)
                }
                is ObservationRequest -> if (source.isAdministrator) {
                    val room = this.gameManager.findRoom(packet.roomId)
                    room.addObserver(source)
                }
                is PauseGameRequest -> if (source.isAdministrator) {
                    try {
                        val room = this.gameManager.findRoom(packet.roomId)
                        room.pause(packet.pause)
                    } catch (e: RescuableClientException) {
                        this.logger.error("Got exception on pause: {}", e)
                    }

                }
                is ControlTimeoutRequest -> if (source.isAdministrator) {
                    val room = this.gameManager.findRoom(packet.roomId)
                    val slot = room.slots[packet.slot]
                    slot.role.player.isCanTimeout = packet.activate
                }
                is StepRequest -> // It is not checked whether there is a prior pending StepRequest
                    if (source.isAdministrator) {
                        val room = this.gameManager.findRoom(packet.roomId)
                        room.step(packet.forced)
                    }
                is CancelRequest -> if (source.isAdministrator) {
                    requireNotNull(packet.roomId) { "Can't cancel a game with roomId null!" }
                    val room = this.gameManager.findRoom(packet.roomId)
                    room.cancel()
                }
                is TestModeRequest -> if (source.isAdministrator) {
                    val testMode = packet.testMode
                    logger.info("Test mode is set to {}", testMode)
                    Configuration.set(Configuration.TEST_MODE, java.lang.Boolean.toString(testMode))
                    source.send(TestModeMessage(testMode))
                }
                is GetScoreForPlayerRequest -> if (source.isAdministrator) {
                    val displayName = packet.displayName
                    val score = getScoreOfPlayer(displayName)
                            ?: throw IllegalArgumentException("Score for \"$displayName\" could not be found!")
                    logger.debug("Sending score of player \"{}\"", displayName)
                    source.send(PlayerScorePacket(score))
                }
                else -> throw RescuableClientException("Unhandled Packet of type: " + packet.javaClass)
            }
            callback.setProcessed()
        }
    }

    private fun getScoreOfPlayer(displayName: String): Score? {
        for (score in this.gameManager.scores) {
            if (score.displayName == displayName) {
                return score
            }
        }
        return null
    }

    fun close() {
        this.clientManager.close()
    }

    override fun onError(source: Client, errorPacket: ProtocolErrorMessage) {
        for (role in source.roles) {
            if (role.javaClass == PlayerRole::class.java) {
                (role as PlayerRole).playerSlot.room.onClientError(errorPacket)
            }
        }
    }
}
