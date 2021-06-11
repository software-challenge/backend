package sc.networking.clients;

import sc.protocol.ProtocolPacket;

import java.io.Closeable;

/** Client interface to send packages to the server. */
public interface IClient extends Closeable {
  /** Send a package. */
  void send(ProtocolPacket packet);
}
