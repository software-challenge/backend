package sc.server.roles;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sc.server.Configuration;
import sc.server.Lobby;
import sc.server.RescueableClientException;
import sc.server.gaming.GameRoomManager;
import sc.server.helpers.ExamplePacket;
import sc.server.helpers.StringNetworkInterface;
import sc.server.network.Client;
import sc.server.plugins.GamePluginManager;
import sc.server.plugins.PluginLoaderException;
import sc.server.plugins.TestPlugin;
import sc.server.protocol.JoinRoomRequest;

public class AdministratorTest
{
	class MyClient extends Client
	{
		public MyClient() throws IOException
		{
			super(new StringNetworkInterface("<object-stream>"), Configuration
					.getXStream());
		}
	}

	private Lobby	lobby;

	@Before
	public void setup()
	{
		lobby = new Lobby();
	}

	@Test
	public void shouldGetAdministrativeRights() throws IOException,
			RescueableClientException, PluginLoaderException
	{
		MyClient client = new MyClient();
		lobby.onClientConnected(client);

		GameRoomManager gameMgr = lobby.getGameManager();
		GamePluginManager pluginMgr = gameMgr.getPluginManager();

		pluginMgr.loadPlugin(TestPlugin.class, gameMgr.getPluginApi());
		Assert.assertTrue(pluginMgr.supportsGame(TestPlugin.TEST_PLUGIN_UUID));

		Configuration.getXStream().alias("example", ExamplePacket.class);

		JoinRoomRequest joinGame = new JoinRoomRequest(
				TestPlugin.TEST_PLUGIN_UUID);
		lobby.onRequest(client, joinGame);

		Assert.assertEquals(1, gameMgr.getGames().size());
		Assert.assertEquals(1, client.getRoles().size());
	}
}
