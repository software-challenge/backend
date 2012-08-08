package sc.plugin2013;

import java.util.LinkedList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Stellt ein Spielfeld dar. Bestitzt Informationen darüber ob Start-, Ziel-
 * oder normales Feld {@link FieldType}. Falls es sich um eine normales Feld
 * handelt ist außerdem gespeichert welches Symbol ({@link SymbolType}) dieses
 * trägt. {@link pirates} ist eine Liste von Piraten, welche sich auf diesem
 * Feld befinden.
 * 
 * @author fdu
 * 
 */
@XStreamAlias(value = "cartagena:field")
public class Field implements Cloneable {

	@XStreamAsAttribute
	public final FieldType type;
	@XStreamAsAttribute
	public final SymbolType symbol;

	private List<Pirate> pirates;

	/**
	 * XStream benötigt eventuell einen parameterlosen Konstruktor bei der
	 * Deserialisierung von Objekten aus XML-Nachrichten.
	 */
	public Field() {
		this.type = null;
		this.symbol = null;
		this.pirates = null;
	}

	/**
	 * Erzeugt ein neues Spielfeld mit dem übergebenen Symbol.
	 * 
	 * @param s
	 */
	public Field(SymbolType s) {
		this.type = FieldType.SYMBOL;
		this.symbol = s;
		this.pirates = new LinkedList<Pirate>();
	}

	/**
	 * Erzeugt ein neues Spielfeld
	 * 
	 * @param type
	 *            Gibt an welchen Typs das Spielfeld ist
	 * @param symbol
	 *            Gibt an welches Symbol das Spielfeld trägt
	 */
	public Field(FieldType type, SymbolType symbol) {
		this.type = type;
		this.symbol = symbol;
		this.pirates = new LinkedList<Pirate>();
	}

	/**
	 * Erzeugt eine neues Start oder Zielfeld
	 * 
	 * @param type
	 */
	public Field(FieldType type) {
		assert type == FieldType.FINISH || type == FieldType.START;
		this.type = type;
		this.symbol = null;
		this.pirates = new LinkedList<Pirate>();
	}

	/**
	 * Setzt einen Piraten auf dieses Spielfeld
	 * 
	 * @param pirate
	 */
	public void putPirate(Pirate pirate) {
		this.pirates.add(pirate);
	}

	/**
	 * @return Liefert die Liste der Piraten, welche sich auf diesem Feld
	 *         befinden.
	 */
	public List<Pirate> getPirates() {
		return this.pirates;
	}

	/**
	 * Enfernt einen Piraten der Spielerfarbe vom Feld und gibt diesen zurück
	 * 
	 * @param color
	 * @return
	 */
	public Pirate removePirate(PlayerColor color) {
		for (Pirate p : pirates) {
			if (p.getOwner() == color) {
				pirates.remove(p);
				return p;
			}
		}
		return null;
	}

	/**
	 * Gibt die Anzahl der Piraten auf diesem Feld zurück
	 * 
	 * @param color
	 *            Die Farbe des Spielers
	 * @return
	 */
	public int numPirates(PlayerColor color) {
		int num = 0;
		for (Pirate p : pirates) {
			if (p.getOwner() == color) {
				num++;
			}
		}
		return num;
	}

	/**
	 * Erzeugt eine deep copy dieses Feldes. Die Piraten welche sich auf diesem
	 * Feld befinden, werden ebenfalls kopiert
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		Field clone = new Field(this.type, this.symbol);
		if (pirates != null) {
			for (Pirate p : pirates) {
				clone.pirates.add((Pirate) p.clone());
			}
		}
		return clone;
	}
}
