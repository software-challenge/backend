package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.protocol.responses.ProtocolMessage;

/**
 * Send by client to join game by gameType. Creates a new gameRoom if no open gameRoom of
 * specified gameType exists.
 */

@XStreamAlias("join")
public class JoinRoomRequest extends ProtocolMessage implements ILobbyRequest {
  @XStreamAsAttribute
  private String gameType = null;

  /**
   * Needed by JoinPreparedGameRequest
   */
  protected JoinRoomRequest() {
    // nothing to do
  }

  public JoinRoomRequest(String gameType) {
    this.gameType = gameType;
  }

  public String getGameType() {
    return this.gameType;
  }
}
