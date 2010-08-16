package sc.plugin_minimal;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * ein schaf, bzw eine spielfigur
 * 
 * @author tkra
 * 
 */
@XStreamAlias(value = "minimal:sheep")
public final class Sheep {

	private static int nextIndex = 0;
	public final int index;

	// groesse der von diesem schaf gefuehrten herde
	private SheepSize size;

	// info, ob diese schaf einen (scharfen) schaeferhund dabei hat
	private boolean hasSheepdog;
	private boolean hasSharpSheepdog;

	// zielfeld fuer dieses schaf
	private int target;

	// spieler dem dieses schaf gehoert
	public final PlayerColor owner;

	// spielfeld auf dem sich dieses schaf befindet
	private int node;

	// anzahl der von diesem schaf gesammelten blumen
	private int flowers;

	// spielerfarbe des spielers dem dieses schaf gehoert
	private PlayerColor playerColor;

	public Sheep(int start, int target, PlayerColor owner) {
		this.target = target;
		this.owner = owner;
		this.node = start;
		size = new SheepSize();
		index = nextIndex++;
		hasSharpSheepdog = false;
		size.add(owner);
	}

	/**
	 * liefert die groesse der von diesem schaf gefuerten herde
	 */
	public SheepSize getSize() {
		return size;
	}

	/**
	 * setzen, ob dieses schaf von einem schaeferhund begleitet wird
	 */
	public void setSheepdog(boolean sheepdog) {
		this.hasSheepdog = sheepdog;

	}

	/**
	 * gibt an, ob dieses schaf von einem schaeferhund begleitet wird
	 */
	public boolean hasSheepdog() {
		return hasSheepdog;
	}

	/**
	 * setzen, ob dieses schaf von einem scharfen schaeferhund begleitet wird
	 */
	public void setSharpSheepdog(boolean sharpSheepdog) {
		this.hasSharpSheepdog = sharpSheepdog;

	}

	/**
	 * gibt an, ob dieses schaf von einem scharfen schaeferhund begleitet wird
	 */
	public boolean hasSharpSheepdog() {
		return hasSharpSheepdog;
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
	public void setNode(int node) {
		this.node = node;
	}

	/**
	 * setzt das zielfeld fuer dieses schaf
	 */
	public void setTarget(int target) {
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
	public void addFlowers(int flowers) {
		this.flowers += flowers;
	}

	/**
	 * liefert die spielerfarbe des spielers dem dieses schaf gehoert
	 */
	public PlayerColor getPlayerColor() {
		return playerColor;
	}



}