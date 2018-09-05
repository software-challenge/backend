package sc.server.network

import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import sc.server.helpers.TestHelper
import sc.server.plugins.TestPlugin
import sc.shared.ScoreCause
import java.util.concurrent.TimeUnit

class LobbyTest : RealServerTest() {

    @Ignore
    @Test // TODO seems to switch the players sometimes
    fun shouldEndGameOnIllegalMessage() {
        val player1 = connectClient("localhost", serverPort)
        waitForConnect(1)
        val player2 = connectClient("localhost", serverPort)
        waitForConnect(2)

        player1.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)
        player2.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)

        TestHelper.assertEqualsWithTimeout(1, { player1.rooms.size })
        TestHelper.assertEqualsWithTimeout(1, { player2.rooms.size })
        TestHelper.assertEqualsWithTimeout(1, { this@LobbyTest.gameMgr.games.size })

        Assert.assertEquals(1, this.gameMgr.games.size)
        Assert.assertEquals(player1.rooms[0], player2.rooms[0])

        val theRoom = this@LobbyTest.gameMgr.games.iterator().next()

        Assert.assertEquals(false, theRoom.isOver)

        player1.sendCustomData("<yarr>")

        TestHelper.assertEqualsWithTimeout(true, { theRoom.isOver })
        TestHelper.assertEqualsWithTimeout(true, { theRoom.result.scores != null })

        TestHelper.assertEqualsWithTimeout(ScoreCause.LEFT, { theRoom.result.scores[0].cause }, 2, TimeUnit.SECONDS)

        // should cleanup gamelist
        TestHelper.assertEqualsWithTimeout(0, { this@LobbyTest.gameMgr.games.size })
    }

}
