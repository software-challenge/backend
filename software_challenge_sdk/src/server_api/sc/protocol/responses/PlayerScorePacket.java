package sc.protocol.responses;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import sc.protocol.requests.ILobbyRequest;
import sc.shared.Score;

/**
 * Response to GetScoreForPlayerRequest.
 */

@XStreamAlias("playerScore")
public class PlayerScorePacket extends ProtocolMessage implements ILobbyRequest {

  private Score score;

  /**
   *
   * @param score Construct a packet containing the score for one player
   */
  public PlayerScorePacket(Score score) {
    this.score = score;
  }

  /**
   * Get the current score
   * @return Object representing the score
   */
  public Score getScore() {
    return score;
  }

}
