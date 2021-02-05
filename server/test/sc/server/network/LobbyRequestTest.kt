package sc.server.network

import io.kotest.assertions.timing.eventually
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import sc.server.plugins.TestPlugin
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalTime
suspend fun <T> assertEqualWithTimeout(value: T, f: () -> T, duration: Duration = 100.milliseconds) =
    eventually(duration) { f() shouldBe value }

@ExperimentalTime
class LobbyRequestTest: WordSpec({
    isolationMode = IsolationMode.SingleInstance
    "A Lobby with connected clients" When {
        val lobby = autoClose(TestLobby())
        val players = Array(3) {
            val player = lobby.connectClient("localhost", lobby.serverPort)
            Thread.sleep(200)
            player
        }
        "a player joined" should {
            players[0].joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)
            "create a room for it" {
                assertEqualWithTimeout(1, { lobby.games.size })
                lobby.games.single().clients shouldHaveSize 1
            }
        }
    }
})
