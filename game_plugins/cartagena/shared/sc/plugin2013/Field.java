package sc.plugin2013;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author felix
 * 
 */

public class Field implements Cloneable {

	public final FieldType type;
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
	
	
	public Field(SymbolType s) {
		this.type = FieldType.SYMBOL;
		this.symbol = s;
		this.pirates = new ArrayList<Pirate>(3);
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
		this.pirates = new ArrayList<Pirate>(3);
	}
	
	public Field(FieldType type){
		assert type == FieldType.FINISH || type == FieldType.START;
		this.type = type;
		this.symbol = null;
		this.pirates = new LinkedList<Pirate>();
		
	}
	
	


	/**
	 * Setzt einen Piraten auf dieses Spielfeld
	 * @param pirate
	 */
	public void putPirate(Pirate pirate){
		this.pirates.add(pirate);
	}
	
	/**
	 * @return Liefert die Liste der Piraten, welche sich auf diesem Feld befinden.
	 */
	public List<Pirate> getPirates(){
		return this.pirates;
	}
	
	public Pirate removePirate(PlayerColor color){
		for(Pirate p: pirates){
			if( p.getOwner() == color){
				pirates.remove(p);
				return p;
			}
		}
		return null;
	}
}
