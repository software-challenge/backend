package sc.server.roles;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import sc.api.plugins.exceptions.RescueableClientException;
import sc.protocol.MementoPacket;
import sc.protocol.RoomPacket;
import sc.protocol.requests.JoinRoomRequest;
import sc.protocol.responses.JoinedGame;
import sc.protocol.responses.RoomLeft;
import sc.server.network.Client;
import sc.server.network.MockClient;
import sc.server.network.PacketCallback;
import sc.server.plugins.PluginLoaderException;
import sc.server.plugins.TestGameState;
import sc.server.plugins.TestMove;
import sc.server.plugins.TestPlugin;
import sc.server.plugins.TestTurnRequest;

public class PlayerTest extends AbstractRoleTest
{
	int	mySecret1	= 13;
	int	mySecret2	= 37;

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
		String roomId2 = player2.seekMessage(JoinedGame.class).getRoomId();
		Assert.assertEquals(roomId, roomId2);

		shouldInitializeCorrectly(roomId, player1, player2);
		shouldProtectFirstPlayersSecrets(roomId, player1, player2);
		shouldProtectSecondPlayersSecrets(roomId, player1, player2);
		makeMoveAfterRequest(roomId, player1);
		makeMoveAfterRequest(roomId, player2);
		player1.seekMessage(RoomLeft.class);
		player2.seekMessage(RoomLeft.class);
	}

	private void shouldInitializeCorrectly(String roomId, MockClient player1,
			MockClient player2)
	{
		MementoPacket memento1 = player1.seekRoomMessage(roomId,
				MementoPacket.class);
		MementoPacket memento2 = player2.seekRoomMessage(roomId,
				MementoPacket.class);

		Assert.assertEquals(0,
				(int) ((TestGameState) memento1.getState()).secret0);
		Assert.assertEquals(0,
				(int) ((TestGameState) memento1.getState()).secret1);
		Assert.assertEquals(0,
				(int) ((TestGameState) memento2.getState()).secret0);
		Assert.assertEquals(0,
				(int) ((TestGameState) memento2.getState()).secret1);
	}

	private void shouldProtectFirstPlayersSecrets(String roomId,
			MockClient player1, MockClient player2)
			throws RescueableClientException
	{
		player1.seekRoomMessage(roomId, TestTurnRequest.class);
		lobby.onRequest(player1, new PacketCallback(new RoomPacket(roomId,
				new TestMove(mySecret1))));

		MementoPacket memento1 = player1.seekRoomMessage(roomId,
				MementoPacket.class);
		Assert.assertEquals(mySecret1, (int) ((TestGameState) memento1
				.getState()).secret0);

		MementoPacket memento2 = player2.seekRoomMessage(roomId,
				MementoPacket.class);
		Assert.assertFalse("Secret of other Player was revealed",
				mySecret1 == ((TestGameState) memento2.getState()).secret0);
	}

	private void makeMoveAfterRequest(String roomId, MockClient player) throws RescueableClientException
	{
		player.seekRoomMessage(roomId, TestTurnRequest.class);
		lobby.onRequest(player, new PacketCallback(new RoomPacket(roomId,
				new TestMove(123456))));
	}

	private void shouldProtectSecondPlayersSecrets(String roomId,
			MockClient player1, MockClient player2)
			throws RescueableClientException
	{
		player2.seekRoomMessage(roomId, TestTurnRequest.class);
		lobby.onRequest(player2, new PacketCallback(new RoomPacket(roomId,
				new TestMove(mySecret2))));

		MementoPacket memento1 = player1.seekRoomMessage(roomId,
				MementoPacket.class);
		Assert
				.assertFalse(
						"Secret of other Player was revealed",
						mySecret2 == (int) ((TestGameState) memento1.getState()).secret1);

		MementoPacket memento2 = player2.seekRoomMessage(roomId,
				MementoPacket.class);
		Assert.assertEquals(mySecret2, (int) ((TestGameState) memento2
				.getState()).secret1);
	}
}
