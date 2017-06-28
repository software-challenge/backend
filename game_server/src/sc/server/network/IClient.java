package sc.server.network;

/**
 * Client interface, which is used to send packages to the server
 */
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
  void send(Object packet);

  /**
   * Send a package asynchronous.
   * @param packet to be send
   */
  void sendAsynchronous(Object packet);
}
