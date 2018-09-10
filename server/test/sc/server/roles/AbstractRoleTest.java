package sc.server.roles;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import sc.server.Configuration;
import sc.server.Lobby;
import sc.server.gaming.GameRoomManager;
import sc.server.network.AdministratorRole;
import sc.server.network.ClientManager;
import sc.server.network.MockClient;
import sc.server.plugins.GamePluginManager;
import sc.server.plugins.PluginLoaderException;
import sc.server.plugins.TestPlugin;

import java.io.IOException;

public abstract class AbstractRoleTest {

  @Before
  public void setup() throws IOException, PluginLoaderException {
    // Random PortAllocation
    Configuration.set(Configuration.PORT_KEY, "0");

    this.lobby = new Lobby();
    this.clientMgr = this.lobby.getClientManager();
    this.gameMgr = this.lobby.getGameManager();
    this.pluginMgr = this.gameMgr.getPluginManager();

    this.pluginMgr.loadPlugin(TestPlugin.class, this.gameMgr.getPluginApi());
    Assert.assertTrue(this.pluginMgr.supportsGame(TestPlugin.TEST_PLUGIN_UUID));

    this.lobby.start();
  }

  @After
  public void tearDown() {
    this.lobby.close();
  }

  protected Lobby lobby;
  protected ClientManager clientMgr;
  protected GameRoomManager gameMgr;
  protected GamePluginManager pluginMgr;

  protected MockClient connectClient(boolean administrator) {
    MockClient client;
    try {
      client = new MockClient();
      this.clientMgr.add(client);
      if (administrator) {
        client.addRole(new AdministratorRole(client));
      }
      return client;
    } catch (IOException e) {
      Assert.fail("Could not connect to server");
      return null;
    }
  }

  protected MockClient connectClient() {
    return connectClient(false);
  }

}
