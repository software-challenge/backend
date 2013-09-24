package sc.plugin2014.entities;

import java.awt.Point;

import sc.plugin2014.moves.Move;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Stellt ein Spieldfeld dar. Besitzt eine x- und y-Position, sowie eventuell
 * einen Stein.
 * 
 * @author ffi
 * 
 */
@XStreamAlias(value = "field")
public class Field implements Cloneable {

	@XStreamAsAttribute
	private int posX;

	@XStreamAsAttribute
	private int posY;

	@XStreamAsAttribute
	private Stone stone;

	/**
	 * Erzeugt ein neues Feld.
	 */
	public Field() {
	}

	/**
	 * Erzeugt ein neues Feld mit übergebener x-,y-Position
	 * 
	 * @param posX
	 * @param posY
	 */
	public Field(int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
	}

	/**
	 * Liefert die x-Position zurück
	 * 
	 * @return x-Position
	 */
	public int getPosX() {
		return posX;
	}

	/**
	 * Liefert die y-Position zurück
	 * 
	 * @return y-Position
	 */
	public int getPosY() {
		return posY;
	}

	/**
	 * Liefert die Position als {@link Point} zurück
	 * 
	 * @return Position als Point
	 */
	public Point getPosAsPoint() {
		return new Point(posX, posY);
	}

	/**
	 * Liefert den Spielstein auf diesem Feld zurück
	 * 
	 * @return Der Spielstein. null wenn das Spielfeld leer ist.
	 */
	public Stone getStone() {
		return stone;
	}

	/**
	 * Setzt einen Spielstein auf das Feld. <b> Achtung! Keine Überprüfung auf
	 * korrektheit des Zuges. Dafür siehe
	 * {@link Move#perform(sc.plugin2014.GameState, Player)}.</b>
	 * 
	 * @param stone
	 */
	public void setStone(Stone stone) {
		this.stone = stone;
	}

	/**
	 * Überprüft ob das Spielfeld frei ist
	 * 
	 * @return true, wenn kein Spielstein auf diesem Feld liegt.
	 */
	public boolean isFree() {
		return getStone() == null;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Field) {
			Field otherField = (Field) o;
			return (otherField.getPosX() == getPosX())
					&& (otherField.getPosY() == getPosY());
		}
		return false;
	}

	@Override
	public String toString() {
		return "Field: (" + getPosX() + ", " + getPosY() + ")";
	}

	/** Klont dieses Objekt. (deep-copy)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		Field clone = new Field(this.posX, this.posY);
		if (stone != null) {
			clone.setStone((Stone) stone.clone());
		}
		return clone;
	}
}
