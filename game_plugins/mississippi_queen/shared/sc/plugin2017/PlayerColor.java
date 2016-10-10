package sc.plugin2017;

/**
 * Ein Spieler spielt entweder in rot (als erster Spieler) oder blau (zweiter
 * Spieler)
 * 
 */
public enum PlayerColor {

	RED, BLUE;

	/**
	 * liefert die Spielerfarbe des Gegners dieses Spielers
	 * @return Spielerfarbe des Gegners
	 */
	public PlayerColor opponent() {
		return this == RED ? BLUE : RED;
	}
	
	/*@Override
	public String toString() {
		return this == RED ? "RED" : "BLUE";
	}*/

}
