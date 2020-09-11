package sc.server.gaming

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import sc.protocol.requests.PrepareGameRequest
import sc.server.plugins.TestPlugin

class GameRoomTest: StringSpec({
    val manager = GameRoomManager()
    manager.prepareGame(PrepareGameRequest(TestPlugin.TEST_PLUGIN_UUID))
    manager.games shouldHaveSize 1
    val room = manager.games.first()
})