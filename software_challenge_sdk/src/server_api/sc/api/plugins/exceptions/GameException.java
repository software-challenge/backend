package sc.api.plugins.exceptions;

public class GameException extends GameRoomException {
  private static final long serialVersionUID = 7718472584354852005L;

  public GameException(String message) {
    super(message);
  }

}
