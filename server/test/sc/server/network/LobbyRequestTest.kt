package sc.server.network

import io.kotest.assertions.timing.eventually
import io.kotest.assertions.until.Interval
import io.kotest.assertions.until.fibonacci
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import sc.framework.plugins.protocol.MoveRequest
import sc.networking.clients.LobbyClient
import sc.protocol.ResponsePacket
import sc.protocol.requests.JoinPreparedRoomRequest
import sc.protocol.requests.PrepareGameRequest
import sc.protocol.responses.ErrorPacket
import sc.protocol.responses.GamePreparedResponse
import sc.protocol.responses.ObservationResponse
import sc.protocol.room.ErrorMessage
import sc.protocol.room.GamePaused
import sc.protocol.room.MementoMessage
import sc.protocol.room.ObservableRoomMessage
import sc.server.client.MessageListener
import sc.server.client.PlayerListener
import sc.server.gaming.GameRoom
import sc.server.helpers.TestTeam
import sc.server.plugins.TestGame
import sc.server.plugins.TestMove
import sc.server.plugins.TestPlugin
import sc.shared.GameResult
import sc.shared.SlotDescriptor
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds
import kotlin.time.seconds

@ExperimentalTime
suspend fun await(clue: String? = null, duration: Duration = 1.seconds, interval: Interval = 20.milliseconds.fibonacci(), f: suspend () -> Unit) =
        withClue(clue) { eventually(duration, interval, f) }

@ExperimentalTime
class LobbyRequestTest: WordSpec({
    "A Lobby with connected clients" When {
        val testLobby = autoClose(TestLobby())
        val lobby = testLobby.lobby
    
        val adminListener = MessageListener<ResponsePacket>()
        val lobbyClient = LobbyClient("localhost", testLobby.serverPort)
        val admin = lobbyClient.authenticate(PASSWORD, adminListener::addMessage)
        fun prepareGame(request: PrepareGameRequest): GamePreparedResponse {
            admin.prepareGame(request)
            val prepared = adminListener.waitForMessage(GamePreparedResponse::class)
            lobby.games shouldHaveSize 1
            return prepared
        }
        
        val players = Array(2) { testLobby.connectClient() }
        await("Clients connected") { lobby.clientManager.clients.size shouldBe 3 }
        "a player joined" should {
            players[0].joinGame(TestPlugin.TEST_PLUGIN_UUID)
            "create a room for it" {
                await("Room opened") { lobby.games.size shouldBe 1 }
                lobby.games.single().clients shouldHaveSize 1
            }
        }
        "a game is prepared paused" should {
            val prepared = prepareGame(PrepareGameRequest(TestPlugin.TEST_PLUGIN_UUID, pause = true))
            val roomId = prepared.roomId
            val room = lobby.findRoom(roomId)
            withClue("GameRoom is empty and paused") {
                room.clients.shouldBeEmpty()
                room.isPauseRequested shouldBe true
            }
            
            val reservations = prepared.reservations
            players[0].joinGameWithReservation(reservations[0])
            await("First player joined") { room.clients shouldHaveSize 1 }
            "not accept a reservation twice" {
                lobbyClient.send(JoinPreparedRoomRequest(reservations[0]))
                adminListener.waitForMessage(ErrorPacket::class)
                room.clients shouldHaveSize 1
                lobby.games shouldHaveSize 1
            }
            players[1].joinGameWithReservation(reservations[1])
            await("Players join, Game start") { room.status shouldBe GameRoom.GameStatus.ACTIVE }
            
            val playerListeners = room.slots.map { slot ->
                PlayerListener().also { listener -> slot.player.addPlayerListener(listener) }
            }
            "terminate when a Move is received while still paused" {
                players[0].sendMessageToRoom(roomId, TestMove(0))
                await("Terminates") { room.status shouldBe GameRoom.GameStatus.OVER }
            }
            "play game on unpause" {
                admin.control(roomId).unpause()
                await { room.isPauseRequested shouldBe false }
                val game = room.game as TestGame
                game.isPaused shouldBe false
                await("game started") { game.activePlayer?.color shouldBe TestTeam.RED }
                withClue("Processes moves") {
                    playerListeners[0].waitForMessage(MoveRequest::class)
                    players[0].sendMessageToRoom(roomId, TestMove(1))
                    await { game.activePlayer?.color shouldBe TestTeam.BLUE }
                    game.currentState.state shouldBe 1
                    playerListeners[1].waitForMessage(MoveRequest::class)
                    players[1].sendMessageToRoom(roomId, TestMove(2))
                    await { game.activePlayer?.color shouldBe TestTeam.RED }
                    await { game.currentState.state shouldBe 2 }
                }
                
                val move = TestMove(-1)
                players[1].sendMessageToRoom(roomId, move)
                val msg = playerListeners[1].waitForMessage(ErrorMessage::class)
                msg.message shouldContain "not your turn"
                msg.originalMessage shouldBe move
                await("Terminate after wrong player sent a turn") {
                    room.status shouldBe GameRoom.GameStatus.OVER
                }
            }
        }
        "a game is prepared with descriptors" should {
            val descriptors = arrayOf(
                    SlotDescriptor("supreme", canTimeout = false, reserved = true),
                    SlotDescriptor("human", reserved = false),
            )
            val prepared = prepareGame(PrepareGameRequest(TestPlugin.TEST_PLUGIN_UUID, descriptors, pause = false))
            val room = lobby.findRoom(prepared.roomId)
            "return a single reservation" {
                prepared.reservations shouldHaveSize 1
            }
            "create appropriate slots" {
                room.slots shouldHaveSize 2
                room.slots.forEachIndexed { index, slot ->
                    val descriptor = descriptors[index]
                    slot.player.displayName shouldBe descriptor.displayName
                    slot.player.canTimeout shouldBe descriptor.canTimeout
                    slot.isReserved shouldBe descriptor.reserved
                }
            }
            players[1].joinGameRoom(prepared.roomId)
            "join player into nonreserved slot" {
                await { room.clients shouldHaveSize 1 }
                room.slots[0].isEmpty shouldBe true
                room.slots[0].isFree shouldBe false
                room.slots[1].isEmpty shouldBe false
            }
    
            val roomListener = MessageListener<ObservableRoomMessage>()
            withClue("accept observation") {
                admin.observe(prepared.roomId, roomListener::addMessage)
                adminListener.waitForMessage(ObservationResponse::class)
            }
            
            players[0].joinGameWithReservation(prepared.reservations.single())
            "react to controller" {
                await("game start") {
                    room.isPauseRequested shouldBe false
                    room.status shouldBe GameRoom.GameStatus.ACTIVE
                }
                roomListener.waitForMessage(MementoMessage::class)
    
                val controller = admin.control(prepared.roomId)
                controller.pause()
                roomListener.waitForMessage(GamePaused::class)
                withClue("appropriate result for aborted game") {
                    players[1].sendMessageToRoom(prepared.roomId, TestMove(0))
                    val result = roomListener.waitForMessage(GameResult::class)
                    // TODO can be checked once moved from plugin to sdk
                    // result.isRegular shouldBe false
                    result.winners?.singleOrNull() shouldBe room.game.players.first()
                }
            }
        }
    }
})
