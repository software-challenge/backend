package sc.server.network;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import sc.helpers.Generator;
import sc.server.Configuration;
import sc.server.Lobby;
import sc.server.gaming.GameRoomManager;
import sc.server.helpers.TestHelper;
import sc.server.plugins.GamePluginManager;
import sc.server.plugins.PluginLoaderException;
import sc.server.plugins.TestPlugin;

public abstract class RealServerTest
{

	protected Lobby				lobby;
	protected ClientManager		clientMgr;
	protected GameRoomManager	gameMgr;
	protected GamePluginManager	pluginMgr;

	@Before
	public void setup() throws IOException, PluginLoaderException
	{
		// Random PortAllocation
		Configuration.set(Configuration.PORT_KEY, "0");

		lobby = new Lobby();
		clientMgr = lobby.getClientManager();
		gameMgr = lobby.getGameManager();
		pluginMgr = gameMgr.getPluginManager();

		pluginMgr.loadPlugin(TestPlugin.class, gameMgr.getPluginApi());
		Assert.assertTrue(pluginMgr.supportsGame(TestPlugin.TEST_PLUGIN_UUID));

		NewClientListener.lastUsedPort = 0;
		lobby.start();
		waitForServer();
	}

	@After
	public void tearDown()
	{
		lobby.close();
	}

	private void waitForServer()
	{
		while (NewClientListener.lastUsedPort == 0)
		{
			Thread.yield();
		}
	}

	protected void waitForConnect(int count)
	{
		TestHelper.assertEqualsWithTimeout(count, new Generator<Integer>() {
			@Override
			public Integer operate()
			{
				return lobby.getClientManager().clients.size();
			}
		}, 1, TimeUnit.SECONDS);
	}

	protected int getServerPort()
	{
		return NewClientListener.lastUsedPort;
	}

	protected TestTcpClient connectClient()
	{
		try
		{
			if (getServerPort() == 0)
			{
				throw new RuntimeException(
						"Could not find an open port to connect to.");
			}
			Socket mySocket = new Socket("localhost",
					NewClientListener.lastUsedPort);
			TestTcpClient result = new TestTcpClient(Configuration.getXStream(),
					mySocket);
			result.start();
			return result;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Assert.fail("Could not connect to server.");
			return null;
		}
	}
}
