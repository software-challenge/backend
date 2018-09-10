package sc.server.roles;

import org.junit.Assert;
import org.junit.Test;
import sc.api.plugins.exceptions.RescuableClientException;
import sc.protocol.requests.JoinPreparedRoomRequest;
import sc.protocol.requests.JoinRoomRequest;
import sc.protocol.requests.ObservationRequest;
import sc.protocol.requests.PrepareGameRequest;
import sc.protocol.responses.*;
import sc.server.network.Client;
import sc.server.network.MockClient;
import sc.server.network.PacketCallback;
import sc.server.plugins.TestGameState;
import sc.server.plugins.TestMove;
import sc.server.plugins.TestPlugin;
import sc.server.plugins.TestTurnRequest;
import sc.shared.InvalidGameStateException;
import sc.shared.SlotDescriptor;

public class PlayerTest extends AbstractRoleTest {
  int firstState = 13;
  int secondState = 37;

  @Test
  public void shouldBeAbleToJoinNonExistingGame() throws RescuableClientException, InvalidGameStateException {
    Client client = connectClient();

    this.lobby.onRequest(client, new PacketCallback(new JoinRoomRequest(
            TestPlugin.TEST_PLUGIN_UUID)));

    Assert.assertEquals(1, this.gameMgr.getGames().size());
    Assert.assertEquals(1, client.getRoles().size());
  }

  @Test
  public void shouldGetRoomIdAfterJoin() throws RescuableClientException, InvalidGameStateException {
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
   *
   * @throws RescuableClientException
   */
  @Test
  public void shouldBeAbleToPlayTheGame() throws RescuableClientException, InvalidGameStateException {
    MockClient admin = connectClient(true);
    MockClient player1 = connectClient();
    MockClient player2 = connectClient();
    MockClient observer = connectClient(true);

    SlotDescriptor slot1 = new SlotDescriptor("player1", true, false);
    SlotDescriptor slot2 = new SlotDescriptor("player2", true, false);

    this.lobby.onRequest(admin, new PacketCallback(new PrepareGameRequest(TestPlugin.TEST_PLUGIN_UUID, slot1, slot2)));
    PrepareGameProtocolMessage prepared = admin.seekMessage(PrepareGameProtocolMessage.class);

    this.lobby.onRequest(observer, new PacketCallback(
            new ObservationRequest(prepared.getRoomId())));
    this.lobby.onRequest(player1, new PacketCallback(
            new JoinPreparedRoomRequest(prepared.getReservations().get(0))));
    this.lobby.onRequest(player2, new PacketCallback(
            new JoinPreparedRoomRequest(prepared.getReservations().get(1))));

    String roomId = player1.seekMessage(JoinGameProtocolMessage.class).getRoomId();
    String roomId2 = player2.seekMessage(JoinGameProtocolMessage.class)
            .getRoomId();
    Assert.assertEquals(roomId, roomId2);

    shouldInitializeCorrectly(roomId, player1, player2, observer);
    shouldPropagateFirstPlayersMove(roomId, player1, player2, observer);
    shouldPropagateSecondPlayersMove(roomId, player1, player2, observer);
    makeMoveAfterRequest(roomId, player1);
    makeMoveAfterRequest(roomId, player2);
    player1.seekMessage(LeftGameEvent.class);
    player2.seekMessage(LeftGameEvent.class);
  }

  private void shouldInitializeCorrectly(String roomId, MockClient player1, MockClient player2, MockClient observer) {
    MementoPacket memento1 = player1.seekRoomMessage(roomId, MementoPacket.class);
    MementoPacket memento2 = player2.seekRoomMessage(roomId, MementoPacket.class);
    MementoPacket mementoObserver = observer.seekRoomMessage(roomId, MementoPacket.class);

    Assert.assertEquals(0, ((TestGameState) memento1.getState()).getState());
    Assert.assertEquals(0, ((TestGameState) memento1.getState()).getState());
    Assert.assertEquals(0, ((TestGameState) memento2.getState()).getState());
    Assert.assertEquals(0, ((TestGameState) memento2.getState()).getState());
    Assert.assertEquals(0, ((TestGameState) mementoObserver.getState()).getState());
    Assert.assertEquals(0, ((TestGameState) mementoObserver.getState()).getState());
  }

  private void shouldPropagateFirstPlayersMove(String roomId,
                                               MockClient player1, MockClient player2, MockClient observer)
          throws RescuableClientException, InvalidGameStateException {
    // Do the move
    player1.seekRoomMessage(roomId, TestTurnRequest.class);
    this.lobby.onRequest(player1, new PacketCallback(new RoomPacket(roomId,
            new TestMove(this.firstState))));

    // Check Player 1
    MementoPacket memento1 = player1.seekRoomMessage(roomId,
            MementoPacket.class);
    Assert.assertEquals(this.firstState, ((TestGameState) memento1.getState()).getState());

    // Check Player 2
    MementoPacket memento2 = player2.seekRoomMessage(roomId,
            MementoPacket.class);
    Assert.assertEquals(this.firstState, ((TestGameState) memento2.getState()).getState());

    // Check Observer
    MementoPacket mementoObserver = observer.seekRoomMessage(roomId,
            MementoPacket.class);
    Assert.assertEquals(this.firstState, ((TestGameState) mementoObserver.getState()).getState());
  }

  private void makeMoveAfterRequest(String roomId, MockClient player)
          throws RescuableClientException, InvalidGameStateException {
    player.seekRoomMessage(roomId, TestTurnRequest.class);
    this.lobby.onRequest(player, new PacketCallback(new RoomPacket(roomId,
            new TestMove(123456))));
  }

  private void shouldPropagateSecondPlayersMove(String roomId,
                                                MockClient player1, MockClient player2, MockClient observer)
          throws RescuableClientException, InvalidGameStateException {
    // Do the move
    player2.seekRoomMessage(roomId, TestTurnRequest.class);
    this.lobby.onRequest(player2, new PacketCallback(new RoomPacket(roomId,
            new TestMove(this.secondState))));

    // Player 1
    MementoPacket memento1 = player1.seekRoomMessage(roomId,
            MementoPacket.class);
    Assert.assertEquals(this.secondState,
            ((TestGameState) memento1.getState()).getState());

    // Player 2
    MementoPacket memento2 = player2.seekRoomMessage(roomId,
            MementoPacket.class);
    Assert.assertEquals(this.secondState, ((TestGameState) memento2
            .getState()).getState());

    // Observer
    MementoPacket mementoObserver = observer.seekRoomMessage(roomId,
            MementoPacket.class);
    Assert.assertEquals(this.secondState,
            ((TestGameState) mementoObserver.getState()).getState());
  }

}
