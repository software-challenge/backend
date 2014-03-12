package shared.sc.plugin2015;

import java.util.LinkedList;
import java.util.List;

import sc.framework.plugins.SimplePlayer;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * Ein Spieler, identifiziert durch seine Spielerfarbe.<br/>
 * Beeinhaltet auch Informationen zum Punktekonto, zu den 
 * {@link Segment Bausteinen} und zu den {@link Card Spielkarten} des Spielers.
 * 
 */
@XStreamAlias(value = "manhattan:player")
public class Player extends SimplePlayer implements Cloneable {

	// spielerfarbe des spielers
	@XStreamOmitField
	private PlayerColor color;

	// aktuelle punktzahl des spielers
	@XStreamAsAttribute
	private int points;

	// liste der verwendbaren segmente
	@XStreamImplicit(itemFieldName = "segment")
	private final List<Segment> segments;

	// liste der karten des spielers
	@XStreamImplicit(itemFieldName = "card")
	private final List<Card> cards;

         /**
         * XStream benötigt eventuell einen parameterlosen Konstruktor
         * bei der Deserialisierung von Objekten aus XML-Nachrichten.
         */
        public Player() {
            segments = null;
            cards = null;
        }
        /**
	 * einen neuen Spieler erstellen und ihm eine Spielerfarbe zuweisen
	 * 
	 * @param color
	 *            seine Spielerfarbe
	 */
	public Player(final PlayerColor color) {
		cards = new LinkedList<Card>();
		segments = new LinkedList<Segment>();
		this.color = color;
		points = 0;
	}

	 /**
         * klont dieses Objekt
         * @return ein neues Objekt mit gleichen Eigenschaften
         * @throws CloneNotSupportedException 
         */
        @Override
        public Object clone() throws CloneNotSupportedException {
            Player clone = new Player(this.color);
            clone.points = this.points;
            if (segments != null)
                for (Segment s : this.segments)
                    clone.addSegmet((Segment)s.clone());
            if (cards != null)
                for (Card c : this.cards)
                    clone.addCard((Card)c.clone());
            return clone;
        }

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Player) && ((Player) obj).color == this.color;
	}

	/**
	 * liefert die Spielerfarbe dieses Spielers
	 */
	public PlayerColor getPlayerColor() {
		return color;
	}

	/**
	 * fuegt diesem Spieler eine Spielkarte hinzu
	 */
	public void addCard(Card card) {
		cards.add(card);
	}

	/**
	 * entfernt eine Kkarte von diesem Spieler
	 */
	public void removeCard(int slot) {
		Card cardToRemove = null;
		for (Card card : cards) {
			if (card.slot == slot) {
				cardToRemove = card;
				break;
			}
		}
		cards.remove(cardToRemove);
	}
	/**
	 * Prüft, ob der Spieler eine Karte für eine Position hat
	 * @param slot fragliche Position
	 * @return wahr, wenn Karte vorhanden
	 */
	public boolean hasCard(int slot) {
		for (Card card : cards) {
			if (card.slot == slot) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @return eine Liste der Karten des Spielers
	 */
	public List<Card> getCards() {
		return cards;
	}

	/**
	 * fuegt eine Bausteininformation hinzu
	 * @param segment Bausteininformation
	 */
	public void addSegmet(Segment segment) {
		segments.add(segment);
	}
	/**
	 * Gibt eine Liste der Bausteininformationen des Spielers
	 * @return Liste der Bausteininformationen des Spielers
	 */
	public List<Segment> getSegments() {
		return segments;
	}

	/**
	 * Gibt {@link Segment Bausteininformation} zu Größe size
	 * @param size Größe des Bausteins
	 * @return Bausteininformation
	 */
	public Segment getSegment(int size) {
		return segments.get(size - 1);
	}

	/**
	 * Fügt dem Punktekonto des Spielers Punkte hinzu
	 * @param points Anzahl hinzuzufügender Punkte
	 */
	public void addPoints(int points) {
		this.points += points;
	}

	/**
	 * Liefert den Stand des Punktekontos des Spielers
	 * @return Punkte des Spielers
	 */
	public int getPoints() {
		return points;
	}

	/**
	 * Liefert die Anzahl in diesem Abschnitt benutzbarer Bauteile
	 * @return Anzahl in diesem Abschnitt benutzbarer Bauteile
	 */
	public int getUsableSegmentCount() {
		int segmentCount = 0;
		for (Segment segment : segments) {
			segmentCount += segment.getUsable();
		}
		return segmentCount;
	}

	/**
	 * Liefert die Anzahl für spaetere Abschnitte zurueckgelegter Bausteine
	 * @return Anzahl für spaetere Abschnitte zurueckgelegter Bausteine
	 */
	public int getRetainedSegmentCount() {
		int segmentCount = 0;
		for (Segment segment : segments) {
			segmentCount += segment.getRetained();
		}
		return segmentCount;
	}

	/**
	 * Gibt die Groesse des groessten jetzt oder in spaeteren Abschnitten
	 * verfuegbaren Bauteils des Spielers
	 * @return Groesse des groessten Bauteils
	 */
	public int getHighestSegment() {
		int highestSegment = 0;
		for (Segment segment : segments) {
			if (segment.getRetained() + segment.getUsable() > 0 && segment.size > highestSegment) {
				highestSegment = segment.size;
			}
		}
		return highestSegment;
	}

	/**
	 * Gibt die Groesse des groessten in diesem Abschnitt
	 * verfuegbaren Bauteils des Spielers
	 * @return Groesse des groessten aktuellen Bauteils
	 */
	public int getHighestCurrentSegment() {
		int highestSegment = 0;
		for (Segment segment : segments) {
			if (segment.getUsable() > 0 && segment.size > highestSegment) {
				highestSegment = segment.size;
			}
		}
		return highestSegment;
	}

}
