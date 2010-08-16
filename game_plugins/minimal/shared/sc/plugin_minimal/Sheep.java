package sc.plugin_minimal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private Node target;

	// spieler dem dieses schaf gehoert
	public final Player owner;

	// spielfeld auf dem sich dieses schaf befindet
	private Node node;

	// anzahl der von diesem schaf gesammelten blumen
	private int flowers;

	// spielerfarbe des spielers dem dieses schaf gehoert
	private PlayerColor playerColor;

	public Sheep(Node start, Node target, Player owner) {
		this.target = target;
		this.owner = owner;
		this.node = start;
		start.addSheep(this);
		size = new SheepSize();
		index = nextIndex++;
		hasSharpSheepdog = false;

		if (owner != null) {
			owner.addSheep(this);
			size.add(owner.getPlayerColor());
			playerColor = owner.getPlayerColor();
		} else {
			size.add(PlayerColor.NOPLAYER);
			playerColor = PlayerColor.NOPLAYER;
		}

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
	public Node getNode() {
		return node;
	}

	/**
	 * setzt das spielfeld auf dem sich dieses schaf befindet
	 */
	public void setNode(final Node node) {
		this.node = node;
	}

	/**
	 * setzt das zielfeld fuer dieses schaf
	 */
	public void setTarget(Node target) {
		this.target = target;
	}

	/**
	 * gibt das zielfeld dieses schafs zurueck
	 */
	public Node getTarget() {
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

	/**
	 * zerstoert dieses schaf
	 */
	public void kill() {
		node.removeSheep(this);
		owner.removeSheep(this);

	}

	/**
	 * liefert eine abbildung von den erreichbaren knoten auf deren abstand zu
	 * diesem knoten
	 */
	public Map<Node, Integer> getReachableNodes() {
		List<Integer> distances = node.getBoard().getDice();
		Map<Node, Integer> nodes = new HashMap<Node, Integer>();
		for (Integer distance : distances) {
			for (Node node : this.node.getNeighbours(distance))
				nodes.put(node, distance);
		}

		return nodes;
	}

	/**
	 * liefert eine abbildung von den gueltig erreichbaren knoten auf deren
	 * abstand zu diesem knoten
	 */
	public Map<Node, Integer> getValideMoves() {

		// alle erreichbaren spielfelder
		Map<Node, Integer> nodes = getReachableNodes();

		// regelwidrige spielfelder entfernen
		for (Node node : nodes.keySet()) {
			switch (node.getNodeType()) {

			case FENCE:
				// TODO: was ist wenn eigene schafe auf dem FENCEfeld sind?
				// es darf kein anderes schaf auf dem spielfeld stehen, es sei
				// den dieses schaf ist in begleitung eines scharfen
				// schaeferhundes
				if (!hasSharpSheepdog && node.getSheeps().size() != 0) {
					nodes.remove(node);
				}

			case GRASS:
				// es darf kein eigenes schaf auf dem spielfeld stehen
				for (Sheep hat : node.getSheeps()) {
					if (hat.owner == owner) {
						nodes.remove(node);
						break;
					}
				}
				break;

			case HOME1:
			case HOME2:
				// es muss das richtiges zielfeld sein
				if (node != target) {
					nodes.remove(node);
				}
				break;

			}
		}

		return nodes;
	}

}