package sc.plugin2012;

/**
 * eine spieler spielt entweder in rot (der erste spieler) oder blau (der zweite
 * spieler)
 * 
 * @author tkra
 * 
 */
public enum PlayerColor {

	RED, BLUE;

	/**
	 * liefert die spielerfarbe des gegners dieses spielers
	 */
	public PlayerColor opponent() {
		return this == RED ? BLUE : RED;
	}

}
