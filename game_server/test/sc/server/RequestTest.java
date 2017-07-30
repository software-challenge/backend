package sc.server;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sc.networking.clients.LobbyClient;
import sc.protocol.responses.PrepareGameResponse;
import sc.server.client.ObserverListener;
import sc.server.client.PreparedGameResponseListener;
import sc.server.gaming.GameRoom;
import sc.server.gaming.ObserverRole;
import sc.server.helpers.TestHelper;
import sc.server.network.Client;
import sc.server.network.IClientRole;
import sc.server.network.RealServerTest;
import sc.server.plugins.TestPlugin;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

//import org.mockito.;


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
    TestHelper.waitMills(1000);
    LinkedList<Client> clients = lobby.getClientManager().getClients();
    Assert.assertEquals(true, clients.get(0).isAdministrator());
    Assert.assertEquals(3,lobby.getClientManager().getClients().size());

    player2.authenticate("PASSWORD_FAIL_TEST");
    TestHelper.waitMills(1000);

    //Player2 got kicked
    Assert.assertEquals(2,lobby.getClientManager().getClients().size());
    Assert.assertEquals(false, clients.get(1).isAdministrator());

  }

  @Test
  public void prepareRoomRequest(){

    player1.authenticate(PASSWORD);
    player1.prepareGame(TestPlugin.TEST_PLUGIN_UUID, true);
    PreparedGameResponseListener listener = new PreparedGameResponseListener();
    player1.addListener(listener);

    TestHelper.waitMills(500);
    Assert.assertNotNull(listener.response);

    Assert.assertEquals(1,  lobby.getGameManager().getGames().size());
    Assert.assertEquals(0, lobby.getGameManager().getGames().iterator().next().getClients().size());
    Assert.assertEquals(true, lobby.getGameManager().getGames().iterator().next().isPaused());

  }

  @Test
  public void joinPreparedRoomRequest(){
    player1.authenticate(PASSWORD);
    PreparedGameResponseListener listener = new PreparedGameResponseListener();
    player1.addListener(listener);

    player1.prepareGame(TestPlugin.TEST_PLUGIN_UUID);
    TestHelper.waitMills(400);
    PrepareGameResponse response = listener.response;

    String reservation = response.getReservations().get(0);
    player1.joinPreparedGame(reservation);
    TestHelper.waitMills(500);
    Assert.assertEquals(1, lobby.getGameManager().getGames().iterator().next().getClients().size());

    player2.joinPreparedGame(response.getReservations().get(1));
    TestHelper.waitMills(500);
    Assert.assertEquals(2, lobby.getGameManager().getGames().iterator().next().getClients().size());

    player3.joinPreparedGame(response.getReservations().get(1));
    TestHelper.waitMills(500);
    Assert.assertEquals(2, lobby.getClientManager().getClients().size());
  }

  @Test
  public void observationRequest(){

    player1.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);
    player2.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);
    TestHelper.waitMills(1000);

    GameRoom gameRoom = lobby.getGameManager().getGames().iterator().next();
    player3.addListener(new ObserverListener());
    player3.authenticate(PASSWORD);
    player3.observe(gameRoom.getId());

    TestHelper.waitMills(1000);

    Iterator<IClientRole> roles = lobby.getClientManager().getClients().get(2).getRoles().iterator();
    boolean hasRole = false;
    while(roles.hasNext()){
      if (roles.next() instanceof ObserverRole){
        hasRole = true;
      }
    }
    Assert.assertEquals(true, hasRole);
    gameRoom = lobby.getGameManager().getGames().iterator().next();
  }


}
