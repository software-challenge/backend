package sc.shared;

import sc.api.plugins.IMove;

public class InvalidMoveException extends Exception {

  public final IMove move;

  public InvalidMoveException(String reason) {
    this(reason, null);
  }

  public InvalidMoveException(String reason, IMove move) {
    /* NOTE: It is important to have information about the move in the reason
     * because that is the only place where the invalid move is logged */
    super(reason + (move == null ? "" : "; move was " + move.toString()));
    this.move = move;
  }

}
