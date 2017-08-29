package sc.server.network;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Ignore;
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
		public int test = 25;
	}

  @Test //TODO works for me
	public void connectionTest() throws IOException, InterruptedException
	{
		TestTcpClient client = connectClient();
		waitForConnect(1);

		client.send(new JoinRoomRequest(TestPlugin.TEST_PLUGIN_UUID));
		TestHelper.assertEqualsWithTimeout(1, new Generator<Integer>() {
			@Override
			public Integer operate()
			{
				return ConnectionTest.this.lobby.getGameManager().getGames()
						.size();
			}
		}, 1, TimeUnit.SECONDS);
	}

	@Ignore //TODO seems so fail sometimes
	public void protocolViolationTestWithCorruptedXml() throws IOException,
			InterruptedException
	{
		TestTcpClient client = connectClient();
		waitForConnect(1);
    client.sendCustomData("<>/I-do/>NOT<CARE".getBytes("utf-8"));

    TestHelper.assertEqualsWithTimeout(DisconnectCause.PROTOCOL_ERROR,()->ConnectionTest.this.lobby.getClientManager().getClients()
            .getFirst().getDisconnectCause(), 5, TimeUnit.SECONDS);
	}

  @Ignore //TODO seems so fail sometimes
	public void protocolViolationTestWithUnknownClass() throws IOException,
			InterruptedException
	{
		TestTcpClient client = connectClient();
		waitForConnect(1);
		client.sendCustomData("<object-stream>".getBytes("utf-8"));

		client
				.sendCustomData("<NoSuchClass foo=\"aaa\"><base val=\"arr\" /></NoSuchClass>"
						.getBytes("utf-8"));
    waitForConnect(1);
		TestHelper.assertEqualsWithTimeout(DisconnectCause.PROTOCOL_ERROR,
            ()-> this.lobby.getClientManager().getClients().getFirst().getDisconnectCause(),
            5, TimeUnit.SECONDS);
	}

	@Ignore //TODO Should be tested, but fails sometimes, client is removed when sending unknown class
  public void protocolViolationTestWithUnknownClasses() throws IOException,
			InterruptedException
	{
		TestTcpClient client = connectClient();
		waitForConnect(1);
		client.send(new DontYouKnowJack());
    waitForConnect(1);
    DisconnectCause disconnect = this.lobby.getClientManager().getClients().getFirst().getDisconnectCause();
		TestHelper.assertEqualsWithTimeout(DisconnectCause.PROTOCOL_ERROR,
            () -> this.lobby.getClientManager().getClients().getFirst().getDisconnectCause(), 1, TimeUnit.SECONDS);
	}
}
