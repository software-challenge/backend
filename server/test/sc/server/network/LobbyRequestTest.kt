package sc.server.network

import io.kotest.assertions.timing.eventually
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import sc.server.helpers.TestHelper
import sc.server.plugins.TestPlugin
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalTime
suspend fun <T> assertEqualWithTimeout(value: T, f: () -> T, duration: Duration = 100.milliseconds) =
    eventually(duration) { f() shouldBe value }

@ExperimentalTime
class LobbyRequestTest: WordSpec({
    "A Lobby with connected clients" When {
        val lobby = autoClose(TestLobby())
        val players = Array(3) {
            val player = lobby.connectClient("localhost", lobby.serverPort)
            TestHelper.waitMillis(200)
            player
        }
        "a player tries to join" should {
            players[0].joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)
            var a = 0
            "create a room" {
                a = 1
                assertEqualWithTimeout(1, { lobby.games.size })
                lobby.games.first().clients shouldHaveSize 1
            }
            "test branching" {
                a shouldBe 0
            }
        }
    }
})
