package sc.plugin2014.exceptions;

public class StoneBagIsEmptyException extends Exception {
    private static final long serialVersionUID = -1095512793431638458L;

    public StoneBagIsEmptyException(String reason) {
        super(reason);
    }

}
