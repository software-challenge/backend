package sc.server.network;

import com.thoughtworks.xstream.XStream;
import sc.networking.TcpNetwork;
import sc.networking.clients.XStreamClient;
import sc.protocol.responses.ProtocolMessage;

import java.io.IOException;
import java.net.Socket;

public class TestTcpClient extends XStreamClient {
  public TestTcpClient(XStream xstream, Socket socket) throws IOException {
    super(xstream, new TcpNetwork(socket));
  }

  @Override
  protected void onObject(ProtocolMessage o) {
    // ignore it
    // LoggerFactory.getLogger(this.getClass()).debug("Received: {}", o);
  }

}
