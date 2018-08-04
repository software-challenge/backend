package sc.server.network;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import sc.networking.clients.LobbyClient;
import sc.server.Configuration;
import sc.server.Lobby;
import sc.server.gaming.GameRoomManager;
import sc.server.helpers.TestHelper;
import sc.server.plugins.GamePluginManager;
import sc.server.plugins.PluginLoaderException;
import sc.server.plugins.TestPlugin;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public abstract class RealServerTest {
  protected Lobby lobby;
  protected ClientManager clientMgr;
  protected GameRoomManager gameMgr;
  protected GamePluginManager pluginMgr;

  public LobbyClient connectClient(String host, int port) throws IOException {
    LobbyClient client = new LobbyClient(Configuration.getXStream(), null, host, port);
    client.start();
    return client;
  }

  @Before
  public void setup() throws IOException, PluginLoaderException {
    // Random PortAllocation
    Configuration.set(Configuration.PORT_KEY, "0");
    Configuration.set(Configuration.PASSWORD_KEY, "TEST_PASSWORD");
    this.lobby = new Lobby();
    this.clientMgr = this.lobby.getClientManager();
    this.gameMgr = this.lobby.getGameManager();
    this.pluginMgr = this.gameMgr.getPluginManager();

    this.pluginMgr.loadPlugin(TestPlugin.class, this.gameMgr.getPluginApi());
    Assert.assertTrue(this.pluginMgr.supportsGame(TestPlugin.TEST_PLUGIN_UUID));

    NewClientListener.lastUsedPort = 0;
    this.lobby.start();
    waitForServer();
  }

  @After
  public void tearDown() {
    this.lobby.close();
  }

  private void waitForServer() {
    while (NewClientListener.lastUsedPort == 0) {
      Thread.yield();
    }
  }

  protected void waitForConnect(int count) {
    TestHelper.assertEqualsWithTimeout(count, () -> RealServerTest.this.lobby.getClientManager().clients.size(), 1, TimeUnit.SECONDS);
  }

  protected int getServerPort() {
    return NewClientListener.lastUsedPort;
  }

  protected TestTcpClient connectClient() {
    try {
      if (getServerPort() == 0) {
        throw new RuntimeException(
                "Could not find an open port to connect to.");
      }
      Socket mySocket = new Socket("localhost",
              NewClientListener.lastUsedPort);
      TestTcpClient result = new TestTcpClient(
              Configuration.getXStream(), mySocket);
      result.start();
      return result;
    } catch (IOException e) {
      e.printStackTrace();
      Assert.fail("Could not connect to server.");
      return null;
    }
  }

}
