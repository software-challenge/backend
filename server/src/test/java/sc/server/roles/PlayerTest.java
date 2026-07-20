package sc.server.roles;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sc.api.plugins.exceptions.RescuableClientException;
import sc.protocol.RemovedFromGame;
import sc.protocol.requests.JoinGameRequest;
import sc.protocol.requests.JoinPreparedRoomRequest;
import sc.protocol.requests.ObservationRequest;
import sc.protocol.requests.PrepareGameRequest;
import sc.protocol.responses.GamePreparedResponse;
import sc.protocol.responses.JoinedRoomResponse;
import sc.protocol.room.MementoMessage;
import sc.protocol.room.MoveRequest;
import sc.protocol.room.RoomPacket;
import sc.server.helpers.MockClient;
import sc.server.network.Client;
import sc.server.network.PacketCallback;
import sc.server.plugins.TestGameState;
import sc.server.plugins.TestMove;
import sc.server.plugins.TestPlugin;
import sc.shared.SlotDescriptor;

public class PlayerTest extends AbstractRoleTest {
  int firstState = 13;
  int secondState = 37;

  @Test
  public void shouldBeAbleToJoinNonExistingGame() throws RescuableClientException {
    Client client = connectClient();

    this.lobby.onRequest(client, new PacketCallback(
        new JoinGameRequest(TestPlugin.TEST_PLUGIN_UUID)));

    Assertions.assertEquals(1, this.gameMgr.getGames().size());
  }

  @Test
  public void shouldGetRoomIdAfterJoin() throws RescuableClientException {
    MockClient player1 = connectClient();
    MockClient player2 = connectClient();

    this.lobby.onRequest(player1, new PacketCallback(
        new JoinGameRequest(TestPlugin.TEST_PLUGIN_UUID)));
    this.lobby.onRequest(player2, new PacketCallback(
        new JoinGameRequest(TestPlugin.TEST_PLUGIN_UUID)));

    Assertions.assertEquals(1, this.gameMgr.getGames().size());

    JoinedRoomResponse msg;
    msg = player1.seekMessage(JoinedRoomResponse.class); // did we receive it?
    Assertions.assertNotNull(msg.getRoomId());

    msg = player2.seekMessage(JoinedRoomResponse.class); // did we receive it?
    Assertions.assertNotNull(msg.getRoomId());
  }

  /** Checks basic sending of Moves and end of game. */
  @Test
  public void shouldBeAbleToPlayTheGame() throws RescuableClientException {
    MockClient admin = connectClient(true);
    MockClient player1 = connectClient();
    MockClient player2 = connectClient();
    MockClient observer = connectClient(true);

    SlotDescriptor slot1 = new SlotDescriptor("player1", true);
    SlotDescriptor slot2 = new SlotDescriptor("player2", true);

    this.lobby.onRequest(admin, new PacketCallback(new PrepareGameRequest(TestPlugin.TEST_PLUGIN_UUID, slot1, slot2, false)));
    GamePreparedResponse prepared = admin.seekMessage(GamePreparedResponse.class);

    this.lobby.onRequest(observer, new PacketCallback(
            new ObservationRequest(prepared.getRoomId())));
    this.lobby.onRequest(player1, new PacketCallback(
            new JoinPreparedRoomRequest(prepared.getReservations().get(0))));
    this.lobby.onRequest(player2, new PacketCallback(
            new JoinPreparedRoomRequest(prepared.getReservations().get(1))));

    String roomId = player1.seekMessage(JoinedRoomResponse.class).getRoomId();
    String roomId2 = player2.seekMessage(JoinedRoomResponse.class).getRoomId();
    Assertions.assertEquals(roomId, roomId2);

    shouldInitializeCorrectly(roomId, player1, player2, observer);
    shouldPropagateFirstPlayersMove(roomId, player1, player2, observer);
    shouldPropagateSecondPlayersMove(roomId, player1, player2, observer);
    makeMoveAfterRequest(roomId, player1);
    makeMoveAfterRequest(roomId, player2);
    // TODO
    // player1.seekMessage(RemovedFromGame.class);
    // player2.seekMessage(RemovedFromGame.class);
  }

  private void shouldInitializeCorrectly(String roomId, MockClient player1, MockClient player2, MockClient observer) {
    MementoMessage memento1 = player1.seekRoomMessage(roomId, MementoMessage.class);
    MementoMessage memento2 = player2.seekRoomMessage(roomId, MementoMessage.class);
    MementoMessage mementoObserver = observer.seekRoomMessage(roomId, MementoMessage.class);

    Assertions.assertEquals(0, ((TestGameState) memento1.getState()).getState());
    Assertions.assertEquals(0, ((TestGameState) memento1.getState()).getState());
    Assertions.assertEquals(0, ((TestGameState) memento2.getState()).getState());
    Assertions.assertEquals(0, ((TestGameState) memento2.getState()).getState());
    Assertions.assertEquals(0, ((TestGameState) mementoObserver.getState()).getState());
    Assertions.assertEquals(0, ((TestGameState) mementoObserver.getState()).getState());
  }

  private void shouldPropagateFirstPlayersMove(String roomId,
                                               MockClient player1, MockClient player2, MockClient observer)
          throws RescuableClientException {
    // Do the move
    player1.seekRoomMessage(roomId, MoveRequest.class);
    this.lobby.onRequest(player1,
        new PacketCallback(new RoomPacket(roomId, new TestMove(this.firstState))));

    // Check Player 1
    MementoMessage memento1 = player1.seekRoomMessage(roomId, MementoMessage.class);
    Assertions.assertEquals(this.firstState, ((TestGameState) memento1.getState()).getState());

    // Check Player 2
    MementoMessage memento2 = player2.seekRoomMessage(roomId, MementoMessage.class);
    Assertions.assertEquals(this.firstState, ((TestGameState) memento2.getState()).getState());

    // Check Observer
    MementoMessage mementoObserver = observer.seekRoomMessage(roomId, MementoMessage.class);
    Assertions.assertEquals(this.firstState, ((TestGameState) mementoObserver.getState()).getState());
  }

  private void makeMoveAfterRequest(String roomId, MockClient player)
          throws RescuableClientException {
    player.seekRoomMessage(roomId, MoveRequest.class);
    this.lobby.onRequest(player, new PacketCallback(new RoomPacket(roomId,
            new TestMove(123456))));
  }

  private void shouldPropagateSecondPlayersMove(String roomId,
                                                MockClient player1, MockClient player2, MockClient observer)
          throws RescuableClientException {
    // Do the move
    player2.seekRoomMessage(roomId, MoveRequest.class);
    this.lobby.onRequest(player2, new PacketCallback(new RoomPacket(roomId,
            new TestMove(this.secondState))));

    // Player 1
    MementoMessage memento1 = player1.seekRoomMessage(roomId,
            MementoMessage.class);
    Assertions.assertEquals(this.secondState,
            ((TestGameState) memento1.getState()).getState());

    // Player 2
    MementoMessage memento2 = player2.seekRoomMessage(roomId,
            MementoMessage.class);
    Assertions.assertEquals(this.secondState, ((TestGameState) memento2
            .getState()).getState());

    // Observer
    MementoMessage mementoObserver = observer.seekRoomMessage(roomId,
            MementoMessage.class);
    Assertions.assertEquals(this.secondState,
            ((TestGameState) mementoObserver.getState()).getState());
  }

}
