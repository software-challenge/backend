package sc.plugin2015;

import java.util.LinkedList;

import sc.plugin2015.util.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Stellt ein Spielfeld dar, welches aus einem Pinguin{@link penguin} und einer
 * Anzahl von Fischen{@link fish} besteht, wobei 0 bedeutet, dass das Feld nicht
 * mehr vorhanden ist.
 * 
 * @author soed
 * 
 */
@XStreamAlias(value = "field")
// @XStreamConverter(FieldConverter.class)
public class Field implements Cloneable {

	@XStreamAsAttribute
	public int fish;

	@XStreamAsAttribute
	private Penguin penguin;

	/**
	 * Erzeugt ein neues leeres Spielfeldplättchen, es enthält keine Fische oder
	 * Pinguine und zählt deshalb als nicht vorhanden.
	 */
	public Field() {
		this.fish = 0;
		this.penguin = null;
	}

	/**
	 * Erzeugt ein neues Spielfeldplättchen mit übergebenen Fischanzahl und
	 * Pinguin.
	 * 
	 * @param f
	 *            Anzahl der Fische
	 * @param p
	 *            Pinguin
	 */
	public Field(int f, Penguin p) {
		this.fish = f;
		this.penguin = p;
	}

	/**
	 * Erzeugt ein neues Spielfeldplättchen mit übergebener Fischzahl und keinem
	 * Pinguin.
	 * 
	 * @param f
	 *            Anzahl der Fische
	 */
	public Field(int f) {
		this.fish = f;
		this.penguin = null;
	}

	/**
	 * Setzt einen Pinguin auf dieses Spielfeld.
	 * 
	 * @param penguin
	 *            zu setzender Pinguin
	 */
	public void putPenguin(Penguin penguin) throws IllegalArgumentException {
		if (getFish() == 0 || getPenguin() != null)
			throw new IllegalArgumentException();
		this.penguin = penguin;
	}

	/**
	 * @return penguin liefert den Pinguin eines Feldes
	 * 
	 */
	public Penguin getPenguin() {
		return this.penguin;
	}

	/**
	 * Enfernt einen Pinguin der Spielerfarbe vom Feld und gibt diesen zurück
	 * falls er nicht existiert wird null zurückgegeben.
	 * 
	 * @param color
	 *            Spielerfarbe des Besitzers
	 * @return Entfernter Pinguin
	 */
	public Penguin removePenguin(PlayerColor color) {

		if (this.penguin.getOwner() == color) {
			Penguin p = this.penguin;
			this.penguin = null;
			return p;
		}

		return null;
	}

	/**
	 * Besagt, ob sich auf dem Feld ein Pinguin befindet.
	 * 
	 * 
	 * @return Liefert zurück, ob ein Pinguin auf dem Feld steht.
	 */
	public boolean hasPenguin() {
		if (this.penguin == null)
			return false;
		else
			return true;
	}

	/**
	 * Gibt die Anzahl der Fische auf einem Feld zurück.
	 * 
	 * @return Anzahl der Fische
	 */
	public int getFish() {
		return this.fish;
	}

	/**
	 * Setzt eine Fischanzahl auf das Feld
	 * 
	 * @param count
	 *            Anzahl
	 */
	public void setFish(int count) {
		this.fish = count;
	}

	/**
	 * Erzeugt eine deep copy dieses Feldes. Der Pinguin der sich auf diesem
	 * Feld befinden, wird ebenfalls kopiert.
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		Field clone = new Field(this.fish, this.penguin);
		return clone;
	}
}
