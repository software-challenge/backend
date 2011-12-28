package sc.plugin2013;

/**
 * @author felix
 * 
 */

public class Field implements Cloneable {

	public final FieldType type;
	public final SymbolType symbol;

	/**
	 * XStream benötigt eventuell einen parameterlosen Konstruktor bei der
	 * Deserialisierung von Objekten aus XML-Nachrichten.
	 */
	public Field() {
		this.type = null;
		this.symbol = null;
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
	}
}
