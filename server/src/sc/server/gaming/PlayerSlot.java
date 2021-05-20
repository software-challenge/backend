package sc.server.gaming;

import org.jetbrains.annotations.NotNull;
import sc.api.plugins.host.IPlayerListener;
import sc.framework.plugins.Player;
import sc.networking.clients.IClient;
import sc.networking.clients.XStreamClient;
import sc.protocol.room.RoomMessage;
import sc.server.network.Client;
import sc.server.network.IClientListener;

/** Links Client and Player into a GameRoom. */
public class PlayerSlot implements IPlayerListener, IClientListener {
  /** Actual Server-client to send Packages. */
  private IClient client;
  /** Extensive Player information. */
  private Player player;
  private final GameRoom room;
  private boolean reserved;

  public PlayerSlot(@NotNull GameRoom room) {
    this.room = room;
  }

  public GameRoom getRoom() {
    return this.room;
  }

  public boolean isFree() {
    return isEmpty() && !isReserved();
  }

  public boolean isEmpty() {
    return this.client == null;
  }

  public boolean isReserved() {
    return this.reserved;
  }

  public synchronized String reserve() {
    if (isReserved())
      throw new IllegalStateException("Slot already reserved.");
    if (!isEmpty())
      throw new IllegalStateException("This slot is already occupied.");

    this.reserved = true;
    return ReservationManager.reserve(this);
  }

  public void setClient(Client client) {
    if (!isEmpty())
      throw new IllegalStateException("This slot is already occupied.");

    this.client = client;
    client.addClientListener(this);
  }

  /** Sets the player and starts listening to its events. */
  public void setPlayer(Player player) {
    this.player = player;
    player.addPlayerListener(this);
  }

  public synchronized void free() {
    if (!this.reserved)
      throw new IllegalStateException("This slot isn't reserved.");

    this.reserved = false;
  }

  public IClient getClient() {
    return this.client;
  }

  public Player getPlayer() {
    return player;
  }

  @Override
  public void onPlayerEvent(RoomMessage message) {
    client.send(getRoom().createRoomPacket(message));
  }

  @Override
  public void onClientDisconnected(Client source, XStreamClient.DisconnectCause cause) {
    getRoom().removePlayer(getPlayer(), cause);
  }
}
