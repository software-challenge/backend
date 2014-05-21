package sc.plugin2015.util;

@SuppressWarnings("serial")
public class InvalidMoveException extends Exception {

	public InvalidMoveException(String reason) {
		super(reason);
	}

}
