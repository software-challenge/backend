package sc.server.network;

import sc.protocol.responses.ProtocolMessage;

/** Client interface, which is used to send packages to the server */
public interface IClient
{
  /**
   * Add role to the client.
   * @param role to be added
   */
  void addRole(IClientRole role);

  /**
   * Send a package.
   * @param packet to be send
   */
  void send(ProtocolMessage packet);
}
