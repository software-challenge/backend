package sc.plugin_schaefchen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sc.plugin_schaefchen.util.Constants;

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

	// mengen der benachbarten spielfelder
	@XStreamOmitField
	private final List<Set<Integer>> neighbours;
	@XStreamImplicit(itemFieldName = "neighbour")
	private final Set<Integer> directNeighbours;

	// ggf. vorghandeses gegenstueck zu diesem feld
	// wird bei den heimatfeldern benoetigt um einem schaf sein ziel mitzuteilen
	@XStreamOmitField
	private int counterPart;

	public Node(final NodeType type, int counterPart, int index) {
		this.index = index;
		this.counterPart = counterPart;

		neighbours = new ArrayList<Set<Integer>>(Constants.DIE_SIZE + 1);
		for (int i = 0; i < Constants.DIE_SIZE + 1; i++) {
			neighbours.add(new HashSet<Integer>());
		}

		neighbours.get(0).add(index);
		directNeighbours = neighbours.get(1);
		this.type = type;

	}

	/**
	 * fuegt diesem spielfeld einen nachbar mit echtem abstand hinzu
	 */
	protected void addNeighbour(final int other, int dist) {
		assert dist > 0 && dist <= Constants.DIE_SIZE;
		neighbours.get(dist).add(other);
	}

	/**
	 * liefert die menge der direkten nachbarn dieses spielfeldes
	 */
	public Set<Integer> getNeighbours() {
		return neighbours.get(1);
	}

	/**
	 * liefert die menge der nachbarn eines gegebenen abstandes dieses
	 * spielfeldes. der abstand muss mit einem wuerfel erreichbar sein
	 */
	public Set<Integer> getNeighbours(Die die) {
		return getNeighbours(die.value);
	}

	/**
	 * liefert die menge der nachbarn eines gegebenen abstandes dieses
	 * spielfeldes. der abstand muss mit einem wuerfel erreichbar sein
	 */
	public Set<Integer> getNeighbours(int dist) {
		assert dist >= 0 && dist <= Constants.DIE_SIZE;
		return neighbours.get(dist);
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