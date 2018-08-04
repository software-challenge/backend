package sc.shared;

import sc.api.plugins.IMove;

public class InvalidMoveException extends Exception {

  public final IMove move;

  public InvalidMoveException(String reason) {
    this(reason, null);
  }

  public InvalidMoveException(String reason, IMove move) {
    super(reason);
    this.move = move;
  }

}
