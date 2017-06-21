package sc.shared;

@SuppressWarnings("serial")
public class InvalidMoveException extends Exception {

	public InvalidMoveException(String reason) {
		super(reason);
	}

}
