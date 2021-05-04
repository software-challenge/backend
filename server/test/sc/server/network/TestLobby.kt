package sc.server.network

import io.kotest.matchers.shouldBe
import sc.networking.clients.LobbyClient
import sc.server.Configuration
import sc.server.Lobby
import sc.server.plugins.TestPlugin

internal const val PASSWORD = "TEST_PASSWORD"

class TestLobby: Lobby() {
    
    val serverPort: Int
        get() = NewClientListener.lastUsedPort
    
    init {
        Configuration.set(Configuration.PORT_KEY, "0") // Random PortAllocation
        Configuration.set(Configuration.PASSWORD_KEY, PASSWORD)
        
        pluginManager.loadPlugin(TestPlugin::class.java)
        this.pluginManager.supportsGame(TestPlugin.TEST_PLUGIN_UUID) shouldBe true
        
        NewClientListener.lastUsedPort = 0
        start()
        waitForServer()
    }
    
    private fun waitForServer() {
        while (NewClientListener.lastUsedPort == 0) {
            Thread.yield()
        }
    }
    
    fun connectClient(): LobbyClient {
        val client = LobbyClient("localhost", serverPort)
        client.start()
        return client
    }
    
}
