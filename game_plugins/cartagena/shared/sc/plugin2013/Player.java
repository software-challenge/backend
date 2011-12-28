package sc.plugin2013;

import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import sc.framework.plugins.SimplePlayer;
import sc.plugin2013.PlayerColor;
import sc.plugin2013.util.Constants;

/**Ein Spieler, identifiziert durch seine Spielerfarbe.<br/>
 * Beeinhaltet auch Informationen zum Punktekonto, zu den {@link Card Karten)
 * des Spieler und den Piraten auf dem Feld und im Ziel.
 * 
 * @author felix
 *
 */
@XStreamAlias(value = "cartagena:player")
public class Player extends SimplePlayer implements Cloneable {

	// Farbe des Spieler
	@XStreamOmitField
	private PlayerColor color;

	// Punkte des Spieler
	private int points;

	// Liste der Karten, die sich auf der Hand befinden
	@XStreamImplicit(itemFieldName = "card")
	private final List<Card> cards;

	// Anzahl der Piraten im Ziel
	private int piratesOnBoat;

	// Anzahl der Piraten auf dem Feld/am Start
	private int piratesOnField;

	/**
	 * XStream benötigt eventuell einen parameterlosen Konstruktor bei der
	 * Deserialisierung von Objekten aus XML-Nachrichten.
	 */
	public Player() {
		cards = null;
	}

	/**
	 * Erzeugt einen neuen Spieler
	 * 
	 * @param color
	 *            Die Farbe des Spielers
	 * 
	 */
	public Player(final PlayerColor color) {
		this.color = color;
		this.cards = new LinkedList<Card>();
		this.points = 0;
		this.piratesOnBoat = 0;
		this.piratesOnField = Constants.PIRATES;
		// TODO init variables
	}

	/**
	 * Liefert die Farbe des Spielers
	 * 
	 * @return Die Farbe des Spielers
	 */
	public PlayerColor getPlayerColor() {
		return this.color;
	}

}
