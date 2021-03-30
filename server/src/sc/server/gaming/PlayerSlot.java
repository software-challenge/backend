package sc.server.gaming;

import sc.framework.plugins.Player;
import sc.networking.clients.XStreamClient;
import sc.server.network.IClient;
import sc.shared.SlotDescriptor;

public class PlayerSlot {
  private PlayerRole role;
  private final GameRoom room;
  private boolean reserved;
  private String displayName;
  private SlotDescriptor descriptor;

  public PlayerSlot(GameRoom room) {
    if (room == null)
      throw new IllegalStateException("Room must not be null.");

    this.room = room;
    descriptor = new SlotDescriptor();
  }

  public PlayerRole getRole() {
    return this.role;
  }

  public GameRoom getRoom() {
    return this.room;
  }

  public boolean isEmpty() {
    return this.role == null;
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

  public void setClient(IClient client) {
    if (!isEmpty())
      throw new IllegalStateException("This slot is already occupied.");

    this.role = new PlayerRole(client, this);
    client.addRole(this.role);
  }

  public void setPlayer(Player player) {
    if (this.role == null)
      throw new IllegalStateException("Slot isn't linked to a Client yet.");

    this.role.setPlayer(player);
  }

  public synchronized void free() {
    if (!this.reserved)
      throw new IllegalStateException("This slot isn't reserved.");

    this.reserved = false;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    if (this.displayName == null)
      return "Unknown";

    return this.displayName;
  }

  public SlotDescriptor getDescriptor() {
    return this.descriptor;
  }

  public void setDescriptor(SlotDescriptor descriptor) {
    this.descriptor = descriptor != null ? descriptor : new SlotDescriptor();
  }

  public IClient getClient() {
    if (this.role == null)
      return null;

    return this.role.getClient();
  }

  public void close(XStreamClient.DisconnectCause cause) {
    this.getRoom().removePlayer(this.getRole().getPlayer(), cause);
  }

}
