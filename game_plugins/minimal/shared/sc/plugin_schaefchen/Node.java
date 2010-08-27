package sc.plugin_schaefchen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sc.plugin_schaefchen.util.Constants;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * ein spielfeld des statischen spielplans. es kennt im wesentlichen seine
 * direkten und indirekten nachbarn -- sofern diese maximal einen wuerfel
 * entfernt liegen.
 * 
 * @author tkra
 * 
 */

public final class Node implements Cloneable {

	/**
	 * eindeutiger index dieses spielfeldes
	 */
	@XStreamAsAttribute
	public final int index;

	// typ dieses spielfeldes
	@XStreamAsAttribute
	private NodeType type;

	// mengen der benachbarten spielfelder
	@XStreamOmitField
	private final List<Set<Integer>> neighbours;
	@SuppressWarnings("unused")
	@XStreamImplicit(itemFieldName = "neighbour")
	private final Set<Integer> directNeighbours;

	// ggf. vorghandeses gegenstueck zu diesem feld
	// wird bei den heimatfeldern benoetigt um einem schaf sein ziel mitzuteilen
	@XStreamOmitField
	private int counterPart;

	/**
	 * ein neues spielfeld erstellen
	 * 
	 * @param type
	 *            der spielfeldtyp dieses spielfelds
	 * @param counterPart
	 *            ggf. index des ihm gegenueberliegenden spielfeldes
	 * @param index
	 *            der index, den dieses spielfeld bekommen soll
	 */
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
		if (dist < 1 || dist > Constants.DIE_SIZE) {
			throw new IllegalArgumentException("keine gueltige augenzahl: "
					+ dist);
		}
		return neighbours.get(dist);
	}

	/**
	 * liefert den spielfeldtyp dieses spielfeldes
	 */
	public NodeType getNodeType() {
		return type;
	}

	/**
	 * liefert den index des gegenueberliegenden spielfeldes.
	 */
	protected int getCounterPart() {
		return counterPart;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		Node clone = new Node(type, counterPart, index);
		for (int i = 1; i <= Constants.DIE_SIZE; i++) {
			for (Integer n : getNeighbours(i)) {
				clone.addNeighbour(n, i);
			}
		}
		return clone;
	}
}