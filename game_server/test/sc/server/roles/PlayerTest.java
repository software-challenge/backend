package sc.server.roles;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

import sc.api.plugins.exceptions.RescuableClientException;
import sc.protocol.requests.*;
import sc.protocol.responses.*;
import sc.protocol.responses.PrepareGameProtocolMessage;
import sc.server.gaming.GameRoom;
import sc.server.network.Client;
import sc.server.network.MockClient;
import sc.server.network.PacketCallback;
import sc.server.plugins.PluginLoaderException;
import sc.server.plugins.TestGameState;
import sc.server.plugins.TestMove;
import sc.server.plugins.TestPlugin;
import sc.server.plugins.TestTurnRequest;
import sc.shared.SlotDescriptor;

public class PlayerTest extends AbstractRoleTest
{
	int firstState = 13;
	int secondState = 37;

	@Test
	public void shouldBeAbleToJoinNonExistingGame() throws IOException,
					RescuableClientException, PluginLoaderException
	{
		Client client = connectClient();

		this.lobby.onRequest(client, new PacketCallback(new JoinRoomRequest(
				TestPlugin.TEST_PLUGIN_UUID)));

		Assert.assertEquals(1, this.gameMgr.getGames().size());
		Assert.assertEquals(1, client.getRoles().size());
	}

	@Test
	public void shouldGetRoomIdAfterJoin() throws RescuableClientException
	{
		MockClient player1 = connectClient();
		MockClient player2 = connectClient();

		this.lobby.onRequest(player1, new PacketCallback(new JoinRoomRequest(
				TestPlugin.TEST_PLUGIN_UUID)));
		this.lobby.onRequest(player2, new PacketCallback(new JoinRoomRequest(
				TestPlugin.TEST_PLUGIN_UUID)));

		Assert.assertEquals(1, this.gameMgr.getGames().size());

		JoinGameProtocolMessage msg;
		msg = player1.seekMessage(JoinGameProtocolMessage.class); // did we receive it?
		Assert.assertNotNull(msg.getRoomId());

		msg = player2.seekMessage(JoinGameProtocolMessage.class); // did we receive it?

		Assert.assertNotNull(msg.getRoomId());
	}

  /**
   * Checks basic sending of Moves and end of game
   * @throws RescuableClientException
   */
	@Test
  public void shouldBeAbleToPlayTheGame() throws RescuableClientException
	{
		MockClient admin = connectClient(true);
		MockClient player1 = connectClient();
		MockClient player2 = connectClient();
		MockClient observer = connectClient(true);

    SlotDescriptor slot1 = new SlotDescriptor("player1", true, false);
    SlotDescriptor slot2 = new SlotDescriptor("player2", true, false);


    this.lobby.onRequest(admin, new PacketCallback(new PrepareGameRequest(
				TestPlugin.TEST_PLUGIN_UUID, slot1, slot2)));
		PrepareGameProtocolMessage prepared = admin
				.seekMessage(PrepareGameProtocolMessage.class);

		this.lobby.onRequest(observer, new PacketCallback(
				new ObservationRequest(prepared.getRoomId())));
		this.lobby
				.onRequest(player1, new PacketCallback(
						new JoinPreparedRoomRequest(prepared.getReservations()
								.get(0))));
		this.lobby
				.onRequest(player2, new PacketCallback(
						new JoinPreparedRoomRequest(prepared.getReservations()
								.get(1))));

		String roomId = player1.seekMessage(JoinGameProtocolMessage.class).getRoomId();
		String roomId2 = player2.seekMessage(JoinGameProtocolMessage.class)
				.getRoomId();
		Assert.assertEquals(roomId, roomId2);

		shouldInitializeCorrectly(roomId, player1, player2, observer);
		shouldPropagadeFirstPlayersMove(roomId, player1, player2, observer);
		shouldPropagadeSecondPlayersMove(roomId, player1, player2, observer);
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
				(int) ((TestGameState) memento1.getState()).state);
		Assert.assertEquals(0,
				(int) ((TestGameState) memento1.getState()).state);
		Assert.assertEquals(0,
				(int) ((TestGameState) memento2.getState()).state);
		Assert.assertEquals(0,
				(int) ((TestGameState) memento2.getState()).state);
		Assert.assertEquals(0, (int) ((TestGameState) mementoObserver
				.getState()).state);
		Assert.assertEquals(0, (int) ((TestGameState) mementoObserver
				.getState()).state);
	}

	private void shouldPropagadeFirstPlayersMove(String roomId,
                                               MockClient player1, MockClient player2, MockClient observer)
			throws RescuableClientException
	{
		// Do the move
		player1.seekRoomMessage(roomId, TestTurnRequest.class);
		this.lobby.onRequest(player1, new PacketCallback(new RoomPacket(roomId,
				new TestMove(this.firstState))));

		// Check Player 1
		MementoPacket memento1 = player1.seekRoomMessage(roomId,
				MementoPacket.class);
		Assert.assertEquals(this.firstState, (int) ((TestGameState) memento1
				.getState()).state);

		// Check Player 2
		MementoPacket memento2 = player2.seekRoomMessage(roomId,
				MementoPacket.class);
		Assert
				.assertEquals(
						this.firstState,
                ((TestGameState) memento2.getState()).state);

		// Check Observer
		MementoPacket mementoObserver = observer.seekRoomMessage(roomId,
				MementoPacket.class);
		Assert.assertEquals(this.firstState,
				(int) ((TestGameState) mementoObserver.getState()).state);
	}

	private void makeMoveAfterRequest(String roomId, MockClient player)
			throws RescuableClientException
	{
		player.seekRoomMessage(roomId, TestTurnRequest.class);
		this.lobby.onRequest(player, new PacketCallback(new RoomPacket(roomId,
				new TestMove(123456))));
	}

	private void shouldPropagadeSecondPlayersMove(String roomId,
                                                MockClient player1, MockClient player2, MockClient observer)
			throws RescuableClientException
	{
		// Do the move
		player2.seekRoomMessage(roomId, TestTurnRequest.class);
		this.lobby.onRequest(player2, new PacketCallback(new RoomPacket(roomId,
				new TestMove(this.secondState))));

		// Player 1
		MementoPacket memento1 = player1.seekRoomMessage(roomId,
				MementoPacket.class);
		Assert.assertEquals(this.secondState,
						(int) ((TestGameState) memento1.getState()).state);

		// Player 2
		MementoPacket memento2 = player2.seekRoomMessage(roomId,
				MementoPacket.class);
		Assert.assertEquals(this.secondState, (int) ((TestGameState) memento2
				.getState()).state);

		// Observer
		MementoPacket mementoObserver = observer.seekRoomMessage(roomId,
				MementoPacket.class);
		Assert.assertEquals(this.secondState,
				(int) ((TestGameState) mementoObserver.getState()).state);
	}
}
