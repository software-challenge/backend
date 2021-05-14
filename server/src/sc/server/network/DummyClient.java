package sc.server.network;

import sc.protocol.ProtocolPacket;

/** A fake client to fill empty player slots. */
public class DummyClient implements IClient {
  @Override
  public void addRole(IClientRole role) {
    // ignore
  }

  @Override
  public void send(ProtocolPacket toSend) {
    // ignore
  }

}
