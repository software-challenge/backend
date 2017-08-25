package sc.server;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import sc.networking.clients.LobbyClient;
import sc.server.gaming.GameRoom;
import sc.server.helpers.TestHelper;
import sc.server.network.ClientManager;
import sc.server.network.RealServerTest;
import sc.server.plugins.TestPlugin;

/**
 * Created by nils on 05.07.17.
 */
public class LobbyTest extends RealServerTest{

  @Ignore // TODO update to new System
  public void shouldConnectAndDisconnect(){
    try {
      final LobbyClient player1 = connectClient("localhost", getServerPort());
      final LobbyClient player2 = connectClient("localhost", getServerPort());
      final LobbyClient player_fail = connectClient("localhost", getServerPort());

      player1.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);
      player2.joinRoomRequest(TestPlugin.TEST_PLUGIN_UUID);

      /* Was game created? */
      TestHelper.assertEqualsWithTimeout(1,()->lobby.getGameManager().getGames().size());
      Assert.assertNotNull(lobby.getGameManager().getGames());
      Assert.assertNotEquals(0,lobby.getGameManager().getGames().size());
      GameRoom gameRoom = lobby.getGameManager().getGames().iterator().next();

      Assert.assertNotNull(lobby.getClientManager());
      ClientManager clientManager = lobby.getClientManager();

      player1.stop();
      Thread.sleep(1000);
      Assert.assertEquals(0, lobby.getGameManager().getGames().size());

    } catch (Exception e){
      e.printStackTrace();
      Assert.fail();
    }
  }
}
