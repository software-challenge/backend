package sc.plugin2014.util;

@SuppressWarnings("serial")
public class InvalideMoveException extends Exception {

    public InvalideMoveException(String reason) {
        super(reason);
    }

}
