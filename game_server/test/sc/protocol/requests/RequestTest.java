package sc.protocol.requests;

import com.thoughtworks.xstream.XStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sc.framework.plugins.RoundBasedGameInstance;
import sc.framework.plugins.SimplePlayer;
import sc.networking.clients.LobbyClient;
import sc.protocol.LobbyProtocol;
import sc.protocol.responses.PrepareGameProtocolMessage;
import sc.protocol.responses.ProtocolMessage;
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
import sc.server.plugins.PluginLoaderException;
import sc.server.plugins.TestMove;
import sc.server.plugins.TestPlugin;
import sc.server.plugins.TestTurnRequest;
import sc.shared.WelcomeMessage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;


public class RequestTest extends RealServerTest{
  private LobbyClient player1;
  private LobbyClient player2;
  private LobbyClient player3;


  private static final String PASSWORD = "TEST_PASSWORD";

  @Before
  public void prepare() {
    try {
      player1  = connectClient("localhost", getServerPort());
      TestHelper.waitMills(200);
      player2  = connectClient("localhost", getServerPort());
      TestHelper.waitMills(200);
      player3  = connectClient("localhost", getServerPort());
      TestHelper.waitMills(200);
    } catch(Exception e){
      // happens if port is already in use
      e.printStackTrace();
    }
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
    Assert.assertEquals(true, lobby.getGameManager().getGames().iterator().next().isPauseRequested());

  }

  @Test
  public void prepareXmlTest() {
    XStream xStream = new XStream();
    xStream.setMode(XStream.NO_REFERENCES);
    xStream.setClassLoader(Configuration.class.getClassLoader());
    LobbyProtocol.registerMessages(xStream);
    LobbyProtocol.registerAdditionalMessages(xStream,
            Arrays.asList(new Class<?>[] {ProtocolMessage.class}));
    Object request = (xStream.fromXML("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<prepare gameType=\"swc_2018_hase_und_igel\">\n" +
            "  <slot displayName=\"HÃ¤schenschule\" canTimeout=\"true\" shouldBePaused=\"true\"/>\n" +
            "  <slot displayName=\"Testhase\" canTimeout=\"true\" shouldBePaused=\"true\"/>\n" +
            "</prepare>"));
    Assert.assertEquals(PrepareGameRequest.class, request.getClass());
    Assert.assertEquals("HÃ¤schenschule", ((PrepareGameRequest)request).getSlotDescriptors().get(0).getDisplayName());
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
  public void stepRequestException() {
    LobbyClient admin = player1;
    LobbyClient player1 = this.player2;
    LobbyClient player2 = this.player3;
    PlayerListener p1Listener = new PlayerListener();
    PlayerListener p2Listener = new PlayerListener();

    // Make player1 Admin and prepare a game in paused mode
    admin.authenticate(PASSWORD);
    TestLobbyClientListener listener = new TestLobbyClientListener();
    admin.addListener(listener);

    player1.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);
    TestHelper.waitMills(500);

    // Room was created
    GameRoom room = lobby.getGameManager().getGames().iterator().next();
    SimplePlayer sp1 = room.getSlots().get(0).getRole().getPlayer();
    sp1.addPlayerListener(p1Listener);
    admin.send(new PauseGameRequest(room.getId(),true));
    admin.observe(room.getId());

    // Wait for admin
    TestHelper.waitUntilTrue(()->listener.observedReceived, 2000);


    player2.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);
    TestHelper.waitMills(500);
    room.getSlots().get(1).getRole().getPlayer().addPlayerListener(p2Listener);

    // Wait for the server to register that
    TestHelper.waitUntilTrue(()->room.isPauseRequested(), 2000);

    Assert.assertEquals(true, room.isPauseRequested());
    PlayerRole pr1 = room.getSlots().get(0).getRole();
    PlayerRole pr2 = room.getSlots().get(1).getRole();
    Assert.assertEquals(true, pr1.getPlayer().isShouldBePaused());
    Assert.assertEquals(true, pr2.getPlayer().isShouldBePaused());


    // Wait for it to register
    // no state will be send if game is paused TestHelper.waitUntilTrue(()->listener.newStateReceived, 2000);
    listener.newStateReceived = false;

    Assert.assertTrue(TestHelper.waitUntilTrue(()->p1Listener.playerEventReceived, 2000));
    p1Listener.playerEventReceived = false;
    Assert.assertEquals(p1Listener.requests.size(), 1);
    Assert.assertEquals(p1Listener.requests.get(0).getClass(), WelcomeMessage.class);

    player1.sendMessageToRoom(room.getId(), new TestMove(1));
    TestHelper.waitMills(100);
    Assert.assertEquals(room.getStatus(), GameRoom.GameStatus.OVER);
  }

  @Test
  public void stepRequest() throws IOException, PluginLoaderException {
    LobbyClient admin = player1;
    LobbyClient player1 = this.player2;
    LobbyClient player2 = this.player3;
    PlayerListener p1Listener = new PlayerListener();
    PlayerListener p2Listener = new PlayerListener();

    // Make player1 Admin and prepare a game in paused mode
    admin.authenticate(PASSWORD);
    TestLobbyClientListener listener = new TestLobbyClientListener();
    admin.addListener(listener);

    player1.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);
    TestHelper.waitMills(500);

    // Room was created
    GameRoom room = lobby.getGameManager().getGames().iterator().next();
    SimplePlayer sp1 = room.getSlots().get(0).getRole().getPlayer();
    sp1.addPlayerListener(p1Listener);
    admin.send(new PauseGameRequest(room.getId(),true));
    admin.observe(room.getId());

    // Wait for admin
    TestHelper.waitUntilTrue(()->listener.observedReceived, 2000);


    player2.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);
    TestHelper.waitMills(500);
    room.getSlots().get(1).getRole().getPlayer().addPlayerListener(p2Listener);

    // Wait for the server to register that
    TestHelper.waitUntilTrue(()->room.isPauseRequested(), 2000);

    PlayerRole pr1 = room.getSlots().get(0).getRole();
    PlayerRole pr2 = room.getSlots().get(1).getRole();
    Assert.assertEquals(true, pr1.getPlayer().isShouldBePaused());
    Assert.assertEquals(true, pr2.getPlayer().isShouldBePaused());


    // Wait for it to register
    // no state will be send if game is paused TestHelper.waitUntilTrue(()->listener.newStateReceived, 2000);
    listener.newStateReceived = false;

    Assert.assertTrue(TestHelper.waitUntilTrue(()->p1Listener.playerEventReceived, 2000));
    p1Listener.playerEventReceived = false;
    Assert.assertEquals(p1Listener.requests.size(), 1);
    Assert.assertEquals(p1Listener.requests.get(0).getClass(), WelcomeMessage.class);

//    enabling this should result in a GameLogicException
//    player1.sendMessageToRoom(room.getId(), new TestMove(1));
//    TestHelper.waitMills(100);

    // Request a move from the first player
    admin.send(new StepRequest(room.getId()));
    TestHelper.waitUntilTrue(()->listener.newStateReceived, 2000);
    // send move
    player1.sendMessageToRoom(room.getId(), new TestMove(1));
    listener.newStateReceived = false;

    admin.send(new StepRequest(room.getId()));
    // Wait for second players turn
    TestHelper.waitUntilTrue(()->p2Listener.playerEventReceived, 4000);
    p2Listener.playerEventReceived = false;

    // Second player sends Move with value 42
    player2.sendMessageToRoom(room.getId(), new TestMove(42));
    TestHelper.waitMills(100);

    // Request a move
    admin.send(new StepRequest(room.getId()));
    TestHelper.waitMills(100);


    // Should register as a new state
    TestHelper.waitUntilTrue(()->listener.newStateReceived, 2000);
    listener.newStateReceived = false;
    // Wait for it to register
    TestHelper.waitUntilTrue(()->p1Listener.playerEventReceived, 2000);

    // Second player sends Move not being his turn
    player2.sendMessageToRoom(room.getId(), new TestMove(73));
    TestHelper.waitUntilFalse(()->listener.newStateReceived, 1000);
    listener.newStateReceived = false;
    TestHelper.waitMills(500);

    // There should not come another request
    Assert.assertTrue(p1Listener.playerEventReceived);
    Assert.assertNotEquals(p2Listener.requests.get(p2Listener.requests.size()-1).getClass(), TestTurnRequest.class);

    // Should not result in a new game state
    Assert.assertFalse(listener.newStateReceived);
    p1Listener.playerEventReceived = false;
    p2Listener.playerEventReceived = false;
    listener.newStateReceived = false;

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
  public void timeoutRequest(){

    player1.authenticate(PASSWORD);
    TestLobbyClientListener listener = new TestLobbyClientListener();
    PlayerListener p1Listener = new PlayerListener();
    PlayerListener p2Listener = new PlayerListener();

    player1.addListener(listener);
    player1.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);
    player2.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);

    TestHelper.waitUntilEqual(1,()->lobby.getGameManager().getGames().size(), 2000);
    GameRoom room = gameMgr.getGames().iterator().next();
    Assert.assertTrue(room.getSlots().get(0).getRole().getPlayer().isCanTimeout());
    ControlTimeoutRequest req = new ControlTimeoutRequest(room.getId(), false, 0);
    player1.send(req);
    TestHelper.waitMills(2000);
    room = gameMgr.getGames().iterator().next();
    Assert.assertFalse(room.getSlots().get(0).getRole().getPlayer().isCanTimeout());


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
    TestHelper.waitMills(500);
    SimplePlayer splayer2 = room.getSlots().get(1).getRole().getPlayer();
    splayer2.addPlayerListener(p2Listener);
    splayer2.setDisplayName("player2...");

    Assert.assertFalse(room.isPauseRequested());
    TestHelper.waitUntilEqual(2, ()->p1Listener.requests.size(), 2000);
    Assert.assertEquals(p1Listener.requests.get(0).getClass(), WelcomeMessage.class);
    TestHelper.waitMills(500);
    Assert.assertEquals(p1Listener.requests.get(1).getClass(), TestTurnRequest.class);
    listener.newStateReceived = false;

    player1.send(new PauseGameRequest(room.getId(), true));
    TestHelper.waitUntilEqual(true,()->room.isPauseRequested(), 2000);

    player1.sendMessageToRoom(room.getId(), new TestMove(42));
    TestHelper.waitMills(1000);
    // assert that (if the game is paused) no new gameState is send to the observers after a pending Request was received
    Assert.assertFalse(listener.newStateReceived);


    p1Listener.playerEventReceived = false;
    p2Listener.playerEventReceived = false;
    player1.send(new PauseGameRequest(room.getId(), false));
    TestHelper.waitUntilEqual(false,()->((RoundBasedGameInstance)room.getGame()).isPaused(), 2000);



    TestHelper.waitMills(500);
    Assert.assertTrue(p2Listener.playerEventReceived);
    Assert.assertEquals(p2Listener.requests.get(p2Listener.requests.size()-1).getClass(),
            TestTurnRequest.class);
  }

}
