package sc.server.network

import io.kotest.assertions.timing.eventually
import io.kotest.assertions.until.Interval
import io.kotest.assertions.until.fibonacci
import io.kotest.assertions.withClue
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import sc.framework.plugins.protocol.MoveRequest
import sc.server.client.PlayerListener
import sc.server.client.TestLobbyClientListener
import sc.server.gaming.GameRoom
import sc.server.plugins.TestGame
import sc.server.plugins.TestMove
import sc.server.plugins.TestPlugin
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds
import kotlin.time.seconds

@ExperimentalTime
suspend fun await(clue: String? = null, duration: Duration = 1.seconds, interval: Interval = 20.milliseconds.fibonacci(), f: suspend () -> Unit) =
        withClue(clue) { eventually(duration, interval, f) }

@ExperimentalTime
class LobbyRequestTest: WordSpec({
    isolationMode = IsolationMode.SingleInstance
    "A Lobby with connected clients" When {
        val lobby = autoClose(TestLobby())
        val players = Array(3) { lobby.connectClient("localhost", lobby.serverPort) }
        await("Clients connected") { lobby.clientManager.clients.size shouldBe players.size }
        "a player joined" should {
            players[0].joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)
            "create a room for it" {
                withClue("Room opened") {
                    await { lobby.games.size shouldBe 1 }
                }
                lobby.games.single().clients shouldHaveSize 1
            }
        }
        val admin = players[0]
        "preparing a paused game" should {
            val listener = TestLobbyClientListener()
            admin.addListener(listener)
            admin.authenticate(PASSWORD)
            
            "prepare the GameRoom" {
                admin.prepareGame(TestPlugin.TEST_PLUGIN_UUID, true)
                await { listener.gamePreparedReceived shouldBe true }
                lobby.games shouldHaveSize 2
            }
            
            val roomId = listener.prepareGameResponse.roomId
            val room = lobby.findRoom(roomId)
            "create an empty paused game" {
                room.clients.shouldBeEmpty()
                room.isPauseRequested shouldBe true
            }
            
            val observer = admin.observeAndControl(roomId, true)
            await { listener.observedReceived shouldBe true }
            
            "let players join by reservation" {
                val reservations = listener.prepareGameResponse.reservations
                players[1].joinPreparedGame(reservations[0])
                players[2].joinPreparedGame(reservations[1])
                await { room.status shouldBe GameRoom.GameStatus.ACTIVE }
            }
            
            val playerListeners = room.slots.map { slot ->
                PlayerListener().also { listener -> slot.role.player.addPlayerListener(listener) }
            }
            "start game on unpause" {
                observer.unpause()
                await { room.isPauseRequested shouldBe false }
            }
            val game = room.game as TestGame
            "accept moves" {
                playerListeners[0].waitForMessage(MoveRequest::class)
                players[1].sendMessageToRoom(roomId, TestMove(32))
                await { game.currentState.state shouldBe 32 }
                playerListeners[1].waitForMessage(MoveRequest::class)
                players[2].sendMessageToRoom(roomId, TestMove(54))
                await { game.currentState.state shouldBe 54 }
            }
            "terminate when wrong player sends a move" {
                players[2].sendMessageToRoom(roomId, TestMove(0))
                await { room.status shouldBe GameRoom.GameStatus.OVER }
            }
        }
    }
})
