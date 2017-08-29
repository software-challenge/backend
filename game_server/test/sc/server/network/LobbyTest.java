package sc.server.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;

import junit.framework.Assert;
import sc.api.plugins.exceptions.RescuableClientException;
import sc.helpers.Generator;
import sc.networking.clients.LobbyClient;
import sc.server.gaming.GameRoom;
import sc.server.helpers.TestHelper;
import sc.server.plugins.TestPlugin;
import sc.shared.ScoreCause;

public class LobbyTest extends RealServerTest
{
	@Ignore // TODO seems to switch the players sometimes
	public void shouldEndGameOnIllegalMessage()
			throws RescuableClientException, UnsupportedEncodingException,
			IOException, InterruptedException
	{
		final LobbyClient player1 = connectClient("localhost", getServerPort());
		waitForConnect(1);
		final LobbyClient player2 = connectClient("localhost", getServerPort());
		waitForConnect(2);

		player1.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);
		player2.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);

		TestHelper.assertEqualsWithTimeout(1, ()->player1.getRooms().size());

		TestHelper.assertEqualsWithTimeout(1, ()->player2.getRooms().size());

		TestHelper.assertEqualsWithTimeout(1, ()->LobbyTest.this.gameMgr.getGames().size());

		Assert.assertEquals(1, this.gameMgr.getGames().size());
		Assert.assertEquals(player1.getRooms().get(0), player2.getRooms()
				.get(0));

		final GameRoom theRoom = LobbyTest.this.gameMgr.getGames().iterator()
				.next();

		Assert.assertEquals(false, theRoom.isOver());

		player1.sendCustomData("<yarr>");


		TestHelper.assertEqualsWithTimeout(true, ()->theRoom.isOver());


		TestHelper.assertEqualsWithTimeout(true, () -> theRoom.getResult().getScores() != null);


    Thread.sleep(1000);
		TestHelper.assertEqualsWithTimeout(ScoreCause.LEFT,() -> theRoom.getResult().getScores().get(0).getCause(), 1, TimeUnit.SECONDS);

		// should cleanup gamelist
		TestHelper.assertEqualsWithTimeout(0, ()->LobbyTest.this.gameMgr.getGames().size());
	}
}
