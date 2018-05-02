package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.protocol.responses.ProtocolMessage;

@XStreamAlias("timeout")
public class ControlTimeoutRequest  extends ProtocolMessage implements ILobbyRequest{
  @XStreamAsAttribute
  public String roomId;

  @XStreamAsAttribute
  public boolean activate;

  @XStreamAsAttribute
  public int slot;

  public ControlTimeoutRequest(String roomId, boolean activate, int slot) {
    this.roomId = roomId;
    this.activate = activate;
    this.slot = slot;
  }

}
