package sc.server.gaming;

import sc.api.plugins.host.IPlayerListener;
import sc.framework.plugins.Player;
import sc.networking.clients.XStreamClient;
import sc.protocol.responses.ProtocolMessage;
import sc.protocol.responses.RoomPacket;
import sc.server.network.IClient;
import sc.server.network.IClientRole;

/**
 * PlayerRole is a {@link IClientRole role}, which can send moves and receive data.
 * It does not have administrative rights.
 */
public class PlayerRole implements IClientRole, IPlayerListener {
  /** Actual Server-client to send Packages */
  private IClient client;
  /** XStream player */
  private Player player;
  /** The slot in the GameRoom which this PlayerRole occupies */
  private PlayerSlot playerSlot;

  public PlayerRole(IClient owner, PlayerSlot slot) {
    this.client = owner;
    this.playerSlot = slot;
  }

  @Override
  public IClient getClient() {
    return this.client;
  }

  public Player getPlayer() {
    return this.player;
  }

  public PlayerSlot getPlayerSlot() {
    return this.playerSlot;
  }

  /** Called when a move is requested. It will send a {@link RoomPacket roompacket} to the server. */
  @Override
  public void onPlayerEvent(ProtocolMessage o) {
    this.client.send(new RoomPacket(getPlayerSlot().getRoom().getId(), o));
  }

  /** Sets the player and starts listening to its events. */
  public void setPlayer(Player player) {
    this.player = player;
    this.player.addPlayerListener(this);
  }

  /** Called when client disconnects. It will close the {@link PlayerSlot playerslot} which this Object occupies. */
  @Override
  public void disconnect(XStreamClient.DisconnectCause cause) {
    getPlayerSlot().close(cause);
  }

}
