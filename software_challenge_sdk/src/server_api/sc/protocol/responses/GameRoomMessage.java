package sc.protocol.responses;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Send to all administrative clients after a playerClient joined a game via a JoinRoomRequest
 */

@XStreamAlias(value="joinedGameRoom")
public class GameRoomMessage extends ProtocolErrorMessage {

  @XStreamAsAttribute
  private String roomId;

  @XStreamAsAttribute
  private boolean existing;

  public GameRoomMessage(String roomId, boolean existing) {
    this.roomId = roomId;
    this.existing = existing;
  }

}
