package sc.plugin_minimal;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * ein spielfeld. als geometrische figur und als logisches element
 * 
 * @author tkra
 * 
 */

@XStreamAlias(value = "minimal:node")
public final class Node {
	
	
	// menge der benachbarten spielfelder
	private final Set<Integer> neighbours;

	// ggf. vorghandeses gegenstueck zu diesem feld
	// wird bei den heimatfeldern benoetigt um einem schaf sein ziel mitzuteilen
	@XStreamOmitField
	private int counterPart;

	// eikndeutige nummer dieses spielfeldes
	public final int index;

	// typ dieses spielfeldes
	private NodeType type;

	// anzahl an blumen die auf diesem spielfeld sind
	private int flowers;

	public Node(final NodeType type, int counterPart, int index) {
		this.index = index;
		this.counterPart = counterPart;
		
		neighbours = new HashSet<Integer>();
		this.type = type;

	}



	/**
	 * fuegt diesem spielfeld einen nachbar hinzu
	 */
	public void addNeighbour(final int other) {
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

	/**
	 * fuegt diesem spielfeld blumen hinzu
	 */
	public void addFlowers(int flowers) {
		this.flowers += flowers;
	}

	/**
	 * liefert die anzahl der blumen auf diesem spielfeld
	 */
	public int getFlowers() {
		return flowers;
	}

	/**
	 * liefert das ggf. vorhandene gegenstueck zu diesem spielfeld
	 */
	public int getCounterPart() {
		return counterPart;
	}


}