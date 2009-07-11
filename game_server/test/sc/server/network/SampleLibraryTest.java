package sc.server.network;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.junit.Test;

import sc.helpers.Generator;
import sc.protocol.ErrorResponse;
import sc.protocol.LobbyClient;
import sc.protocol.RequestResult;
import sc.protocol.responses.PrepareGameResponse;
import sc.server.Configuration;
import sc.server.helpers.TestHelper;
import sc.server.plugins.TestPlugin;

import com.thoughtworks.xstream.XStream;

public class SampleLibraryTest extends RealServerTest
{
	static class TestLobbyClient
	{
		private LobbyClient	client;

		public TestLobbyClient(String gameType, String host, int port)
				throws IOException
		{
			client = new LobbyClient(host, port, null, Configuration
					.getXStream());
		}

		public LobbyClient getClient()
		{
			return client;
		}
	}

	@Test
	public void shouldConnectToServer() throws IOException
	{
		final TestLobbyClient client = new TestLobbyClient(
				TestPlugin.TEST_PLUGIN_UUID, "localhost", getServerPort());

		client.getClient().joinAnyGame(TestPlugin.TEST_PLUGIN_UUID);

		TestHelper.assertEqualsWithTimeout(1, new Generator<Integer>() {
			@Override
			public Integer operate()
			{
				return client.client.getRooms().size();
			}
		});
	}

	@Test
	public void shouldBeAbleToPlayTheGame() throws IOException
	{
		final TestLobbyClient client = new TestLobbyClient(
				TestPlugin.TEST_PLUGIN_UUID, "localhost", getServerPort());

		client.getClient().joinAnyGame(TestPlugin.TEST_PLUGIN_UUID);

		TestHelper.assertEqualsWithTimeout(1, new Generator<Integer>() {
			@Override
			public Integer operate()
			{
				return client.getClient().getRooms().size();
			}
		});
	}

	@Test
	public void shouldSupportBlockingHandlers() throws IOException,
			InterruptedException
	{
		final TestLobbyClient client = new TestLobbyClient(
				TestPlugin.TEST_PLUGIN_UUID, "localhost", getServerPort());

		RequestResult<PrepareGameResponse> result = client.client
				.prepareGameAndWait(TestPlugin.TEST_PLUGIN_UUID, 2);

		Assert.assertTrue(result.hasValidContents());
		Assert.assertTrue(result.isSuccessful());
	}
}
