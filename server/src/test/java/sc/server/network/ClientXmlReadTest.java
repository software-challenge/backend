package sc.server.network;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import sc.networking.clients.XStreamClient;
import sc.server.helpers.ExamplePacket;
import sc.server.helpers.MockClient;
import sc.server.helpers.StringNetworkInterface;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ClientXmlReadTest {
  private static class StupidClientListener implements IClientRequestListener {
    public Object lastPacket = null;
    @Override
    public void onRequest(Client source, PacketCallback callback) {
      callback.setProcessed();
      this.lastPacket = callback.getPacket();
    }
  }

  /** Denotes an empty ObjectStream (to be used with XStream). */
  private static final String EMPTY_OBJECT_STREAM = "<protocol></protocol>";

  @Test @Timeout(2)
  public void clientReceivePacketTest() throws IOException, InterruptedException {
    StringNetworkInterface stringInterface = new StringNetworkInterface(
            "<protocol>\n<example />");
    StupidClientListener clientListener = new StupidClientListener();
    MockClient client = new MockClient(stringInterface);
    aliasExamplePacket(client);

    client.setRequestHandler(clientListener);
    client.start();

    assertNotNull(client.receive());
    assertNotNull(clientListener.lastPacket);
    assertTrue(clientListener.lastPacket instanceof ExamplePacket);
  }

  @Test
  public void clientSendPacketTest() throws IOException {
    StringNetworkInterface stringInterface = new StringNetworkInterface(EMPTY_OBJECT_STREAM);
    Client client = new Client(stringInterface);
    aliasExamplePacket(client);

    client.start();
    client.send(new ExamplePacket());
    String data = stringInterface.getData();
    assertEquals("<protocol>\n  <example/>", data);
  }

  private void aliasExamplePacket(XStreamClient client) {
    client.getXStream().alias("example", ExamplePacket.class);
  }

}
