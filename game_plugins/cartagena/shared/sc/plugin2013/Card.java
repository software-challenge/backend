package sc.plugin2013;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Klasse, welche eine Karte darstellt. Hat ein Attribut {@code symbol} vom Typ
 * {@link SymbolType}.
 * 
 * @author fdu
 * 
 */
@XStreamAlias(value = "cartagena:card")
public class Card implements Cloneable {
	@XStreamAsAttribute
	public final SymbolType symbol;

	/*
	 * XStream benötigt eventuell einen parameterlosen Konstruktor bei der
	 * Deserialisierung von Objekten aus XML-Nachrichten.
	 */
	public Card() {
		this.symbol = null;
	}

	/**
	 * Erzeugt eine Karte fuer ein Symbol
	 * 
	 * @param s
	 *            Symbol der Karte
	 */

	public Card(SymbolType s) {
		this.symbol = s;
	}

	/**
	 * klont dieses Objekt
	 * 
	 * @return ein neues Objekt mit gleichen Eigenschaften
	 * @throws CloneNotSupportedException
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		Card clone = new Card(this.symbol);
		return clone;
	}

	/**
	 * Prueft, ob die Karte mit der uebergebenen im Symbol übereinstimmt
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Card && ((Card) obj).symbol == symbol;
	}

}
