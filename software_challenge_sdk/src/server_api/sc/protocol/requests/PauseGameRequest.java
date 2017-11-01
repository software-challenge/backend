package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.protocol.responses.ProtocolMessage;

/**
 * Request by administrative client to pause or unpause a game specified by given roomId.
 */

@XStreamAlias("pause")
public class PauseGameRequest extends ProtocolMessage implements ILobbyRequest {
  @XStreamAsAttribute
  public String roomId;

  @XStreamAsAttribute
  public boolean pause;

  /**
   * might be needed by XStream
   */
  public PauseGameRequest() {
  }

  public PauseGameRequest(String roomId, boolean pause) {
    this.roomId = roomId;
    this.pause = pause;
  }
}
