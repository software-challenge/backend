package sc.server.network

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import sc.protocol.requests.JoinGameRequest
import sc.server.Lobby
import sc.server.helpers.StringNetworkInterface

class LobbyRequestTest: FunSpec({
    test("join without gametype") {
        val dummy = Client(StringNetworkInterface("")).apply { start() }
        val lobby = Lobby()
        val callback = PacketCallback(JoinGameRequest(null))
        lobby.onRequest(dummy, callback)
        await("Starts a game") { lobby.games shouldHaveSize 1 }
    }
})