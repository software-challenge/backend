package sc.shared;

import sc.protocol.responses.ProtocolMove;

public class InvalidMoveException extends Exception {

	private ProtocolMove move;

  public InvalidMoveException(String reason) {
    super(reason);
  }

  public InvalidMoveException(String reason, ProtocolMove move) {
    super(reason);
    this.move = move;
  }

  public ProtocolMove getMove() {
    return move;
  }
}
