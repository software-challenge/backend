package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.protocol.responses.ProtocolMessage;

@XStreamAlias("scoreForPlayer")
public class GetScoreForPlayerRequest extends ProtocolMessage implements ILobbyRequest {

  @XStreamAsAttribute
  private String displayName;

  public GetScoreForPlayerRequest(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return this.displayName;
  }
}
