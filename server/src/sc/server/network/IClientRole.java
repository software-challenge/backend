package sc.server.network;

import sc.networking.clients.XStreamClient;

/** Interface for each Role there is */
public interface IClientRole {
  IClient getClient();

  default void disconnect(XStreamClient.DisconnectCause cause) {
    close();
  }

  default void close() { }

}
