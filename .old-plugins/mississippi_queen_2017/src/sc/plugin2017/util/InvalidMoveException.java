package sc.plugin2017.util;

@SuppressWarnings("serial")
public class InvalidMoveException extends Exception {

	public InvalidMoveException(String reason) {
		super(reason);
	}

}
