package sc.plugin2012.util;

@SuppressWarnings("serial")
public class InvalideMoveException extends Exception {

	public InvalideMoveException(String reason) {
		super(reason);
	}

}
