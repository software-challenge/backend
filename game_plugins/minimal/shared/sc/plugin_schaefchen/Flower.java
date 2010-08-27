package sc.plugin_schaefchen;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * blumen die auf spielfeldern stehen. sie kennen den index des spielfeldes auf
 * dem sie stehen und die anzahl der blueten.
 * 
 * @author tkra
 * 
 */
public final class Flower implements Cloneable {

	/**
	 * index des spielfeldes, auf dem diese bluemen stehen
	 */
	@XStreamAsAttribute
	public final int node;
	
	
	/** 
	 * anzahl der blueten. negative zahlen stehen fuer fliegenpilze
	 */
	@XStreamAsAttribute
	public final int amount;

	/**
	 * eine neue blume auf einemspielfeld erstellen
	 * @param index index des spielfeldes
	 * @param amount anzahl der blueten
	 */
	public Flower(int index, int amount) {
		this.node = index;
		this.amount = amount;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new Flower(node, amount);
	}

}
