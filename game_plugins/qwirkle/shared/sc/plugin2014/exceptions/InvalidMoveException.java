package sc.plugin2014.exceptions;

@SuppressWarnings("serial")
public class InvalidMoveException extends Exception {

    public InvalidMoveException(String reason) {
        super(reason);
    }

}
