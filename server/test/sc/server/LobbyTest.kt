package sc.server

import org.junit.Test
import sc.server.helpers.TestHelper
import sc.server.network.RealServerTest
import sc.server.plugins.TestPlugin

class LobbyTest: RealServerTest() {
    
    @Test
    fun shouldConnectAndDisconnect() {
        val player1 = connectClient("localhost", serverPort)
        val player2 = connectClient("localhost", serverPort)
        
        player1.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)
        player2.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)
        
        // Wait for game to be created
        TestHelper.assertEqualsWithTimeout(1, { lobby.games.size }, 2000)
        
        // Game should be stopped when one player dies
        player1.stop()
        TestHelper.assertEqualsWithTimeout(0, { lobby.games.size }, 5000)
    }
    
}
