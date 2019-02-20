package sc.server

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import sc.api.plugins.exceptions.RescuableClientException
import sc.protocol.requests.*
import sc.protocol.responses.PlayerScorePacket
import sc.protocol.responses.ProtocolMessage
import sc.protocol.responses.RoomPacket
import sc.protocol.responses.TestModeMessage
import sc.server.gaming.ReservationManager
import kotlin.coroutines.CoroutineContext

fun CoroutineScope.createLobby(output: SendChannel<ProtocolMessage>, context: CoroutineContext = this.coroutineContext):
        SendChannel<ILobbyRequest> = actor(context = context, capacity = Channel.UNLIMITED, start = CoroutineStart.LAZY) {
    LobbyActor(this, channel, output).start()
}


/**
 * RPC Actor implementation.
 *
 * @param scope the scope for this actor to act in.
 * @param input the actor's input channel.
 * @param output the actor's output channel.
 */
private class LobbyActor(
        private val scope: CoroutineScope,
        private val input: ReceiveChannel<ILobbyRequest>,
        private val output: SendChannel<ProtocolMessage>) {

    /**
     * Start the actor.
     */
    suspend fun start() {
        for (m in input) onReceive(m)
    }

    private suspend fun onReceive(request: ILobbyRequest) {
        when (request) {
            is JoinPreparedRoomRequest -> ReservationManager.redeemReservationCode(source, request.reservationCode)
            is JoinRoomRequest -> {
                val gameRoomMessage = this.gameManager.joinOrCreateGame(source, request.gameType)
                if (gameRoomMessage != null) {
                    for (admin in clientManager.clients) {
                        if (admin.isAdministrator) {
                            admin.send(gameRoomMessage)
                        }
                    }
                }
            }
            is AuthenticateRequest -> source.authenticate(request.password)
            is PrepareGameRequest -> if (source.isAdministrator) {
                source.send(this.gameManager.prepareGame(request))
            }
            is FreeReservationRequest -> if (source.isAdministrator) {
                ReservationManager.freeReservation(request.reservation)
            }
            is RoomPacket -> {
                // i.e. new move
                val room = this.gameManager.findRoom(request.roomId)
                room.onEvent(source, request.data)
            }
            is ObservationRequest -> if (source.isAdministrator) {
                val room = this.gameManager.findRoom(request.roomId)
                room.addObserver(source)
            }
            is PauseGameRequest -> if (source.isAdministrator) {
                try {
                    val room = this.gameManager.findRoom(request.roomId)
                    room.pause(request.pause)
                } catch (e: RescuableClientException) {
                    this.logger.error("Got exception on pause: {}", e)
                }

            }
            is ControlTimeoutRequest -> if (source.isAdministrator) {
                val room = this.gameManager.findRoom(request.roomId)
                val slot = room.slots[request.slot]
                slot.role.player.isCanTimeout = request.activate

            }
            is StepRequest -> // It is not checked whether there is a prior pending StepRequest
                if (source.isAdministrator) {
                    val room = this.gameManager.findRoom(request.roomId)
                    room.step(request.forced)
                }
            is CancelRequest -> if (source.isAdministrator) {
                val room = this.gameManager.findRoom(request.roomId)
                room.cancel()
                // TODO check whether all clients receive game over message
                this.gameManager.games.remove(room)
            }
            is TestModeRequest -> if (source.isAdministrator) {
                val testMode = request.testMode
                logger.info("Test mode is set to {}", testMode)
                Configuration.set(Configuration.TEST_MODE, java.lang.Boolean.toString(testMode))
                source.send(TestModeMessage(testMode))
            }
            is GetScoreForPlayerRequest -> if (source.isAdministrator) {
                val displayName = request.displayName
                val score = getScoreOfPlayer(displayName)
                        ?: throw IllegalArgumentException("Score for \"$displayName\" could not be found!")
                logger.debug("Sending score of player \"{}\"", displayName)
                source.send(PlayerScorePacket(score))
            }
            else -> throw RescuableClientException("Unhandled Packet of type: " + request.javaClass)
        }
    }
}