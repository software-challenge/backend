package sc.plugin2014.exceptions;

public class InvalidMoveException extends Exception {
    private static final long serialVersionUID = -1095512793431638458L;

    public InvalidMoveException(String reason) {
        super(reason);
    }

}
