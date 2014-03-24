package sc.plugin2015;


import java.util.LinkedList;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Stellt ein Spielfeld dar, welches aus einem Pinguin{@link penguin} und
 * einer Anzahl von Fischen {@link fish} besteht wobei 0 bedeutet, dass das 
 * Feld nicht mehr vorhanden ist
 * 
 * @author soed
 *
 */
@XStreamAlias(value = "field")
//@XStreamConverter(FieldConverter.class)
public class Field implements Cloneable {

	@XStreamAsAttribute
	public final int fish;


	private Penguin penguin;

	
	public Field() {
		this.fish = 0;
		this.penguin = null;
	}

	/**
	 * Erzeugt ein neues Spielfeld mit übergebenen Fischanzahl und Pinguin
	 * 
	 * @param f
	 * @param p
	 */
	public Field(int f, Penguin p) {
		this.fish = f;
		this.penguin = p;
	}

	/**
	 * Erzeugt ein neues Spielfeld mit übergebener Fischzahl keinem Pinguin
	 * 
	 * @param f
	 */
	public Field(int f) {
		this.fish = f;
		this.penguin = null;
	}

	/**
	 * Setzt einen Pinguin auf dieses Spielfeld
	 * 
	 * @param penguin
	 */
	public void putPenguin(Penguin penguin) {
		this.penguin= penguin; //eventuell überprüfen ob penguin = null
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
	 * falls er nicht existiert wird null zurückgegeben
	 * 
	 * @param color
	 * @return
	 */
	public Penguin removePenguin(PlayerColor color) {
		
		if (this.penguin.getOwner() == color) {
				this.penguin = null;
				return penguin;
		}
		
		return null;
	}

	/**
	 * Falls sich ein Pinguin auf dem Feld befindet true zurückgegeben
	 * ansonsten false
	 * 
	 * 
	 * @return
	 */
	public boolean penguin() {
		if(this.penguin==null)
			return false;
		else 
			return true;
	}
	/**
	 * gibt die Anzahl der Fische auf einem Feld zurück
	 * 
	 * 
	 */
		
	public int getFish()
	{
		return this.fish;
	}
	
	/**
	 * Erzeugt eine deep copy dieses Feldes. Der Pinguin der sich auf diesem
	 * Feld befinden, werden ebenfalls kopiert
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		Field clone = new Field(this.fish, this.penguin);
		
		return clone;
	}
}
