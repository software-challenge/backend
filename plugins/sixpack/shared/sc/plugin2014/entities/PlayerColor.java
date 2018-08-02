package sc.plugin2014.entities;

/**
 * Enum der Spielerfarben. RED und BLUE
 * 
 * @author ffi
 * 
 */
public enum PlayerColor {

	RED, BLUE;

	/**
	 * Liefert die Farbe des Gegners.
	 * 
	 * @return Farbe des Gegners.
	 */
	public PlayerColor getOpponent() {
		return this == RED ? BLUE : RED;
	}
}
