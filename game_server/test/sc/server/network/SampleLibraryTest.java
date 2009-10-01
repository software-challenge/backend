package sc.server.network;

import java.io.IOException;
import junit.framework.Assert;

import org.junit.Test;

import sc.helpers.Generator;
import sc.networking.clients.LobbyClient;
import sc.protocol.helpers.RequestResult;
import sc.protocol.responses.PrepareGameResponse;
import sc.server.helpers.TestHelper;
import sc.server.plugins.TestPlugin;

public class SampleLibraryTest extends RealServerTest
{
	@Test
	public void shouldConnectToServer() throws IOException
	{
		final LobbyClient client = connectClient("localhost", getServerPort());

		client.joinAnyGame(TestPlugin.TEST_PLUGIN_UUID);

		TestHelper.assertEqualsWithTimeout(1, new Generator<Integer>() {
			@Override
			public Integer operate()
			{
				return client.getRooms().size();
			}
		});
	}

	@Test
	public void shouldBeAbleToCreateGameInstance() throws IOException
	{
		final LobbyClient client = connectClient("localhost", getServerPort());

		client.joinAnyGame(TestPlugin.TEST_PLUGIN_UUID);

		TestHelper.assertEqualsWithTimeout(1, new Generator<Integer>() {
			@Override
			public Integer operate()
			{
				return client.getRooms().size();
			}
		});
	}

	@Test
	public void shouldSupportBlockingHandlers() throws IOException,
			InterruptedException
	{
		final LobbyClient client = connectClient("localhost", getServerPort());

		RequestResult<PrepareGameResponse> result = client.prepareGameAndWait(
				TestPlugin.TEST_PLUGIN_UUID, 2);

		Assert.assertTrue(result.hasValidContents());
		Assert.assertTrue(result.isSuccessful());
	}
}
