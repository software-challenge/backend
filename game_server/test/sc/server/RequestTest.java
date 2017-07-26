package sc.server;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import sc.networking.clients.LobbyClient;
import sc.protocol.responses.PrepareGameResponse;
import sc.server.client.PreparedGameResponseListener;
import sc.server.gaming.GameRoom;
import sc.server.helpers.TestHelper;
import sc.server.network.RealServerTest;
import sc.server.plugins.TestPlugin;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

//import org.mockito.;


public class RequestTest extends RealServerTest{
  LobbyClient player1;
  LobbyClient player2;

  @Before
  public void prepare() {
    try {
      player1  = connectClient("localhost", getServerPort());
      player2  = connectClient("localhost", getServerPort());
    } catch(Exception e){}
  }

  @After
  public void terminate() {
  }

  @Test
  public void joinRoomRequest () {
    player1.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);

    TestHelper.assertEqualsWithTimeout(1,()->lobby.getGameManager().getGames().size());
    Assert.assertEquals(1,lobby.getGameManager().getGames().iterator().next().getClients().size());
  }

  @Test
  public void joinPreparedRoomRequest(){
    final Object waiting = new Object();
    PrepareGameResponse gameResponse;

    PreparedGameResponseListener listener = new PreparedGameResponseListener(){
      @Override
      public void onGamePrepared(PrepareGameResponse gameResponse) {
        super.onGamePrepared(gameResponse);
        System.out.println("==============");
        System.out.println("==============");
        synchronized (waiting){
          waiting.notify();
        }
      }
    };
    player1.addListener(listener);
    player1.prepareGame(TestPlugin.TEST_PLUGIN_UUID);

    TestHelper.waitForObject(waiting);

    PrepareGameResponse response = listener.response;
    Assert.assertEquals(1,lobby.getGameManager().getGames().size());
    Assert.assertEquals(0, lobby.getGameManager().getGames().iterator().next().getClients().size());
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
    
  }

}
