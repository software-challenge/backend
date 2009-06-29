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
import sc.server.plugins.GamePluginManager;
import sc.server.plugins.PluginLoaderException;
import sc.server.plugins.TestPlugin;

public abstract class AbstractRoleTest
{
	protected class MyClient extends Client
	{
		private Queue<Object>	outgoingMessages	= new LinkedList<Object>();

		public MyClient() throws IOException
		{
			super(new StringNetworkInterface("<object-stream>"), Configuration
					.getXStream());
		}

		@Override
		public synchronized void send(Object packet)
		{
			outgoingMessages.add(packet);
			super.send(packet);
		}

		public Object popMessage()
		{
			return outgoingMessages.poll();
		}

		@SuppressWarnings("unchecked")
		public <T> T seekMessage(Class<T> type)
		{
			Object current = null;
			do
			{
				current = popMessage();
			} while (current != null && current.getClass() != type);

			if (current == null)
			{
				throw new RuntimeException(
						"Could not find a message of the specified type");
			}
			else
			{
				return (T) current;
			}
		}
	}

	@Before
	public void setup() throws PluginLoaderException
	{
		lobby = new Lobby();
		clientMgr = lobby.getClientManager();
		gameMgr = lobby.getGameManager();
		pluginMgr = gameMgr.getPluginManager();

		pluginMgr.loadPlugin(TestPlugin.class, gameMgr.getPluginApi());
		Assert.assertTrue(pluginMgr.supportsGame(TestPlugin.TEST_PLUGIN_UUID));
	}

	@After
	public void tearDown()
	{
		clientMgr.close();
	}

	protected Lobby				lobby;
	protected ClientManager		clientMgr;
	protected GameRoomManager	gameMgr;
	protected GamePluginManager	pluginMgr;

	protected MyClient connectClient()
	{
		MyClient client;
		try
		{
			client = new MyClient();
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
