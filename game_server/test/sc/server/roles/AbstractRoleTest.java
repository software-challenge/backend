package sc.server.roles;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import sc.server.Configuration;
import sc.server.Lobby;
import sc.server.gaming.GameRoomManager;
import sc.server.helpers.StringNetworkInterface;
import sc.server.network.Client;
import sc.server.network.ClientManager;
import sc.server.network.MockClient;
import sc.server.plugins.GamePluginManager;
import sc.server.plugins.PluginLoaderException;
import sc.server.plugins.TestPlugin;

public abstract class AbstractRoleTest
{
	@Before
	public void setup() throws PluginLoaderException
	{
		// Random PortAllocation
		Configuration.set(Configuration.PORT_KEY, "0");

		lobby = new Lobby();
		clientMgr = lobby.getClientManager();
		gameMgr = lobby.getGameManager();
		pluginMgr = gameMgr.getPluginManager();

		pluginMgr.loadPlugin(TestPlugin.class, gameMgr.getPluginApi());
		Assert.assertTrue(pluginMgr.supportsGame(TestPlugin.TEST_PLUGIN_UUID));

		lobby.start();
	}

	@After
	public void tearDown()
	{
		lobby.close();
	}

	protected Lobby				lobby;
	protected ClientManager		clientMgr;
	protected GameRoomManager	gameMgr;
	protected GamePluginManager	pluginMgr;

	protected MockClient connectClient()
	{
		MockClient client;
		try
		{
			client = new MockClient();
			clientMgr.add(client);
			return client;
		}
		catch (IOException e)
		{
			Assert.fail("Could not connect to server");
			return null;
		}
	}
}
