package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.protocol.responses.ProtocolMessage;

/**
 * Only has effect on paused games. Send by administrative client to send a MoveRequest to current player.
 */

@XStreamAlias("step")
public class StepRequest extends ProtocolMessage implements ILobbyRequest {
  @XStreamAsAttribute
  public String roomId;

  @XStreamAsAttribute
  public boolean forced;

  public StepRequest(String roomId) {
    this(roomId, false);
  }

  public StepRequest(String roomId, boolean forced) {
    this.roomId = roomId;
    this.forced = forced;
  }

}
