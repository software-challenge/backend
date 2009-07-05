package sc.server.network;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import sc.helpers.Action;
import sc.protocol.LobbyClient;
import sc.server.Configuration;
import sc.server.helpers.TestHelper;
import sc.server.plugins.TestPlugin;

public class SampleLibraryTest extends RealServerTest
{
	class TestLobbyClient extends LobbyClient
	{
		public TestLobbyClient(String gameType, XStream xstream, String host,
				int port) throws IOException
		{
			super(gameType, xstream, host, port);
		}

		@Override
		protected void onRoomMessage(String roomId, Object data)
		{
			// TODO Auto-generated method stub
		}
	}

	@Test
	public void shouldConnectToServer() throws IOException
	{
		final TestLobbyClient client = new TestLobbyClient(
				TestPlugin.TEST_PLUGIN_UUID, Configuration.getXStream(),
				"localhost", getServerPort());

		client.joinAnyGame();

		TestHelper.assertEqualsWithTimeout(1, new Action<Integer>() {
			@Override
			public Integer operate()
			{
				return client.getRooms().size();
			}
		}, 1, TimeUnit.SECONDS);
	}
}
