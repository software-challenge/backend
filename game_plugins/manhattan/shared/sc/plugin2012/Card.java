package sc.plugin2012;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * wrapper fuer einen integer der eine bauplatzkarte beschreibt
 * 
 * @author tkra
 *
 */

public class Card {

	@XStreamAsAttribute
    public final int slot;
    
	public Card(int slot){
		this.slot = slot;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Card && ((Card) obj).slot == slot;
	}

	@Override
	public int hashCode() {
		return slot;
	}
	
}
