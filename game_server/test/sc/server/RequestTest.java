package sc.server;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sc.networking.clients.LobbyClient;
import sc.protocol.responses.PrepareGameResponse;
import sc.server.client.ObserverListener;
import sc.server.client.PreparedGameResponseListener;
import sc.server.gaming.GameRoom;
import sc.server.helpers.TestHelper;
import sc.server.network.RealServerTest;
import sc.server.plugins.TestGame;
import sc.server.plugins.TestPlugin;


//import org.mockito.;


public class RequestTest extends RealServerTest{
  LobbyClient player1;
  LobbyClient player2;
  LobbyClient player3;

  @Before
  public void prepare() {
    try {
      player1  = connectClient("localhost", getServerPort());
      player2  = connectClient("localhost", getServerPort());
      //player3  = connectClient("localhost", getServerPort());
    } catch(Exception e){}
  }

  @Test
  public void joinRoomRequest () {
    System.out.println("===> Join Room Request Test");
    player1.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);

    TestHelper.assertEqualsWithTimeout(1,()->lobby.getGameManager().getGames().size());
    Assert.assertEquals(1,lobby.getGameManager().getGames().iterator().next().getClients().size());
  }


  @Test
  public void joinPreparedRoomRequest(){
    System.out.println("===> Join Prepared Room Request Test");
    final Object waiting = new Object();
    PrepareGameResponse gameResponse;
    TestHelper.waitMills(1000);

    PreparedGameResponseListener listener = new PreparedGameResponseListener();
    player1.addListener(listener);

    player1.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);

    TestHelper.waitMills(1000);
    Assert.assertNotNull(listener.response);
    PrepareGameResponse response = listener.response;
    Assert.assertEquals(1,  lobby.getGameManager().getGames().size());
    Assert.assertEquals(1, lobby.getGameManager().getGames().iterator().next().getClients().size());
    GameRoom gameRoom = lobby.getGameManager().getGames().iterator().next();
    String reservation = response.getReservations().get(0);
    Assert.assertEquals(0, lobby.getGameManager().getGames().iterator().next().getClients().size());
    player1.joinPreparedGame(reservation);
    TestHelper.waitMills(1000);
    Assert.assertEquals(1, lobby.getGameManager().getGames().iterator().next().getClients().size());
    player1.joinPreparedGame(reservation);
    TestHelper.waitMills(1000);
    Assert.assertEquals(0, lobby.getGameManager().getGames().size());
  }

  @Test
  public void observationRequest(){

    player1.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);
    player2.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);
    TestHelper.waitMills(1000);

    GameRoom gameRoom = lobby.getGameManager().getGames().iterator().next();
    player3.addListener(new ObserverListener());
    player3.authenticate("password");
    player3.observe(gameRoom.getId());

    TestHelper.waitMills(1000);


    gameRoom = lobby.getGameManager().getGames().iterator().next();

  }

}
