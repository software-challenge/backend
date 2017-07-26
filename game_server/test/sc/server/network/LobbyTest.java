package sc.server.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

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
	@Test
	public void shouldEndGameOnIllegalMessage()
			throws RescuableClientException, UnsupportedEncodingException,
			IOException, InterruptedException
	{
		final LobbyClient player1 = connectClient("localhost", getServerPort());
		final LobbyClient player2 = connectClient("localhost", getServerPort());

		player1.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);
		player2.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);

		TestHelper.assertEqualsWithTimeout(1, new Generator<Integer>() {
			@Override
			public Integer operate()
			{
				return player1.getRooms().size();
			}
		});

		TestHelper.assertEqualsWithTimeout(1, new Generator<Integer>() {
			@Override
			public Integer operate()
			{
				return player2.getRooms().size();
			}
		});

		TestHelper.assertEqualsWithTimeout(1, new Generator<Integer>() {
			@Override
			public Integer operate()
			{
				return LobbyTest.this.gameMgr.getGames().size();
			}
		});

		Assert.assertEquals(1, this.gameMgr.getGames().size());
		Assert.assertEquals(player1.getRooms().get(0), player2.getRooms()
				.get(0));

		final GameRoom theRoom = LobbyTest.this.gameMgr.getGames().iterator()
				.next();

		Assert.assertEquals(false, theRoom.isOver());

		player1.sendCustomData("<yarr>");


		TestHelper.assertEqualsWithTimeout(true, new Generator<Boolean>() {
			@Override
			public Boolean operate()
			{
				return theRoom.isOver();
			}
		});


		TestHelper.assertEqualsWithTimeout(true, new Generator<Boolean>() {
			@Override
			public Boolean operate()
			{
				return theRoom.getResult().getScores() != null;
			}
		});

    System.out.println(theRoom.getResult().getScores());
		System.out.println("=============================");
    System.out.println(theRoom.getResult().getScores().get(0).getCause());
    System.out.println("=============================");
    Thread.sleep(1000);
		TestHelper.assertEqualsWithTimeout(ScoreCause.LEFT,
				new Generator<ScoreCause>() {
					@Override
					public ScoreCause operate()
					{
            ScoreCause cause = theRoom.getResult().getScores().get(0)
                    .getCause();

					  return cause;
					}
				});

		// should cleanup gamelist
		TestHelper.assertEqualsWithTimeout(0, new Generator<Integer>() {
			@Override
			public Integer operate()
			{
				return LobbyTest.this.gameMgr.getGames().size();
			}
		});
	}
}
