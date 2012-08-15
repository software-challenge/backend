package sc.plugin2013;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Beinhaltet Informationen zum Spielende: Farbe des Gewinners
 * und Gewinngrund.
 * 
 */
@XStreamAlias(value = "condition")
class Condition implements Cloneable {
	
	@XStreamAsAttribute
	public final PlayerColor winner;
	
	@XStreamAsAttribute
	public final String reason;

	/*
	 * XStream benötigt eventuell einen parameterlosen Konstruktor bei der
	 * Deserialisierung von Objekten aus XML-Nachrichten.
	 */
	public Condition() {
		winner = null;
		reason = null;
	}

	/**
	 * erzeugt eine neue Condition mit Sieger und Gewinngrund
	 * 
	 * @param winner
	 *            Farbe des Siegers
	 * @param reason
	 *            TExt, der Sieg beschreibt
	 */
	public Condition(PlayerColor winner, String reason) {
		this.winner = winner;
		this.reason = reason;
	}

	/**
	 * klont dieses Objekt
	 * 
	 * @return ein neues Objekt mit gleichen Eigenschaften
	 * @throws CloneNotSupportedException
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return new Condition(winner, reason);
	}

}
