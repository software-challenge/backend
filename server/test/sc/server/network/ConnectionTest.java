package sc.server.network;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import sc.networking.clients.XStreamClient.DisconnectCause;
import sc.protocol.requests.JoinRoomRequest;
import sc.protocol.ProtocolPacket;
import sc.server.helpers.TestHelper;
import sc.server.plugins.TestPlugin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class ConnectionTest extends RealServerTest {
  private static class DontYouKnowJack implements ProtocolPacket {
    public int test = 25;
  }

  @Test
  public void connectionTest() {
    TestTcpClient client = connectClient();
    waitForConnect(1);

    client.send(new JoinRoomRequest(TestPlugin.TEST_PLUGIN_UUID));
    TestHelper.INSTANCE.assertEqualsWithTimeout(1, () -> getLobby().getGames().size(), 1, TimeUnit.SECONDS);
  }

  @Disabled
  @Test //TODO seems so fail sometimes
  public void protocolViolationTestWithCorruptedXml() throws IOException {
    TestTcpClient client = connectClient();
    waitForConnect(1);
    client.sendCustomData("<>/I-do/>NOT<CARE".getBytes(StandardCharsets.UTF_8));

    TestHelper.INSTANCE.assertEqualsWithTimeout(DisconnectCause.PROTOCOL_ERROR,
        () -> getLobby().getClientManager().getClients().get(0).getDisconnectCause(),
        5, TimeUnit.SECONDS);
  }

  @Disabled
  @Test //TODO seems so fail sometimes
  public void protocolViolationTestWithUnknownClass() throws IOException {
    TestTcpClient client = connectClient();
    waitForConnect(1);
    client.sendCustomData("<object-stream>".getBytes(StandardCharsets.UTF_8));

    client.sendCustomData("<NoSuchClass foo=\"aaa\"><base val=\"arr\" /></NoSuchClass>".getBytes(StandardCharsets.UTF_8));
    waitForConnect(1);
    TestHelper.INSTANCE.assertEqualsWithTimeout(DisconnectCause.PROTOCOL_ERROR,
        () -> getLobby().getClientManager().getClients().get(0).getDisconnectCause(),
        5, TimeUnit.SECONDS);
  }

  @Disabled
  @Test //TODO should be tested, but fails sometimes, client is removed when sending unknown class
  public void protocolViolationTestWithUnknownClasses() {
    TestTcpClient client = connectClient();
    waitForConnect(1);
    client.send(new DontYouKnowJack());
    waitForConnect(1);
    TestHelper.INSTANCE.assertEqualsWithTimeout(DisconnectCause.PROTOCOL_ERROR,
        () -> getLobby().getClientManager().getClients().get(0).getDisconnectCause(),
        1, TimeUnit.SECONDS);
  }

}
