package sc.server.roles;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import sc.api.plugins.RescueableClientException;
import sc.protocol.RoomPacket;
import sc.protocol.requests.JoinRoomRequest;
import sc.protocol.responses.JoinedGame;
import sc.server.network.Client;
import sc.server.network.MockClient;
import sc.server.network.PacketCallback;
import sc.server.plugins.PluginLoaderException;
import sc.server.plugins.TestMove;
import sc.server.plugins.TestPlugin;
import sc.server.plugins.TestTurnRequest;

public class PlayerTest extends AbstractRoleTest
{
	@Test
	public void shouldBeAbleToJoinNonExistingGame() throws IOException,
			RescueableClientException, PluginLoaderException
	{
		Client client = connectClient();

		lobby.onRequest(client, new PacketCallback(new JoinRoomRequest(
				TestPlugin.TEST_PLUGIN_UUID)));

		Assert.assertEquals(1, gameMgr.getGames().size());
		Assert.assertEquals(1, client.getRoles().size());
	}

	@Test
	public void shouldGetRoomIdAfterJoin() throws RescueableClientException
	{
		MockClient player1 = connectClient();
		MockClient player2 = connectClient();

		lobby.onRequest(player1, new PacketCallback(new JoinRoomRequest(
				TestPlugin.TEST_PLUGIN_UUID)));
		lobby.onRequest(player2, new PacketCallback(new JoinRoomRequest(
				TestPlugin.TEST_PLUGIN_UUID)));

		Assert.assertEquals(1, gameMgr.getGames().size());

		JoinedGame msg;
		msg = player1.seekMessage(JoinedGame.class); // did we receive it?
		msg = player2.seekMessage(JoinedGame.class); // did we receive it?

		Assert.assertNotNull(msg.getRoomId());
	}

	@Test
	public void shouldBeAbleToPlayTheGame() throws RescueableClientException
	{
		MockClient player1 = connectClient();
		MockClient player2 = connectClient();

		lobby.onRequest(player1, new PacketCallback(new JoinRoomRequest(
				TestPlugin.TEST_PLUGIN_UUID)));
		lobby.onRequest(player2, new PacketCallback(new JoinRoomRequest(
				TestPlugin.TEST_PLUGIN_UUID)));

		String roomId = player1.seekMessage(JoinedGame.class).getRoomId();
		
		player1.seekRoomMessage(roomId, TestTurnRequest.class); // did we receive it?
		lobby.onRequest(player1, new PacketCallback(new RoomPacket(roomId,
				new TestMove())));
		
		player2.seekRoomMessage(roomId, TestTurnRequest.class);
		lobby.onRequest(player2, new PacketCallback(new RoomPacket(roomId,
				new TestMove())));
	}
}
