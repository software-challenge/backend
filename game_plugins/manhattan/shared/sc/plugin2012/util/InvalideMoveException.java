package sc.plugin2012.util;

@SuppressWarnings("serial")
public class InvalideMoveException extends Exception {
	
	public final String reason;
	
	public InvalideMoveException(String reason) {
		this.reason = reason;
	}
	
}
