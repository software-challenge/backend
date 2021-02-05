package sc.server.network

import io.kotest.assertions.timing.eventually
import io.kotest.assertions.withClue
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import sc.server.plugins.TestPlugin
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalTime
class LobbyRequestTest: WordSpec({
    isolationMode = IsolationMode.SingleInstance
    "A Lobby with connected clients" When {
        val lobby = autoClose(TestLobby())
        val players = Array(3) { pos ->
            val player = lobby.connectClient("localhost", lobby.serverPort)
            withClue("Client connected") {
                eventually(200.milliseconds) { lobby.clientManager.clients.size shouldBe pos + 1 }
            }
            player
        }
        "a player joined" should {
            players[0].joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)
            "create a room for it" {
                withClue("Room opened") {
                    eventually(100.milliseconds) { lobby.games.size shouldBe 1 }
                }
                lobby.games.single().clients shouldHaveSize 1
            }
        }
    }
})
