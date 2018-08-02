package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Used by client to join a room by reservation code. The code can be received from
 * the administrative client who requested game creation via PrepareGameRequest.
 */

@XStreamAlias("joinPrepared")
public class JoinPreparedRoomRequest extends JoinRoomRequest {
  @XStreamAsAttribute
  private String reservationCode;

  public JoinPreparedRoomRequest(String reservationCode) {
    this.reservationCode = reservationCode;
  }

  public String getReservationCode() {
    return this.reservationCode;
  }

}
