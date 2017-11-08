package sc.protocol.requests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sc.framework.plugins.protocol.MoveRequest;
import sc.networking.clients.LobbyClient;
import sc.protocol.responses.PrepareGameProtocolMessage;
import sc.server.Configuration;
import sc.server.client.PlayerListener;
import sc.server.client.TestLobbyClientListener;
import sc.server.client.TestObserverListener;
import sc.server.client.TestPreparedGameResponseListener;
import sc.server.gaming.GameRoom;
import sc.server.gaming.ObserverRole;
import sc.server.gaming.PlayerRole;
import sc.server.helpers.TestHelper;
import sc.server.network.*;
import sc.server.plugins.TestMove;
import sc.server.plugins.TestPlugin;
import sc.server.plugins.TestTurnRequest;
import sc.shared.WelcomeMessage;

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
    Assert.assertTrue(TestHelper.waitUntilTrue(() -> listener.gamePreparedReceived, 1000));
    Assert.assertTrue(listener.gamePreparedReceived);
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
    Assert.assertTrue(TestHelper.waitUntilTrue(()->listener.observedReceived, 2000));
    Assert.assertNotNull(listener.roomid);
    Assert.assertFalse(listener.gamePausedReceived);
    Assert.assertEquals(2, room.getClients().size());
    Assert.assertEquals(true, room.isPaused());
    PlayerRole pr1 = room.getSlots().get(0).getRole();
    PlayerRole pr2 = room.getSlots().get(1).getRole();
    Assert.assertEquals(true, pr1.getPlayer().isShouldBePaused());
    Assert.assertEquals(true, pr2.getPlayer().isShouldBePaused());
    // Request a move from the first player
    player1.send(new StepRequest(room.getId()));

    // Wait for it to register
    Assert.assertTrue(TestHelper.waitUntilTrue(()->p1Listener.playerEventReceived, 2000));
    listener.newStateReceived = false;

    // First player sends Move with value 42
    player2.sendMessageToRoom(room.getId(), new TestMove(42));

    // Should register as a new state
    Assert.assertTrue(TestHelper.waitUntilTrue(()->listener.newStateReceived, 2000));
    p1Listener.playerEventReceived = false;

    // Request a move from the second player
    player1.send(new StepRequest(room.getId()));


    // Wait for it to register
    Assert.assertTrue(TestHelper.waitUntilTrue(()->p2Listener.playerEventReceived, 2000));

    // Second player sends Move with value 73
    player3.sendMessageToRoom(room.getId(), new TestMove(73));
    listener.newStateReceived = false;

    // Should register as a new state
    Assert.assertTrue(TestHelper.waitUntilTrue(()->listener.newStateReceived, 2000));
    p1Listener.playerEventReceived = false;
    p2Listener.playerEventReceived = false;

    // There should not come another request
    Assert.assertFalse(TestHelper.waitUntilTrue(()->p1Listener.playerEventReceived, 500));
    Assert.assertFalse(TestHelper.waitUntilTrue(()->p2Listener.playerEventReceived, 500));

    // Try to send a message, without beeing requested
    player3.sendMessageToRoom(room.getId(), new TestMove(21));
    listener.newStateReceived = false;

    // Should not result in a new game state
    Assert.assertFalse(TestHelper.waitUntilTrue(()->listener.newStateReceived, 500));
    p1Listener.playerEventReceived = false;
    p2Listener.playerEventReceived = false;
    listener.newStateReceived = false;

    // Should not result in a new player Event
    Assert.assertFalse(TestHelper.waitUntilTrue(()->p1Listener.playerEventReceived, 500));
    Assert.assertFalse(TestHelper.waitUntilTrue(()->p2Listener.playerEventReceived, 500));

    // Game should be deleted, because player3 send invalid move
    Assert.assertEquals(0, lobby.getGameManager().getGames().size());

  }


  @Test
  public void cancelRequest(){

    player1.authenticate(PASSWORD);
    player1.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);
    player2.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);
    TestLobbyClientListener listener = new TestLobbyClientListener();
    player1.addListener(listener);

    // Wait for messages to get to server
    Assert.assertTrue(TestHelper.waitUntilTrue(() -> lobby.getGameManager().getGames().size() > 0, 1000));

    player1.send(new CancelRequest(listener.roomid));
    Assert.assertTrue(TestHelper.waitUntilTrue(() -> lobby.getGameManager().getGames().size() == 0, 1000));
    Assert.assertEquals(0,lobby.getGameManager().getGames().size());
  }

  @Test
  public void testModeRequest(){

    player1.authenticate(PASSWORD);
    player1.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);
    player2.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);
    TestLobbyClientListener listener = new TestLobbyClientListener();
    player1.addListener(listener);

    player1.send(new TestModeRequest(true));
    TestHelper.assertEqualsWithTimeout("true",()->Configuration.get(Configuration.TEST_MODE), 1000);

    player1.send(new TestModeRequest(false));
    TestHelper.assertEqualsWithTimeout("false",()->Configuration.get(Configuration.TEST_MODE), 1000);


  }

  @Test
  public void getScoreForPlayerRequest(){
    //TODO implement
  }

  @Test
  public void pauseRequest() {
    player1.authenticate(PASSWORD);
    TestLobbyClientListener listener = new TestLobbyClientListener();
    PlayerListener p1Listener = new PlayerListener();
    PlayerListener p2Listener = new PlayerListener();

    player1.addListener(listener);
    player1.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);
    TestHelper.waitUntilEqual(1,()->lobby.getGameManager().getGames().size(), 2000);
    GameRoom room = gameMgr.getGames().iterator().next();
    room.getSlots().get(0).getRole().getPlayer().addPlayerListener(p1Listener);
    player2.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);
    TestHelper.waitUntilEqual(2, ()->room.getSlots().size(), 2000);
    room.getSlots().get(1).getRole().getPlayer().addPlayerListener(p2Listener);

    Assert.assertFalse(room.isPaused());
    TestHelper.waitUntilEqual(2, ()->p1Listener.requests.size(), 2000);
    Assert.assertEquals(p1Listener.requests.get(0).getClass(), WelcomeMessage.class);
    Assert.assertEquals(p1Listener.requests.get(1).getClass(), TestTurnRequest.class);
    listener.newStateReceived = false;
    player1.send(new PauseGameRequest(room.getId(), true));
    TestHelper.waitUntilEqual(true,()->room.isPaused(), 2000);

    player1.sendMessageToRoom(room.getId(), new TestMove(0));
    TestHelper.waitMills(500);
    Assert.assertFalse(listener.newStateReceived);
    player1.send(new PauseGameRequest(room.getId(), false));
    TestHelper.waitUntilEqual(false,()->room.isPaused(), 2000);

    p1Listener.playerEventReceived = false;
    p2Listener.playerEventReceived = false;
    player1.send(new PauseGameRequest(room.getId(), true));
    TestHelper.waitUntilEqual(true,()->room.isPaused(), 2000);

    Assert.assertTrue(p1Listener.playerEventReceived);
    Assert.assertEquals(p2Listener.requests.get(p2Listener.requests.size()-1).getClass(),
            TestTurnRequest.class);





  }

}
