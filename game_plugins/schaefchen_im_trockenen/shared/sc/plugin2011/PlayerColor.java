package sc.plugin2011;

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
		PlayerColor result = null;
		switch (this) {
		case RED:
			result = PlayerColor.BLUE;
			break;

		case BLUE:
			result = PlayerColor.RED;
			break;
		}

		return result;
	}

}
