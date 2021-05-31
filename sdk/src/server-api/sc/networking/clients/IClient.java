package sc.networking.clients;

import sc.protocol.ProtocolPacket;

/** Client interface to send packages to the server. */
public interface IClient {
  /** Send a package. */
  void send(ProtocolPacket packet);
}
