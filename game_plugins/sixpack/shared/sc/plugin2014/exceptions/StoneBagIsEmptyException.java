package sc.plugin2014.exceptions;

/**
 * Diese Exception wird geworfen, wenn versucht wird aus einem leeren
 * Spielsteinbeutel Spielsteine zu ziehen.
 * 
 * @author ffi
 * 
 */
public class StoneBagIsEmptyException extends Exception {
	private static final long serialVersionUID = -1095512793431638458L;

	public StoneBagIsEmptyException(String reason) {
		super(reason);
	}

}
