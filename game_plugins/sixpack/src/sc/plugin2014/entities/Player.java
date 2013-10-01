package sc.plugin2014.entities;

import java.util.ArrayList;
import java.util.List;

import sc.framework.plugins.SimplePlayer;
import sc.plugin2014.moves.Move;

import com.thoughtworks.xstream.annotations.*;

/**
 * Stellt einen Spieler dar.
 * 
 * @author ffi
 * 
 */
@XStreamAlias(value = "player")
public class Player extends SimplePlayer implements Cloneable {

	@XStreamOmitField
	private PlayerColor color;

	@XStreamAsAttribute
	private int points;

	@XStreamImplicit(itemFieldName = "stone")
	private final List<Stone> stones;

	/**
	 * Erzeugt ein neues Spielerobjekt. stones wird auf null gesetzt.
	 */
	public Player() {
		stones = null;
	}

	/**
	 * Erzeugt ein neues Spielerobjekt mit übergebener Farbe.
	 * 
	 * @param color
	 *            Die Farbe, welche das Spielerobjekt erhalten soll.
	 */
	public Player(final PlayerColor color) {
		stones = new ArrayList<Stone>();
		this.color = color;
		points = 0;
	}

	/**
	 * Liefert die Farbe dieses Spielers zurück
	 * 
	 * @return Spielerfarbe
	 */
	public PlayerColor getPlayerColor() {
		return color;
	}

	/**
	 * Liefert eine Liste mit den Spielsteinen dieses Spielers zurück
	 * 
	 * @return Liste mit Spielsteinen des Spielers
	 */
	public List<Stone> getStones() {
		return stones;
	}

	/**
	 * Liefert die Punkte dieses Spielers zurück
	 * 
	 * @return Die Punkte des Spielers
	 */
	public int getPoints() {
		return points;
	}

	/**
	 * Fügt einen Spielstein zum Vorrat des Spielers hinzu. <b> Achtung! Keine
	 * Überprüfung auf korrektheit des Zuges. Dafür siehe
	 * {@link Move#perform(sc.plugin2014.GameState, Player)}.</b>
	 * 
	 * @param stone
	 */
	public void addStone(Stone stone) {
		stones.add(stone);
	}

	/**
	 * Fügt einen Spielstein zum Vorrat des Spielers hinzu. <b> Achtung! Keine
	 * Überprüfung auf korrektheit des Zuges. Dafür siehe
	 * {@link Move#perform(sc.plugin2014.GameState, Player)}.</b>
	 * 
	 * @param stone
	 * @param position
	 */
	public void addStone(Stone stone, int position) {
		if (position > stones.size()) {
			stones.add(stone);
		} else {
			stones.set(position, stone);
		}
	}

	/**
	 * Entfernt einen Spielstein aus dem Vorrat des Spielers. <b> Achtung! Keine
	 * Überprüfung auf korrektheit des Zuges. Dafür siehe
	 * {@link Move#perform(sc.plugin2014.GameState, Player)}.</b>
	 * 
	 * @param stone
	 */
	public void removeStone(Stone stone) {
		int position = getStonePosition(stone);
		stones.set(position, null);
	}

	/**
	 * Liefert die Position eines Spielsteines in der Liste des Spielers.
	 * 
	 * @param stone
	 * @return -1 wenn Spieler Stein nicht besitz, sonst Position
	 */
	public int getStonePosition(Stone stone) {
		for (int i = 0; i < stones.size(); i++) {
			Stone s = stones.get(i);
			if (stone.equals(s)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Überprüft ob ein Spielstein im Vorrat des Spielers vorhanden ist
	 * 
	 * @param stone
	 * @return true, wenn der Spielstein sich im Vorrat des Spielers befindet.
	 */
	public boolean hasStone(Stone stone) {
		for (Stone s : stones) {
			if (stone.equals(s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Fügt Punkte zum Konto des Spielers hinzu
	 * 
	 * @param points Punkte, welche hinzuaddiert werden sollen.
	 */
	public void addPoints(int points) {
		this.points += points;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Player) && (((Player) obj).color == color);
	}

	/** Klont dieses Objekt. (deep-copy)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		Player clone = new Player(color);
		clone.points = points;
		if (stones != null) {
			for (Stone s : stones) {
				clone.addStone((Stone) s.clone());
			}
		}
		return clone;
	}
}
