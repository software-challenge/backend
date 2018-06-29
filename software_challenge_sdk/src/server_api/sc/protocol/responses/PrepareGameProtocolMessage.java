package sc.protocol.responses;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/** Response to PrepareGameRequest */

@XStreamAlias(value = "prepared")
public class PrepareGameProtocolMessage extends ProtocolMessage {
  @XStreamImplicit(itemFieldName = "reservation")
  private List<String> reservations;

  @XStreamAsAttribute
  private String roomId;

  /** might be needed by XStream */
  public PrepareGameProtocolMessage() {
  }

  public PrepareGameProtocolMessage(String roomId, List<String> reservations) {
    this.roomId = roomId;
    this.reservations = reservations;
  }

  public List<String> getReservations() {
    return this.reservations;
  }

  public String getRoomId() {
    return this.roomId;
  }

}
