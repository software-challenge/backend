package sc.server.network

import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe
import sc.networking.clients.LobbyClient
import sc.server.Configuration
import sc.server.Lobby
import sc.server.helpers.TestHelper
import sc.server.plugins.TestPlugin
import java.io.IOException
import java.net.Socket
import java.util.concurrent.TimeUnit

class TestLobby: Lobby() {
    val serverPort: Int
        get() = NewClientListener.lastUsedPort
    
    init {
        // Random PortAllocation
        Configuration.set(Configuration.PORT_KEY, "0")
        Configuration.set(Configuration.PASSWORD_KEY, "TEST_PASSWORD")
        
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
    
    fun connectClient(host: String, port: Int): LobbyClient {
        val client = LobbyClient(host, port)
        client.start()
        return client
    }
    
    fun waitForConnect(count: Int) =
        TestHelper.assertEqualsWithTimeout(count, { clientManager.clients.size }, 1, TimeUnit.SECONDS)
    
    fun connectClient(): TestTcpClient {
        try {
            if (serverPort == 0)
                throw RuntimeException("Could not find an open port to connect to.")
            val mySocket = Socket("localhost",
                NewClientListener.lastUsedPort)
            val result = TestTcpClient(mySocket)
            result.start()
            return result
        } catch (e: IOException) {
            e.printStackTrace()
            fail("Could not connect to server.")
        }
    }
    
}
