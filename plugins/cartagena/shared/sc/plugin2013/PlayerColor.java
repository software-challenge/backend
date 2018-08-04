package sc.plugin2013;

/**
 * Übernommen aus 2012
 * Ein Spieler spielt entweder in rot (als erster Spieler) oder blau (zweiter
 * Spieler)
 * 
 */
public enum PlayerColor {

	RED, BLUE;

	/**
	 * liefert die Spielerfarbe des Gegners dieses Spielers
	 */
	public PlayerColor opponent() {
		return this == RED ? BLUE : RED;
	}

}
