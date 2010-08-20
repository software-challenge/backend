package sc.plugin_schaefchen;

import org.omg.PortableInterceptor.NON_EXISTENT;

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
	public final PlayerColor owner;

	// info, ob diese schaf einen (scharfen) schaeferhund dabei hat
	private DogState dogState;

	// anzahl der eingesammelten schafe
	private int sheepsFromPlayer1;
	private int sheepsFromPlayer2;

	// anzahl der von diesem schaf gesammelten blumen
	private int flowers;

	// spielfeld auf dem sich dieses schaf befindet
	private int node;

	// zielfeld fuer dieses schaf
	private int target;

	public Sheep(int start, int target, PlayerColor owner) {
		this.target = target;
		this.owner = owner;
		this.node = start;
		index = nextIndex++;
		dogState = DogState.NONE;
		increaseSize(owner);
	}

	/**
	 * setzen, ob dieses schaf von einem schaeferhund begleitet wird
	 */
	protected void setDogState(DogState dogState) {
		this.dogState = dogState;

	}


	/**
	 * gibt an, ob dieses schaf von einem schaeferhund begleitet wird
	 */
	public DogState getDogState() {
		return dogState;
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
		sheepsFromPlayer1 += other.sheepsFromPlayer1;
		sheepsFromPlayer2 += other.sheepsFromPlayer2;
	}

	/**
	 * addiert eine andere herdengruesse zu dieser
	 */
	protected void resetSize() {
		sheepsFromPlayer1 = 0;
		sheepsFromPlayer2 = 0;
	}

	/**
	 * vergroesert diese herde in abhaengigkeit einer spielerfarbe
	 */
	protected void increaseSize(PlayerColor c) {

		if (c.equals(PlayerColor.PLAYER1)) {
			sheepsFromPlayer1++;
		} else if (c.equals(PlayerColor.PLAYER2)) {
			sheepsFromPlayer2++;
		}

	}

	/**
	 * liefert die anzahl der schafe oder hunde in dieser herde in abhaengigkeit
	 * einer spielerfarbe
	 */
	public int getSize(PlayerColor c) {
		assert c.equals(PlayerColor.PLAYER1) || c.equals(PlayerColor.PLAYER2);

		return c.equals(PlayerColor.PLAYER1) ? sheepsFromPlayer1
				: sheepsFromPlayer2;
	}

}