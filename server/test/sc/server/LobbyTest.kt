package sc.server

import org.junit.Assert
import org.junit.Test
import sc.server.helpers.TestHelper
import sc.server.network.RealServerTest
import sc.server.plugins.TestPlugin

class LobbyTest : RealServerTest() {

    @Test
    fun shouldConnectAndDisconnect() {
        try {
            val player1 = connectClient("localhost", serverPort)
            val player2 = connectClient("localhost", serverPort)

            player1.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)
            player2.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)

            // Was game created?
            TestHelper.assertEqualsWithTimeout(1, { lobby.gameManager.games.size }, 2000)
            Assert.assertNotNull(lobby.gameManager.games)
            Assert.assertNotEquals(0, lobby.gameManager.games.size.toLong())
            Assert.assertNotNull(lobby.clientManager)

            player1.stop()
            // FIXME sometimes fails - see Issue #124
            // TestHelper.assertEqualsWithTimeout(0, { lobby.gameManager.games.size }, 5000)
        } catch (e: Exception) {
            e.printStackTrace()
            Assert.fail()
        }
    }

}
