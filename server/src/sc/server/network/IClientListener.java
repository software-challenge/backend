package sc.server.network;

import sc.api.plugins.exceptions.RescuableClientException;
import sc.protocol.responses.ProtocolErrorMessage;
import sc.shared.InvalidGameStateException;

public interface IClientListener {

  /**
   * Invoked when a client has disconnected.
   *
   * @param source client, that disconnected
   */
  void onClientDisconnected(Client source);

  /** Invoked when new data is received and ready to be processed. */
  void onRequest(Client source, PacketCallback packet)
          throws RescuableClientException, InvalidGameStateException;

  /**
   * Invoked when a client encountered a problem.
   *
   * @param source client
   * @param packet which caused the problem
   */
  void onError(Client source, ProtocolErrorMessage packet);
}
