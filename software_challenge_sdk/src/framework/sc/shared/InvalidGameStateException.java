package sc.shared;

/**
 * This exception is thrown if the server tried to create an invalid game state.
 * This indicates an error in the server or plugin code. It should never be
 * thrown on invalid input to the server (i.e. invalid move made).
 */
public class InvalidGameStateException extends Exception {

  public InvalidGameStateException(String reason) {
    super(reason);
  }

}
