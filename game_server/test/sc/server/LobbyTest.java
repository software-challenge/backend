package sc.server;

import org.junit.Assert;
import org.junit.Test;
import sc.networking.clients.LobbyClient;
import sc.server.gaming.GameRoom;
import sc.server.helpers.TestHelper;
import sc.server.network.RealServerTest;
import sc.server.plugins.TestPlugin;

/**
 * Created by nils on 05.07.17.
 */
public class LobbyTest extends RealServerTest{
  @Test
  public void shouldConnect(){
    try {
      final LobbyClient player1 = connectClient("localhost", getServerPort());

      final LobbyClient player2 = connectClient("localhost", getServerPort());
      final LobbyClient player_fail = connectClient("localhost", getServerPort());

      player1.joinAnyGame(TestPlugin.TEST_PLUGIN_UUID);
      player2.joinAnyGame(TestPlugin.TEST_PLUGIN_UUID);

      /* Was game created? */
      TestHelper.assertEqualsWithTimeout(1,()->lobby.getGameManager().getGames().size());
      GameRoom gameRoom = lobby.getGameManager().getGames().iterator().next();


      /* player_fail should not be able to join */
      


    } catch (Exception e){
      Assert.assertFalse(false);
    }
  }
}
