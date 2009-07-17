package sc.server.roles;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import sc.api.plugins.exceptions.RescueableClientException;
import sc.protocol.requests.JoinPreparedRoomRequest;
import sc.protocol.requests.JoinRoomRequest;
import sc.protocol.requests.ObservationRequest;
import sc.protocol.requests.PauseGameRequest;
import sc.protocol.requests.PrepareGameRequest;
import sc.protocol.requests.StepRequest;
import sc.protocol.responses.MementoPacket;
import sc.protocol.responses.PrepareGameResponse;
import sc.protocol.responses.JoinGameResponse;
import sc.protocol.responses.LeftGameEvent;
import sc.protocol.responses.RoomPacket;
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

		JoinGameResponse msg;
		msg = player1.seekMessage(JoinGameResponse.class); // did we receive it?
		msg = player2.seekMessage(JoinGameResponse.class); // did we receive it?

		Assert.assertNotNull(msg.getRoomId());
	}

	@Test
	public void shouldSupportGamePausing() throws RescueableClientException
	{
		MockClient admin = connectClient();
		MockClient player1 = connectClient();
		MockClient player2 = connectClient();

		lobby.onRequest(admin, new PacketCallback(new PrepareGameRequest(
				TestPlugin.TEST_PLUGIN_UUID, 2)));
		PrepareGameResponse prepared = admin
				.seekMessage(PrepareGameResponse.class);

		// PAUSE
		lobby.onRequest(admin, new PacketCallback(new PauseGameRequest(prepared
				.getRoomId(), true)));

		lobby
				.onRequest(player1, new PacketCallback(
						new JoinPreparedRoomRequest(prepared.getReservations()
								.get(0))));
		lobby
				.onRequest(player2, new PacketCallback(
						new JoinPreparedRoomRequest(prepared.getReservations()
								.get(1))));

		player1.seekMessage(JoinGameResponse.class);
		player2.seekMessage(JoinGameResponse.class);

		String roomId = prepared.getRoomId();

		try
		{
			player1.seekRoomMessage(roomId, TestTurnRequest.class);
			Assert.fail();
		}
		catch (Exception e)
		{
			// ok
		}

		// STEP
		lobby.onRequest(admin, new PacketCallback(new StepRequest(roomId)));
		player1.seekRoomMessage(prepared.getRoomId(), TestTurnRequest.class);
		lobby.onRequest(player1, new PacketCallback(new RoomPacket(roomId,
				new TestMove(mySecret1))));

		try
		{
			player2.seekRoomMessage(roomId, TestTurnRequest.class);
			Assert.fail();
		}
		catch (Exception e)
		{
			// ok
		}

		// UNPAUSE
		lobby.onRequest(admin, new PacketCallback(new PauseGameRequest(roomId,
				false)));

		player2.seekRoomMessage(roomId, TestTurnRequest.class);
		lobby.onRequest(player2, new PacketCallback(new RoomPacket(roomId,
				new TestMove(mySecret1))));

		player1.seekRoomMessage(roomId, TestTurnRequest.class);

		lobby.onRequest(player1, new PacketCallback(new RoomPacket(roomId,
				new TestMove(mySecret1))));

		player2.seekRoomMessage(roomId, TestTurnRequest.class);
	}

	@Test
	public void shouldBeAbleToPlayTheGame() throws RescueableClientException
	{
		MockClient admin = connectClient();
		MockClient player1 = connectClient();
		MockClient player2 = connectClient();
		MockClient observer = connectClient();

		lobby.onRequest(admin, new PacketCallback(new PrepareGameRequest(
				TestPlugin.TEST_PLUGIN_UUID, 2)));
		PrepareGameResponse prepared = admin
				.seekMessage(PrepareGameResponse.class);

		lobby.onRequest(observer, new PacketCallback(new ObservationRequest(
				prepared.getRoomId(), "hello")));
		lobby
				.onRequest(player1, new PacketCallback(
						new JoinPreparedRoomRequest(prepared.getReservations()
								.get(0))));
		lobby
				.onRequest(player2, new PacketCallback(
						new JoinPreparedRoomRequest(prepared.getReservations()
								.get(1))));

		String roomId = player1.seekMessage(JoinGameResponse.class).getRoomId();
		String roomId2 = player2.seekMessage(JoinGameResponse.class)
				.getRoomId();
		Assert.assertEquals(roomId, roomId2);

		shouldInitializeCorrectly(roomId, player1, player2, observer);
		shouldProtectFirstPlayersSecrets(roomId, player1, player2, observer);
		shouldProtectSecondPlayersSecrets(roomId, player1, player2, observer);
		makeMoveAfterRequest(roomId, player1);
		makeMoveAfterRequest(roomId, player2);
		player1.seekMessage(LeftGameEvent.class);
		player2.seekMessage(LeftGameEvent.class);
	}

	private void shouldInitializeCorrectly(String roomId, MockClient player1,
			MockClient player2, MockClient observer)
	{
		MementoPacket memento1 = player1.seekRoomMessage(roomId,
				MementoPacket.class);
		MementoPacket memento2 = player2.seekRoomMessage(roomId,
				MementoPacket.class);
		MementoPacket mementoObserver = observer.seekRoomMessage(roomId,
				MementoPacket.class);

		Assert.assertEquals(0,
				(int) ((TestGameState) memento1.getState()).secret0);
		Assert.assertEquals(0,
				(int) ((TestGameState) memento1.getState()).secret1);
		Assert.assertEquals(0,
				(int) ((TestGameState) memento2.getState()).secret0);
		Assert.assertEquals(0,
				(int) ((TestGameState) memento2.getState()).secret1);
		Assert.assertEquals(0, (int) ((TestGameState) mementoObserver
				.getState()).secret0);
		Assert.assertEquals(0, (int) ((TestGameState) mementoObserver
				.getState()).secret1);
	}

	private void shouldProtectFirstPlayersSecrets(String roomId,
			MockClient player1, MockClient player2, MockClient observer)
			throws RescueableClientException
	{
		// Do the move
		player1.seekRoomMessage(roomId, TestTurnRequest.class);
		lobby.onRequest(player1, new PacketCallback(new RoomPacket(roomId,
				new TestMove(mySecret1))));

		// Check Player 1
		MementoPacket memento1 = player1.seekRoomMessage(roomId,
				MementoPacket.class);
		Assert.assertEquals(mySecret1, (int) ((TestGameState) memento1
				.getState()).secret0);

		// Check Player 2
		MementoPacket memento2 = player2.seekRoomMessage(roomId,
				MementoPacket.class);
		Assert.assertFalse("Secret of other Player was revealed",
				mySecret1 == ((TestGameState) memento2.getState()).secret0);

		// Check Observer
		MementoPacket mementoObserver = observer.seekRoomMessage(roomId,
				MementoPacket.class);
		Assert.assertEquals(mySecret1, (int) ((TestGameState) mementoObserver
				.getState()).secret0);
	}

	private void makeMoveAfterRequest(String roomId, MockClient player)
			throws RescueableClientException
	{
		player.seekRoomMessage(roomId, TestTurnRequest.class);
		lobby.onRequest(player, new PacketCallback(new RoomPacket(roomId,
				new TestMove(123456))));
	}

	private void shouldProtectSecondPlayersSecrets(String roomId,
			MockClient player1, MockClient player2, MockClient observer)
			throws RescueableClientException
	{
		// Do the move
		player2.seekRoomMessage(roomId, TestTurnRequest.class);
		lobby.onRequest(player2, new PacketCallback(new RoomPacket(roomId,
				new TestMove(mySecret2))));

		// Player 1
		MementoPacket memento1 = player1.seekRoomMessage(roomId,
				MementoPacket.class);
		Assert
				.assertFalse(
						"Secret of other Player was revealed",
						mySecret2 == (int) ((TestGameState) memento1.getState()).secret1);

		// Player 2
		MementoPacket memento2 = player2.seekRoomMessage(roomId,
				MementoPacket.class);
		Assert.assertEquals(mySecret2, (int) ((TestGameState) memento2
				.getState()).secret1);

		// Observer
		MementoPacket mementoObserver = observer.seekRoomMessage(roomId,
				MementoPacket.class);
		Assert.assertEquals(mySecret2, (int) ((TestGameState) mementoObserver
				.getState()).secret1);
	}
}
