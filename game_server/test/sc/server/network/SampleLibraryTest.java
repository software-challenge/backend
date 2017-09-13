package sc.server.network;

import java.io.IOException;

import org.junit.Test;

import sc.helpers.Generator;
import sc.networking.clients.LobbyClient;
import sc.server.helpers.TestHelper;
import sc.server.plugins.TestPlugin;

public class SampleLibraryTest extends RealServerTest
{
	@Test
	public void shouldConnectToServer() throws IOException
	{
		final LobbyClient client = connectClient("localhost", getServerPort());

		client.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);

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

		client.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);

		TestHelper.assertEqualsWithTimeout(1, new Generator<Integer>() {
			@Override
			public Integer operate()
			{
				return client.getRooms().size();
			}
		});
	}

}
