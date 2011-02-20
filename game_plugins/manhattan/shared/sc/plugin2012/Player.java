package sc.plugin2012;

import java.util.LinkedList;
import java.util.List;

import sc.framework.plugins.SimplePlayer;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * ein spieler. er wird durch seine spielerfarbe identifiziert und weiss wie
 * viele schafe seine schafe gestohlen haben und wie viele blumen seine schafe
 * gefressen haben.
 * 
 * @author sca, tkra
 * 
 */
@XStreamAlias(value = "manhattan:player")
public final class Player extends SimplePlayer {

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
	 * einen neuen spieler erstellen und ihm eine spielerfarbe zuweisen
	 * 
	 * @param color
	 *            seine spielerfarbe
	 */
	public Player(final PlayerColor color) {
		cards = new LinkedList<Card>();
		segments = new LinkedList<Segment>();
		this.color = color;
		points = 0;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Player) && ((Player) obj).color == this.color;
	}

	/**
	 * liefert die spielerfarbe dieses spielers
	 */
	public PlayerColor getPlayerColor() {
		return color;
	}

	/**
	 * fuegt diesem spieler eine spielkarte hinzu
	 */
	public void addCard(Card card) {
		cards.add(card);
	}

	/**
	 * entfernt eine karte von diesem spieler
	 */
	public void removeCard(int slot) {
		Card cardToRemove = null;
		for(Card card : cards){
			if(card.slot == slot){
				cardToRemove = card;
				break;
			}
		}
		cards.remove(cardToRemove);
	}
	
	public boolean hasCard(int slot){
		for(Card card: cards){
			if(card.slot == slot){
				return true;
			}
		}
		return false;
	}

	/**
	 * liefert eine kopie der liste der karten des spielers
	 */
	public List<Card> getCards() {
		return new LinkedList<Card>(cards);
	}

	/**
	 * fuegt neue segmentinformation hinzu
	 */
	public void addSegmet(Segment segment) {
		segments.add(segment);
	}

	public List<Segment> getSegments() {
		return segments;
	}

	/**
	 * liefert die segmentinformatieon einer bestimmten groesse deises spielers
	 * oder null, falls keine segmentinformationd ieser groesse existiert.
	 */
	public Segment getSegment(int size) {
		return segments.get(size - 1);
	} 

	/**
	 * fuegt diesem spieler punkte hinzu
	 */
	public void addPoints(int points) {
		this.points += points;
	}

	/**
	 * liefert den punktestand dieses spielers
	 */
	public int getPoints() {
		return points;
	}

	/**
	 * liefert die anzahl der von diesem spieler momentan noch buabaren segmente
	 */
	public int getSegmentCount() {
		int segmentCount = 0;
		for (Segment segment : segments) {
			segmentCount += segment.getUsable();
		}
		return segmentCount;
	}

}
