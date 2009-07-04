package sc.server.roles;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import sc.api.plugins.RescueableClientException;
import sc.protocol.requests.JoinRoomRequest;
import sc.server.network.Client;
import sc.server.network.PacketCallback;
import sc.server.plugins.PluginLoaderException;
import sc.server.plugins.TestPlugin;

public class PlayerTest extends AbstractRoleTest
{
	@Test
	public void shouldBeAbleToJoinNonExistingGame() throws IOException,
			RescueableClientException, PluginLoaderException
	{
		Client client = connectClient();

		lobby.onRequest(client,
				new PacketCallback(new JoinRoomRequest(TestPlugin.TEST_PLUGIN_UUID)));

		Assert.assertEquals(1, gameMgr.getGames().size());
		Assert.assertEquals(1, client.getRoles().size());
	}
}
