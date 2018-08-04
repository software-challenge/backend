package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.protocol.responses.ProtocolMessage;

/** Used to cancel game and deleting GameRoom. Used by administrative client. */

@XStreamAlias("cancel")
public class CancelRequest implements ILobbyRequest {
  @XStreamAsAttribute
  public String roomId;
  
  public CancelRequest(String roomId) {
    this.roomId = roomId;
  }
  
}
