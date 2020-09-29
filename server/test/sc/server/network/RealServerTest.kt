package sc.server.network

import org.junit.After
import org.junit.Assert
import org.junit.Before
import sc.networking.clients.LobbyClient
import sc.server.Configuration
import sc.server.Lobby
import sc.server.gaming.GameRoomManager
import sc.server.helpers.TestHelper
import sc.server.plugins.GamePluginManager
import sc.server.plugins.TestPlugin
import java.io.IOException
import java.net.Socket
import java.util.concurrent.TimeUnit

abstract class RealServerTest {
    protected lateinit var lobby: Lobby
    protected lateinit var clientMgr: ClientManager
    protected lateinit var gameMgr: GameRoomManager
    protected lateinit var pluginMgr: GamePluginManager
    
    protected val serverPort: Int
        get() = NewClientListener.lastUsedPort
    
    fun connectClient(host: String, port: Int): LobbyClient {
        val client = LobbyClient(host, port)
        client.start()
        return client
    }
    
    @Before
    fun setup() {
        // Random PortAllocation
        Configuration.set(Configuration.PORT_KEY, "0")
        Configuration.set(Configuration.PASSWORD_KEY, "TEST_PASSWORD")
        this.lobby = Lobby()
        this.clientMgr = this.lobby.clientManager
        this.gameMgr = this.lobby
        this.pluginMgr = this.gameMgr.pluginManager
        
        this.pluginMgr.loadPlugin(TestPlugin::class.java)
        Assert.assertTrue(this.pluginMgr.supportsGame(TestPlugin.TEST_PLUGIN_UUID))
        
        NewClientListener.lastUsedPort = 0
        this.lobby.start()
        waitForServer()
    }
    
    @After
    fun tearDown() {
        this.lobby.close()
    }
    
    private fun waitForServer() {
        while (NewClientListener.lastUsedPort == 0) {
            Thread.yield()
        }
    }
    
    protected fun waitForConnect(count: Int) =
            TestHelper.assertEqualsWithTimeout(count, { this@RealServerTest.lobby.clientManager.clients.size }, 1, TimeUnit.SECONDS)
    
    protected fun connectClient(): TestTcpClient {
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
            Assert.fail("Could not connect to server.")
            throw Throwable("Unreachable")
        }
        
    }
    
}
