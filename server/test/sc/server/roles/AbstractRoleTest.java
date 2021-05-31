package sc.server.roles;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import sc.server.Configuration;
import sc.server.Lobby;
import sc.server.gaming.GameRoomManager;
import sc.server.network.AuthenticationFailedException;
import sc.server.network.ClientManager;
import sc.server.network.MockClient;
import sc.server.plugins.GamePluginManager;
import sc.server.plugins.PluginLoaderException;
import sc.server.plugins.TestPlugin;

import java.io.IOException;

public abstract class AbstractRoleTest {

  protected Lobby lobby;
  protected ClientManager clientMgr;
  protected GameRoomManager gameMgr;
  protected GamePluginManager pluginMgr;

  @BeforeEach
  public void setup() throws IOException, PluginLoaderException {
    // Random PortAllocation
    Configuration.set(Configuration.PORT_KEY, "0");

    this.lobby = new Lobby();
    this.clientMgr = this.lobby.getClientManager();
    this.gameMgr = this.lobby;
    this.pluginMgr = this.gameMgr.getPluginManager();

    this.pluginMgr.loadPlugin(TestPlugin.class);
    Assertions.assertTrue(this.pluginMgr.supportsGame(TestPlugin.TEST_PLUGIN_UUID));

    this.lobby.start();
  }

  @AfterEach
  public void tearDown() {
    this.lobby.close();
  }

  protected MockClient connectClient(boolean administrator) {
    MockClient client;
    try {
      client = new MockClient();
      this.clientMgr.add(client);
      if (administrator) {
        client.authenticate(Configuration.getAdministrativePassword());
      }
      return client;
    } catch (IOException | AuthenticationFailedException e) {
      return Assertions.fail("Could not connect to server");
    }
  }

  protected MockClient connectClient() {
    return connectClient(false);
  }

}
