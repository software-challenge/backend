package sc.plugin_schaefchen;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * ein schaf, bzw eine spielfigur
 * 
 * @author tkra
 * 
 */
public final class Sheep implements Cloneable {

	// fortlaufende nummer dieses schafes
	@XStreamAsAttribute
	public final int index;

	// spieler dem dieses schaf gehoert
	@XStreamAsAttribute
	public final PlayerColor owner;

	// info, ob diese schaf einen (scharfen) schaeferhund dabei hat
	@XStreamAsAttribute
	private DogState dog;

	// anzahl der eingesammelten schafe
	@XStreamAsAttribute
	private int sheeps1;
	@XStreamAsAttribute
	private int sheeps2;

	// anzahl der von diesem schaf gesammelten blumen
	@XStreamAsAttribute
	private int flowers;

	// spielfeld auf dem sich dieses schaf befindet
	@XStreamAsAttribute
	private int node;

	// zielfeld fuer dieses schaf
	@XStreamAsAttribute
	private int target;

	/**
	 * ein neues schaf erzeugen.
	 * 
	 * @param start
	 *            der index des knotens auf dem sich das schaf befinden soll
	 * @param target
	 *            der index des einzigen heimatfeldes das dieses schaf betreten
	 *            darf
	 * @param owner
	 *            der spieler der dieses schaf besitzt
	 * @param index
	 *            der eindeutige index dieses schafs
	 */
	public Sheep(int start, int target, PlayerColor owner, int index) {
		this.target = target;
		this.owner = owner;
		this.node = start;
		this.index = index;
		increaseSize(owner);
	}

	/**
	 * setzen, ob dieses schaf von einem schaeferhund begleitet wird
	 */
	protected void setDogState(DogState dogState) {
		this.dog = dogState;

	}

	/**
	 * gibt an, ob dieses schaf von einem schaeferhund begleitet wird
	 */
	public DogState getDogState() {
		return dog;
	}

	/**
	 * liefert das spielfeld auf dem sich dieses schaf befindet
	 */
	public int getNode() {
		return node;
	}

	/**
	 * setzt das spielfeld auf dem sich dieses schaf befindet
	 */
	protected void setNode(int node) {
		this.node = node;
	}

	/**
	 * setzt das zielfeld fuer dieses schaf
	 */
	protected void setTarget(int target) {
		this.target = target;
	}

	/**
	 * gibt das zielfeld dieses schafs zurueck
	 */
	public int getTarget() {
		return target;
	}

	/**
	 * liefert dia anzahl der von diesem schaf gesammelten blumen
	 */
	public int getFlowers() {
		return flowers;
	}

	/**
	 * fuegt diesem schaf blumen hinzu
	 */
	protected void addFlowers(int flowers) {
		this.flowers += flowers;
	}

	/**
	 * addiert dei anzahl der gefangenen schafe eines anderen schafes zu seiner
	 * eienen groesse.
	 */
	public void addSize(Sheep other) {
		sheeps1 += other.sheeps1;
		sheeps2 += other.sheeps2;
	}

	/**
	 * setzt die anzahl der gefangenen schafe auf 0 zurueck
	 */
	protected void resetSize() {
		sheeps1 = 0;
		sheeps2 = 0;
	}

	/**
	 * erhoeht die anzahl der gefangenen schafe in abhaengigkeit der
	 * spielerfarbe
	 */
	protected void increaseSize(PlayerColor c) {

		if (c != null) {
			if (c == PlayerColor.RED) {
				sheeps1++;
			} else if (c == PlayerColor.BLUE) {
				sheeps2++;
			}
		}
	}

	/**
	 * liefert die anzahl der gefangenen schafe inklusive sich selbst in
	 * abhaengigkeit der spielerfarbe
	 */
	public int getSize(PlayerColor c) {
		assert c == PlayerColor.RED || c == PlayerColor.BLUE;

		return c == PlayerColor.RED ? sheeps1 : sheeps2;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		Sheep dolly = new Sheep(node, target, owner, index);
		dolly.dog = dog;
		dolly.sheeps1 = sheeps1;
		dolly.sheeps2 = sheeps2;
		dolly.flowers = flowers;
		return dolly;
	}

}