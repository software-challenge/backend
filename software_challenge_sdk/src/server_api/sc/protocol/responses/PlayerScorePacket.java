package sc.protocol.responses;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import sc.protocol.requests.ILobbyRequest;
import sc.shared.Score;

@XStreamAlias("playerScore")
public class PlayerScorePacket extends ProtocolMessage implements ILobbyRequest {

  private Score score;

  public PlayerScorePacket(Score score) {
    this.score = score;
  }

  public Score getScore() {
    return score;
  }
}
