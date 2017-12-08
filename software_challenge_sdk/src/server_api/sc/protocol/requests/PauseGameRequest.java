package sc.protocol.requests;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.protocol.responses.ProtocolMessage;

/**
 * Request by administrative client to pause or unpause a game specified by given roomId.
 * A game will only be paused immediately, if there is no pending MoveRequest. If there is a pending
 * MoveRequest the game will be paused in the next turn.
 * If the game is paused no GameState or MoveRequest will be send to the players (and all other observers).
 * These are only send after a an AdminClient sends a StepRequest or resumes the game.
 */

@XStreamAlias("pause")
public class PauseGameRequest extends ProtocolMessage implements ILobbyRequest {
  @XStreamAsAttribute
  public String roomId;

  @XStreamAsAttribute
  public boolean pause;

  public PauseGameRequest(String roomId, boolean pause) {
    this.roomId = roomId;
    this.pause = pause;
  }
}
