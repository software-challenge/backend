package sc.plugin2016;

/**
 * Beinhaltet Informationen zum Spielende: Farbe des Gewinners und Gewinngrund.
 * 
 */
class Condition implements Cloneable {

	/**
	 * Farbe des Gewinners
	 */
	public final PlayerColor winner;

	/**
	 * Sieggrund
	 */
	public final String reason;

	/**
	 * XStream benötigt eventuell einen parameterlosen Konstruktor bei der
	 * Deserialisierung von Objekten aus XML-Nachrichten.
	 */
	public Condition() {
		winner = null;
		reason = null;
	}

	/**
	 * Erzeugt eine neue Condition mit Sieger und Geiwnngrund
	 * 
	 * @param winner
	 *            Farbe des Siegers
	 * @param reason
	 *            Text, der Sieg beschreibt
	 */
	public Condition(PlayerColor winner, String reason) {
		this.winner = winner;
		this.reason = reason;
	}

	/**
	 * klont dieses Objekt
	 * 
	 * @return ein neues Objekt mit gleichen Eigenschaften
	 * @throws CloneNotSupportedException falls Objekt nicht geklont werden kann
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return new Condition(winner, reason);
	}

}
