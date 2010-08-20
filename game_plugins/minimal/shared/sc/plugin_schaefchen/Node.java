package sc.plugin_schaefchen;

import java.util.HashSet;
import java.util.Set;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * ein spielfeld. als geometrische figur und als logisches element
 * 
 * @author tkra
 * 
 */

@XStreamAlias(value = "sit:node")
public final class Node {


	// eikndeutige nummer dieses spielfeldes
	@XStreamAsAttribute
	public final int index;

	// typ dieses spielfeldes
	@XStreamAsAttribute
	private NodeType type;
	
	// menge der benachbarten spielfelder
	@XStreamImplicit(itemFieldName="neighbour")
	private final Set<Integer> neighbours;

	// ggf. vorghandeses gegenstueck zu diesem feld
	// wird bei den heimatfeldern benoetigt um einem schaf sein ziel mitzuteilen
	@XStreamOmitField
	private int counterPart;

	public Node(final NodeType type, int counterPart, int index) {
		this.index = index;
		this.counterPart = counterPart;
		
		neighbours = new HashSet<Integer>();
		this.type = type;

	}



	/**
	 * fuegt diesem spielfeld einen nachbar hinzu
	 */
	protected void addNeighbour(final int other) {
		neighbours.add(other);
	}

	/**
	 * liefert die menge der nachbarn dieses spielfeldes
	 */
	public Set<Integer> getNeighbours() {
		return neighbours;
	}


	/**
	 * liefert den spielfeldtyp dieses spielfeldes
	 */
	public NodeType getNodeType() {
		return type;
	}



	protected int getCounterPart() {
		return counterPart;
	}

}