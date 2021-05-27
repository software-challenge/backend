package sc.server.gaming

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import sc.networking.clients.GameLoaderClient
import sc.server.plugins.TestGameState

class GameLoaderTest: FunSpec({
    test("GameLoaderClient loads replay") {
        val client = GameLoaderClient(minimalReplay.byteInputStream())
        val state = TestGameState()
        client.getHistory() shouldBe listOf(state)
        client.getTurn(0) shouldBe state
        client.getTurn(-1) shouldBe state
        shouldThrow<NoSuchElementException> {
            client.getTurn(1)
        }
    }
})