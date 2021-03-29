package sc.server.roles;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sc.api.plugins.exceptions.RescuableClientException;
import sc.protocol.requests.AuthenticateRequest;
import sc.protocol.requests.PrepareGameRequest;
import sc.server.Configuration;
import sc.server.network.Client;
import sc.server.network.MockClient;
import sc.server.network.PacketCallback;
import sc.server.plugins.TestPlugin;

public class AdministratorTest extends AbstractRoleTest {
  private static final String CORRECT_PASSWORD = "this-is-a-secret";
  private static final String WRONG_PASSWORD = "i-am-a-hacker";

  @Test
  public void shouldBecomeAdminWithCorrectPassword() {
    Client client = connectAsAdmin();

    Assertions.assertEquals(1, client.getRoles().size());
    Assertions.assertEquals(true, client.isAdministrator());
  }

  @Test
  public void shouldNotBecomeAdminWithWrongPassword() {
    Client client = connectClient();

    Configuration.set(Configuration.PASSWORD_KEY, CORRECT_PASSWORD);

    try {
      this.lobby.onRequest(client, new PacketCallback(new AuthenticateRequest(WRONG_PASSWORD)));
      Assertions.fail("No exception was thrown");
    } catch (RescuableClientException e) {
      // expected
    }

    Assertions.assertEquals(0, client.getRoles().size());
    Assertions.assertEquals(false, client.isAdministrator());
  }

  protected MockClient connectAsAdmin() {
    MockClient client = null;
    client = connectClient();

    Configuration.set(Configuration.PASSWORD_KEY, CORRECT_PASSWORD);

    try {
      this.lobby.onRequest(client, new PacketCallback(new AuthenticateRequest(CORRECT_PASSWORD)));
    } catch (RescuableClientException e) {
      Assertions.fail("Could not authenticate as admin.");
    }

    return client;
  }

  @Test
  public void shouldBeAbleToPrepareGame() throws RescuableClientException {
    Client client = connectAsAdmin();
    this.lobby.onRequest(client, new PacketCallback(new PrepareGameRequest(TestPlugin.TEST_PLUGIN_UUID)));
    Assertions.assertEquals(1, this.gameMgr.getGames().size());
  }

}
