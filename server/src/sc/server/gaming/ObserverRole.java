package sc.server.gaming;

import sc.server.network.Client;
import sc.server.network.IClientRole;

/** Client Role, which can watch, but not send any moves */
public class ObserverRole implements IClientRole {
  /** Actual server Client */
  private Client client;
  /** Room which this object belongs to */
  private GameRoom gameRoom;

  /**
   * Create Observer Role from {@link Client client} and {@link GameRoom gameroom}
   *
   * @param owner    Server-client that sends packages
   * @param gameRoom The room, from which this observer is listening
   */
  public ObserverRole(Client owner, GameRoom gameRoom) {
    this.client = owner;
    this.gameRoom = gameRoom;
  }

  /**
   * Getter for {@link Client client}.
   *
   * @return the client
   */
  @Override
  public Client getClient() {
    return this.client;
  }

  /**
   * Getter for the {@link GameRoom gameroom}
   *
   * @return the Gameroom
   */
  public GameRoom getGameRoom() {
    return this.gameRoom;
  }

  @Override
  public void close() {
    //TODO close Observer if necessary
  }

}
