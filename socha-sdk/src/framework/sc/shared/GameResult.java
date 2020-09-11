package sc.shared;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import sc.framework.plugins.Player;
import sc.protocol.responses.ProtocolMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Das Endergebnis eines Spiels.
 * Mit getScores() können die Punkte abgefragt werden.
 */
@XStreamAlias(value = "result")
public class GameResult implements ProtocolMessage {
  private final ScoreDefinition definition;

  @XStreamImplicit(itemFieldName = "score")
  private final List<PlayerScore> scores;

  @XStreamImplicit(itemFieldName = "winner")
  private List<Player> winners;

  /** might be needed by XStream */
  public GameResult() {
    definition = null;
    scores = null;
    winners = null;
  }

  public GameResult(ScoreDefinition definition, List<PlayerScore> scores, List<Player> winners) {
    this.definition = definition;
    this.scores = scores;
    this.winners = winners;
  }

  public ScoreDefinition getDefinition() {
    return this.definition;
  }

  public List<PlayerScore> getScores() {
    return this.scores;
  }

  public List<Player> getWinners() {
    if (this.winners == null)
      this.winners = new ArrayList<>(2);
    return this.winners;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("Winner: ").append(winners);
    int playerIndex = 0;
    for (PlayerScore score : this.scores) {
      builder.append("\n").append("Player ").append(playerIndex).append(": ");
      String[] scoreParts = score.toStrings();
      for (int i = 0; i < scoreParts.length; i++) {
        builder.append(this.definition.get(i).getName()).append("=").append(scoreParts[i]);
        if (i + 1 < scoreParts.length)
          builder.append("; ");
      }
      playerIndex++;
    }

    return builder.toString();
  }

  public boolean isRegular() {
    for (PlayerScore score : this.scores) {
      if (score.getCause() != ScoreCause.REGULAR) {
        return false;
      }
    }
    return true;
  }

}
