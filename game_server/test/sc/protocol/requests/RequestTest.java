package sc.protocol.requests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sc.networking.clients.LobbyClient;
import sc.protocol.responses.PrepareGameProtocolMessage;
import sc.server.client.PlayerListener;
import sc.server.client.TestLobbyClientListener;
import sc.server.client.TestObserverListener;
import sc.server.client.TestPreparedGameResponseListener;
import sc.server.gaming.GameRoom;
import sc.server.gaming.ObserverRole;
import sc.server.gaming.PlayerRole;
import sc.server.helpers.TestHelper;
import sc.server.network.Client;
import sc.server.network.IClientRole;
import sc.server.network.RealServerTest;
import sc.server.plugins.TestMove;
import sc.server.plugins.TestPlugin;

import java.util.Iterator;
import java.util.LinkedList;


public class RequestTest extends RealServerTest{
  LobbyClient player1;
  LobbyClient player2;
  LobbyClient player3;


  static final String PASSWORD = "TEST_PASSWORD";

  @Before
  public void prepare() {
    try {
      player1  = connectClient("localhost", getServerPort());
      TestHelper.waitMills(200);
      player2  = connectClient("localhost", getServerPort());
      TestHelper.waitMills(200);
      player3  = connectClient("localhost", getServerPort());
      TestHelper.waitMills(200);
    } catch(Exception e){}
  }

  @Test
  public void joinRoomRequest () {
    player1.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);

    TestHelper.assertEqualsWithTimeout(1,()->lobby.getGameManager().getGames().size());
    Assert.assertEquals(1,lobby.getGameManager().getGames().iterator().next().getClients().size());
  }

  @Test
  public void authenticationRequest(){
    player1.authenticate(PASSWORD);
    TestHelper.waitMills(200);
    LinkedList<Client> clients = lobby.getClientManager().getClients();
    Assert.assertEquals(true, clients.get(0).isAdministrator());
    Assert.assertEquals(3,lobby.getClientManager().getClients().size());

    player2.authenticate("PASSWORD_FAIL_TEST");
    TestHelper.waitMills(200);

    //Player2 got kicked
    Assert.assertEquals(2,lobby.getClientManager().getClients().size());
    Assert.assertEquals(false, clients.get(1).isAdministrator());

  }

  @Test
  public void prepareRoomRequest(){

    player1.authenticate(PASSWORD);
    player1.prepareGame(TestPlugin.TEST_PLUGIN_UUID, true);
    TestPreparedGameResponseListener listener = new TestPreparedGameResponseListener();
    player1.addListener(listener);

    TestHelper.waitMills(200);
    Assert.assertNotNull(listener.response);

    Assert.assertEquals(1,  lobby.getGameManager().getGames().size());
    Assert.assertEquals(0, lobby.getGameManager().getGames().iterator().next().getClients().size());
    Assert.assertEquals(true, lobby.getGameManager().getGames().iterator().next().isPaused());

  }

  @Test
  public void joinPreparedRoomRequest(){
    player1.authenticate(PASSWORD);
    TestPreparedGameResponseListener listener = new TestPreparedGameResponseListener();
    player1.addListener(listener);

    player1.prepareGame(TestPlugin.TEST_PLUGIN_UUID);
    TestHelper.waitMills(200);
    PrepareGameProtocolMessage response = listener.response;

    String reservation = response.getReservations().get(0);
    player1.joinPreparedGame(reservation);
    TestHelper.waitMills(200);
    Assert.assertEquals(1, lobby.getGameManager().getGames().iterator().next().getClients().size());

    player2.joinPreparedGame(response.getReservations().get(1));
    TestHelper.waitMills(200);
    Assert.assertEquals(2, lobby.getGameManager().getGames().iterator().next().getClients().size());

    player3.joinPreparedGame(response.getReservations().get(1));
    TestHelper.waitMills(200);
    Assert.assertEquals(2, lobby.getClientManager().getClients().size());
  }

  @Test
  public void observationRequest(){

    player1.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);
    player2.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);


    TestHelper.waitMills(200);

    GameRoom gameRoom = lobby.getGameManager().getGames().iterator().next();
    player3.addListener(new TestObserverListener());
    player3.authenticate(PASSWORD);
    player3.observe(gameRoom.getId());

    TestHelper.waitMills(200);

    Iterator<IClientRole> roles = lobby.getClientManager().getClients().get(2).getRoles().iterator();
    boolean hasRole = false;
    while(roles.hasNext()){
      if (roles.next() instanceof ObserverRole){
        hasRole = true;
      }
    }
    Assert.assertEquals(true, hasRole);
  }

  @Test
  public void stepRequest(){

    // Make player1 Admin and prepare a game in paused mode
    player1.authenticate(PASSWORD);
    player1.prepareGame(TestPlugin.TEST_PLUGIN_UUID, true);
    TestLobbyClientListener listener = new TestLobbyClientListener();
    player1.addListener(listener);

    // Wait for messages to get to server
    Assert.assertTrue(TestHelper.waitUntilTrue(() -> listener.onGamePrepared, 1000));
    Assert.assertTrue(listener.onGamePrepared);
    Assert.assertNotNull(listener.prepareGameResponse);
    // Let Player 2 and Player 3 join the prepared game
    player2.joinPreparedGame(listener.prepareGameResponse.getReservations().get(0));
    player3.joinPreparedGame(listener.prepareGameResponse.getReservations().get(1));

    // Wait until players are registered
    Assert.assertTrue(TestHelper.waitUntilEqual(1,()->lobby.getGameManager().getGames().size(), 2000));
    GameRoom room = lobby.getGameManager().getGames().iterator().next();
    TestHelper.waitMills(100);
    PlayerListener p1Listener = new PlayerListener();
    PlayerListener p2Listener = new PlayerListener();
    Assert.assertNotNull(room.getSlots().get(0).getRole());
    room.getSlots().get(0).getRole().getPlayer().addPlayerListener(p1Listener);
    room.getSlots().get(1).getRole().getPlayer().addPlayerListener(p2Listener);

    // Make player1 Observer as well (requires Admin rights)
    player1.observe(room.getId());

    // Wait for the server to register that
    Assert.assertTrue(TestHelper.waitUntilTrue(()->listener.onObserved, 2000));
    Assert.assertNotNull(listener.roomid);
    Assert.assertFalse(listener.onGamePaused);
    Assert.assertEquals(2, room.getClients().size());
    Assert.assertEquals(true, room.isPaused());
    PlayerRole pr1 = room.getSlots().get(0).getRole();
    PlayerRole pr2 = room.getSlots().get(1).getRole();
    Assert.assertEquals(true, pr1.getPlayer().isShouldBePaused());
    Assert.assertEquals(true, pr2.getPlayer().isShouldBePaused());
    // Request a move from the first player
    player1.send(new StepRequest(room.getId()));

    // Wait for it to register
    Assert.assertTrue(TestHelper.waitUntilTrue(()->p1Listener.onPlayerEvent, 2000));
    listener.onNewState = false;

    // First player sends Move with value 42
    player2.sendMessageToRoom(room.getId(), new TestMove(42));

    // Should register as a new state
    Assert.assertTrue(TestHelper.waitUntilTrue(()->listener.onNewState, 2000));
    p1Listener.onPlayerEvent = false;

    // Request a move from the second player
    player1.send(new StepRequest(room.getId()));


    // Wait for it to register
    Assert.assertTrue(TestHelper.waitUntilTrue(()->p2Listener.onPlayerEvent, 2000));

    // Second player sends Move with value 73
    player3.sendMessageToRoom(room.getId(), new TestMove(73));
    listener.onNewState = false;

    // Should register as a new state
    Assert.assertTrue(TestHelper.waitUntilTrue(()->listener.onNewState, 2000));
    p1Listener.onPlayerEvent = false;
    p2Listener.onPlayerEvent = false;

    // There should not come another request
    Assert.assertFalse(TestHelper.waitUntilTrue(()->p1Listener.onPlayerEvent, 500));
    Assert.assertFalse(TestHelper.waitUntilTrue(()->p2Listener.onPlayerEvent, 500));

    // Try to send a message, without beeing requested
    player3.sendMessageToRoom(room.getId(), new TestMove(21));
    listener.onNewState = false;

    // Should not result in a new game state
    Assert.assertFalse(TestHelper.waitUntilTrue(()->listener.onNewState, 500));
    p1Listener.onPlayerEvent = false;
    p2Listener.onPlayerEvent = false;
    listener.onNewState = false;

    // Should not result in a new player Event
    Assert.assertFalse(TestHelper.waitUntilTrue(()->p1Listener.onPlayerEvent, 500));
    Assert.assertFalse(TestHelper.waitUntilTrue(()->p2Listener.onPlayerEvent, 500));

    // Game should be deleted, because player3 send invalid move
    Assert.assertEquals(0, lobby.getGameManager().getGames().size());

  }


}
