package sc.plugin2014.exceptions;

/**
 * Diese Exception wird geworfen, wenn ein invalider Zug ausgeführt wurde.
 * @author ffi
 *
 */
public class InvalidMoveException extends Exception {
    private static final long serialVersionUID = -1095512793431638458L;

    public InvalidMoveException(String reason) {
        super(reason);
    }

}
