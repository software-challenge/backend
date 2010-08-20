package sc.plugin_schaefchen;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * ein schaf, bzw eine spielfigur
 * 
 * @author tkra
 * 
 */
@XStreamAlias(value = "sit:sheep")
public final class Sheep {

	private static int nextIndex = 0;

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

	public Sheep(int start, int target, PlayerColor owner) {
		this.target = target;
		this.owner = owner;
		this.node = start;
		index = nextIndex++;
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
	 * addiert eine andere herdengruesse zu dieser
	 */
	public void addSize(Sheep other) {
		sheeps1 += other.sheeps1;
		sheeps2 += other.sheeps2;
	}

	/**
	 * addiert eine andere herdengruesse zu dieser
	 */
	protected void resetSize() {
		sheeps1 = 0;
		sheeps2 = 0;
	}

	/**
	 * vergroesert diese herde in abhaengigkeit einer spielerfarbe
	 */
	protected void increaseSize(PlayerColor c) {

		if (c != null) {
			if (c == PlayerColor.PLAYER1) {
				sheeps1++;
			} else if (c == PlayerColor.PLAYER2) {
				sheeps2++;
			}
		}
	}

	/**
	 * liefert die anzahl der schafe oder hunde in dieser herde in abhaengigkeit
	 * einer spielerfarbe
	 */
	public int getSize(PlayerColor c) {
		assert c == PlayerColor.PLAYER1 || c == PlayerColor.PLAYER2;

		return c == PlayerColor.PLAYER1 ? sheeps1 : sheeps2;
	}

}