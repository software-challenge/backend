package sc.server.network;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import sc.networking.clients.XStreamClient;
import sc.protocol.responses.ProtocolErrorMessage;
import sc.server.helpers.ExamplePacket;
import sc.server.helpers.StringNetworkInterface;

import java.io.IOException;

public class ClientXmlReadTest {
  private static class StupidClientListener implements IClientListener {
    public Object LastPacket = null;

    @Override
    public void onRequest(Client source, PacketCallback callback) {
      callback.setProcessed();
      this.LastPacket = callback.getPacket();
    }

    @Override
    public void onClientDisconnected(Client source) {
      // ignore
    }

    @Override
    public void onError(Client source, ProtocolErrorMessage packet) {
      // ignore
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

    client.addClientListener(clientListener);
    client.start();

    Assertions.assertNotNull(client.receive());
    Assertions.assertNotNull(clientListener.LastPacket);
    Assertions.assertTrue(clientListener.LastPacket instanceof ExamplePacket);
  }

  @Test
  public void clientSendPacketTest() throws IOException {
    StringNetworkInterface stringInterface = new StringNetworkInterface(EMPTY_OBJECT_STREAM);
    Client client = new Client(stringInterface);
    aliasExamplePacket(client);

    client.start();
    client.send(new ExamplePacket());
    String data = stringInterface.getData();
    Assertions.assertTrue(data.startsWith("<protocol>\n  <example"));
  }

  private void aliasExamplePacket(XStreamClient client) {
    client.getXStream().alias("example", ExamplePacket.class);
  }

}
