package sc.server.network

import io.kotest.matchers.shouldBe
import sc.networking.clients.LobbyClient
import sc.networking.clients.AdminClient
import sc.protocol.ResponsePacket
import sc.server.Configuration
import sc.server.Lobby
import sc.server.helpers.TestGameHandler
import java.io.Closeable

internal const val PASSWORD = "TEST_PASSWORD"

data class TestLobby(val lobby: Lobby = Lobby()): Closeable by lobby {
    
    val serverPort: Int
        get() = lobby.port
    
    init {
        Configuration.set(Configuration.PORT_KEY, "0") // Random PortAllocation
        Configuration.set(Configuration.PASSWORD_KEY, PASSWORD)
        
        lobby.start()
    }
    
    fun connectClient() =
            LobbyClient("localhost", serverPort)

    fun authenticateAdmin(client: LobbyClient, listener: (ResponsePacket) -> Unit): AdminClient {
        val admin = client.authenticate(PASSWORD, listener)
        await("Admin authenticated") { lobby.clientManager.clients.count { it.isAdministrator } shouldBe 1 }
        return admin
    }
    
    fun connectPlayer() =
            TestGameHandler().let {
                it to connectClient().asPlayer(it)
            }
    
}
