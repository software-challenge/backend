package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.protocol.responses.ProtocolMessage;

/**
 * Request of administrative client to observe a gameRoom specified by given roomId.
 */

@XStreamAlias("observe")
public class ObservationRequest extends ProtocolMessage implements ILobbyRequest {
  @XStreamAsAttribute
  private String roomId;

  /**
   * might be needed by XStream
   */
  public ObservationRequest() {
  }


  public ObservationRequest(String roomId) {
    this.roomId = roomId;
  }

  public String getRoomId() {
    return this.roomId;
  }


}
