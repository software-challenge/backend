package sc.server.network;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import sc.helpers.Generator;
import sc.networking.clients.XStreamClient.DisconnectCause;
import sc.protocol.requests.JoinRoomRequest;
import sc.server.helpers.TestHelper;
import sc.server.plugins.TestPlugin;

public class ConnectionTest extends RealServerTest
{
	private static class DontYouKnowJack
	{
		// nothing here
	}

	@Test
	public void connectionTest() throws IOException, InterruptedException
	{
		TestTcpClient client = connectClient();
		waitForConnect(1);

		client.send(new JoinRoomRequest(TestPlugin.TEST_PLUGIN_UUID));
		TestHelper.assertEqualsWithTimeout(1, new Generator<Integer>() {
			@Override
			public Integer operate()
			{
				return ConnectionTest.this.lobby.getGameManager().getGames().size();
			}
		}, 1, TimeUnit.SECONDS);
	}

	@Test
	public void protocolViolationTestWithCorruptedXml() throws IOException,
			InterruptedException
	{
		TestTcpClient client = connectClient();
		waitForConnect(1);

		client.sendCustomData("<>/I-do/>NOT<CARE".getBytes("utf-8"));

		TestHelper.assertEqualsWithTimeout(DisconnectCause.PROTOCOL_ERROR,
				new Generator<DisconnectCause>() {
					@Override
					public DisconnectCause operate()
					{
						return ConnectionTest.this.lobby.getClientManager().clients.iterator()
								.next().getDisconnectCause();
					}
				}, 1, TimeUnit.SECONDS);
		// Assert.assertEquals(DisconnectCause.PROTOCOL_ERROR, lobby
		// .getClientManager().clients.iterator().next()
		// .getDisconnectCause());
	}

	@Test
	public void protocolViolationTestWithUnknownClasses() throws IOException,
			InterruptedException
	{
		TestTcpClient client = connectClient();
		TestHelper.assertEqualsWithTimeout(1, new Generator<Integer>() {
			@Override
			public Integer operate()
			{
				return ConnectionTest.this.lobby.getClientManager().clients.size();
			}
		}, 1, TimeUnit.SECONDS);

		client.send(new DontYouKnowJack() {
			// create a class that isn't in the class loader
		});

		TestHelper.assertEqualsWithTimeout(DisconnectCause.PROTOCOL_ERROR,
				new Generator<DisconnectCause>() {
					@Override
					public DisconnectCause operate()
					{
						return ConnectionTest.this.lobby.getClientManager().clients.iterator()
								.next().getDisconnectCause();
					}
				}, 1, TimeUnit.SECONDS);
	}
}
