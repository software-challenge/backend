package sc.server.network;

import sc.protocol.responses.ProtocolMessage;

/** Client interface to send packages to the server. */
public interface IClient {
  /** Add role to the client. */
  void addRole(IClientRole role);

  /** Send a package. */
  void send(ProtocolMessage packet);
}
