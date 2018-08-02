package sc.server.network;

/** Interface for each Role there is */
public interface IClientRole {
  IClient getClient();

  void close();
}
