package sc.server.network

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.*
import io.kotest.matchers.collections.*
import sc.protocol.requests.AuthenticateRequest
import sc.protocol.requests.JoinGameRequest
import sc.server.Configuration
import sc.server.Lobby
import sc.server.helpers.StringNetworkInterface

class DummyClient(private val data: String = "", private val networkInterface: StringNetworkInterface = StringNetworkInterface(data)): Client(networkInterface) {
    init {
        start()
    }
    fun readData() = networkInterface.readData()
}

class LobbyRequestTest: FunSpec({
    val lobby = Lobby()
    val dummy = DummyClient()
    context("join game") {
        test("without gametype creates a room") {
            lobby.onRequest(dummy, PacketCallback(JoinGameRequest(null)))
            lobby.games shouldHaveSize 1
        }
    }
    context("admin authentication") {
        // Always configure the shared PASSWORD: Configuration is global state
        // and other specs run against it concurrently.
        test("wrong password") {
            Configuration.set(Configuration.PASSWORD_KEY, PASSWORD)
            shouldThrow<AuthenticationFailedException> {
                lobby.onRequest(dummy, PacketCallback(AuthenticateRequest("Bobby")))
            }
            dummy.isAdministrator.shouldBeFalse()
        }
        test("correct password") {
            Configuration.set(Configuration.PASSWORD_KEY, PASSWORD)
            lobby.onRequest(dummy, PacketCallback(AuthenticateRequest(PASSWORD)))
            dummy.isAdministrator.shouldBeTrue()
        }
    }
})