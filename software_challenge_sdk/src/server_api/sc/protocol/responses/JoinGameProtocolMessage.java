package sc.protocol.responses;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Response to client who successfully joined a game.
 * example of such a response:
 * {@code
 * <protocol>
 *   <joined roomId="7dc299b1-dcd5-4854-8a02-90510754b943"/>
 * }
 */
@XStreamAlias(value = "joined")
public class JoinGameProtocolMessage extends ProtocolMessage {
  @XStreamAsAttribute
  private String roomId;

  /** might be needed by XStream */
  public JoinGameProtocolMessage() {
  }

  public JoinGameProtocolMessage(String id) {
    this.roomId = id;
  }

  public String getRoomId() {
    return this.roomId;
  }

}
