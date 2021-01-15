package sc.server.network

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import sc.server.client.TestLobbyClientListener
import sc.server.helpers.TestHelper
import sc.server.plugins.TestPlugin
import sc.shared.ScoreCause
import java.util.concurrent.TimeUnit

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
    
    @Disabled
    @Test // TODO seems to switch the players sometimes
    fun shouldEndGameOnIllegalMessage() {
        val player1 = connectClient("localhost", serverPort)
        waitForConnect(1)
        val player2 = connectClient("localhost", serverPort)
        waitForConnect(2)
    
        val listener = TestLobbyClientListener()
        player1.addListener(listener)
        
        player1.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)
        player2.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)
        
        // TODO Listen for RoomJoinedResponse directly instead
        //TestHelper.assertEqualsWithTimeout(1, { player1.rooms.size })
        //TestHelper.assertEqualsWithTimeout(1, { player2.rooms.size })
        listener.gameJoinedReceived shouldBe true
        listener.roomId shouldNotBe null
        TestHelper.assertEqualsWithTimeout(1, { this@LobbyTest.gameMgr.games.size })
        
        val theRoom = this@LobbyTest.gameMgr.games.iterator().next()
        
        Assertions.assertEquals(false, theRoom.isOver)
        
        player1.sendCustomData("<yarr>")
        
        TestHelper.assertEqualsWithTimeout(true, { theRoom.isOver })
        TestHelper.assertEqualsWithTimeout(ScoreCause.LEFT, { theRoom.result.scores[0].cause }, 2, TimeUnit.SECONDS)
        
        // should cleanup gamelist
        TestHelper.assertEqualsWithTimeout(0, { this@LobbyTest.gameMgr.games.size })
    }
    
}
