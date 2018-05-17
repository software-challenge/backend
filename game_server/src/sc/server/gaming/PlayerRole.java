package sc.server.gaming;

import sc.api.plugins.host.IPlayerListener;
import sc.framework.plugins.SimplePlayer;
import sc.protocol.responses.ProtocolMessage;
import sc.protocol.responses.RoomPacket;
import sc.server.network.IClient;
import sc.server.network.IClientRole;

/**
 * PlayerRole is a {@link IClientRole role}, which can send moves and receive data.
 * It does not have administrative rights.
 */
public class PlayerRole implements IClientRole, IPlayerListener
{
  /* private fields */
  private IClient		client;		//Actual Server-client, to send Packages

  private SimplePlayer		player;  //XStream player Object, basically a wrapper

  private PlayerSlot	playerSlot; //The slot in GameRoom, which it occupies

  /* constructor */
  public PlayerRole(IClient owner, PlayerSlot slot)
  {
    this.client = owner;
    this.playerSlot = slot;
  }

  /* methods */

  /**
   * Getter for {@link IClient client}
   * @return the client
   */
  @Override
  public IClient getClient()
  {
    return this.client;
  }

  /**
   * Getter for {@link SimplePlayer player}.
   * @return the player
   */
  public SimplePlayer getPlayer()
  {
    return this.player;
  }

  /**
   * Getter for {@link PlayerSlot playerslot}.
   * @return the playerslot
   */
  public PlayerSlot getPlayerSlot()
  {
    return this.playerSlot;
  }

  /**
   * Called when a move is requested. It will send a {@link RoomPacket roompacket} to the server
   * @param o message Object
   */
  @Override
  public void onPlayerEvent(ProtocolMessage o)
  {
    this.client.send(new RoomPacket(getPlayerSlot().getRoom().getId(), o));
  }

  /**
   * Setter for the player.
   * @param player
   */
  public void setPlayer(SimplePlayer player)
  {
    this.player = player;
    this.player.addPlayerListener(this);
  }

  /**
   * Called when client disconnects. It will close the {@link PlayerSlot playerslot}, which this Object
   * occupies
   */
  @Override
  public void close()
  {
    getPlayerSlot().close();
  }

}
