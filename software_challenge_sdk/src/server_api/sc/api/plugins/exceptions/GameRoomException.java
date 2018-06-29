package sc.api.plugins.exceptions;

public class GameRoomException extends RescuableClientException {
  private static final long serialVersionUID = -2344097964145074632L;

  public GameRoomException(String message) {
    super(message);
  }

}
