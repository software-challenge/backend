package sc.plugin_minimal;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sc.plugin_minimal.util.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * ein spielbrett beinhaltet die liste der spielfelder und die zur verfuegung
 * stehenden wurfel und die teilnehmenden spieler
 * 
 * @author ffa, sca, tkra
 * 
 */
@XStreamAlias(value = "minimal:board")
public final class Board {

	// der eigentlicher wurfel
	private static final SecureRandom rand = new SecureRandom();

	// die teilenhmenden spieler
	private Player player1;
	private Player player2;

	// liste de spielfelder
	private final Node[] nodes;

	// liste der zur verfuegung stehenden wuerfelergebnisse
	private final List<Integer> dice;

	// liste der vorhandenen schafe
	private final List<Sheep> sheeps;

	public Board() {
		List<Node> nodeList = BoardFactory.createNodes();
		nodes = new Node[nodeList.size()];
		for (Node node : nodeList) {
			nodes[node.index] = node;
		}

		dice = new LinkedList<Integer>();
		for (int i = dice.size(); i < Constants.DIE_COUNT; i++) {
			dice.add(rand.nextInt(Constants.DIE_SIZE) + 1);
		}

		sheeps = new LinkedList<Sheep>();
		for (Node node : nodes) {
			if (node.getNodeType().equals(NodeType.DOGHOUSE)) {
				Sheep sheep =new Sheep(node.index, node.index,
						PlayerColor.NOPLAYER);
				
				if (Constants.PRE_GOLDEN_RULE) {
					sheep.setSheepdog(true);
				} else {
					sheep.setSharpSheepdog(true);
				}
				
				sheeps.add(sheep);

			}
		}
	}

	/**
	 * fuegt dem spiel einen neuen spieler hinzu und erzeugt huete in den
	 * zugehoerigen basen. es werden nur zwei spieler unterstuetzt
	 */
	public final void addPlayer(final Player player) {
		if (player.getPlayerColor().equals(PlayerColor.PLAYER1)) {
			player1 = player;
			for (Node node : nodes) {
				if (node.getNodeType().equals(NodeType.HOME1)) {
					for (int i = 0; i < Constants.HATS_IN_BASE; i++) {
						sheeps.add(new Sheep(node.index, node.getCounterPart(),
								PlayerColor.PLAYER1));
					}
				}
			}
		} else if (player.getPlayerColor().equals(PlayerColor.PLAYER2)) {
			player2 = player;
			for (Node node : nodes) {
				if (node.getNodeType().equals(NodeType.HOME2)) {
					for (int i = 0; i < Constants.HATS_IN_BASE; i++) {
						sheeps.add(new Sheep(node.index, node.getCounterPart(),
								PlayerColor.PLAYER2));
					}
				}
			}
		}
	}

	/**
	 * liefert den gegenspieler zu einem gegebenen spieler
	 */
	public Player getOtherPlayer(final Player player) {
		assert player1 != null;
		assert player2 != null;

		return player.getPlayerColor().equals(PlayerColor.PLAYER1) ? player2
				: player1;
	}

	/**
	 * liefert die liste an knoten die zu diesem board gehoeren
	 */
	public Node[] getNodes() {
		return nodes;
	}

	/**
	 * liefert einen knoten zu einem gegebenen index
	 */
	public Node getNode(Integer node) {
		assert nodes != null;
		return nodes[node];
	}

	/**
	 * liefert alle schafe
	 */
	public List<Sheep> getSheeps() { 
		return sheeps;
	}
	
	/**
	 * liefert alle schafe eines spielers
	 */
	public List<Sheep> getSheeps(PlayerColor player) {
		List<Sheep> sheeps = new LinkedList<Sheep>();
		for (Sheep sheep : this.sheeps) {
			if (sheep.owner.equals(player)) {
				sheeps.add(sheep);
			}
		}
		return sheeps;
	}

	/**
	 * liefert alle schafe eines knotens
	 */
	public List<Sheep> getSheeps(int node) {
		List<Sheep> sheeps = new LinkedList<Sheep>();
		for (Sheep sheep : this.sheeps) {
			if (sheep.getNode() == node) {
				sheeps.add(sheep);
			}
		}
		return sheeps;
	}

	/**
	 * liefert eine liste der verfuegbaren wuerfel
	 */
	public List<Integer> getDice() {
		return new LinkedList<Integer>(dice);
	}

	/**
	 * entfernt einen wuerfel aus dem vorrat und fuellt den vorrat auf
	 */
	public void removeDice(Integer die) {
		dice.remove(die);
		dice.add(rand.nextInt(Constants.DIE_SIZE) + 1);
	}

	/* liefert die anzahl aller noch vorhandenen taler */
	public int getTotalGold() {
		int result = 0;
		for (Node node : nodes) {
			result += node.getFlowers();
		}
		return result;
	}

	/**
	 * liefert die menge der (indirekten) nachbar dieses spielfeldes im abstand
	 * dist
	 */
	public Set<Integer> getNeighbours(int node, int dist) {
		Set<Integer> set = new HashSet<Integer>();
		if (dist == 0) {
			set.add(node);
		} else {
			for (Integer n : nodes[node].getNeighbours()) {
				addNeighbours(n, node, Math.abs(dist) - 1, set);
			}
		}

		return set;
	}

	/*
	 * fuegt einen menge von spielfeldern ihre (indirekten) nachbarn im abstand
	 * dist hinzu. ohne den aufrufenden knoten origin
	 */
	private void addNeighbours(int node, int origin, int dist,
			final Set<Integer> set) {
		if (dist == 0) {
			set.add(node);
		} else {
			for (Integer n : nodes[node].getNeighbours()) {
				if (n != origin) {
					addNeighbours(n, node, dist - 1, set);
				}
			}
		}

	}

	/**
	 * gibt an ob dies ein gueltiger spielzug ist, wenn der spieler dem das
	 * betroffene schaf gehoert zur zeit am zug waere
	 */
	public boolean isValideMove(Move move) {
		return getValideMoves(move.sheep).keySet().contains(this);
	}

	/**
	 * liefert eine abbildung von den gueltig erreichbaren knoten auf deren
	 * abstand zu diesem knoten
	 */
	public Map<Integer, Integer> getValideMoves(Sheep sheep) {

		// alle erreichbaren spielfelder
		Map<Integer, Integer> reachableNodes = getReachableNodes(sheep
				.getNode());

		// regelwidrige spielfelder entfernen
		for (Integer n : reachableNodes.keySet()) {
			switch (nodes[n].getNodeType()) {

			case FENCE:
				// es darf kein anderes schaf auf dem spielfeld stehen, es sei
				// den dieses schaf ist in begleitung eines scharfen
				// schaeferhundes
				if (!sheep.hasSharpSheepdog() && getSheeps(n).size() != 0) {
					reachableNodes.remove(n);
				}

			case GRASS:
				// es darf kein eigenes schaf auf dem spielfeld stehen
				for (Sheep hat : getSheeps(n)) {
					if (hat.owner == sheep.owner) {
						reachableNodes.remove(n);
						break;
					}
				}
				break;

			case HOME1:
			case HOME2:
				// es muss das richtiges zielfeld sein
				if (n != sheep.getTarget()) {
					reachableNodes.remove(n);
				}
				break;

			}
		}

		return reachableNodes;
	}

	/*
	 * liefert eine abbildung von den erreichbaren spielfelder auf deren abstand
	 * zu zu zem spielfeld auf dem sich dieses schaf befindet
	 */
	private Map<Integer, Integer> getReachableNodes(int node) {
		Map<Integer, Integer> reachableNodes = new HashMap<Integer, Integer>();
		for (Integer distance : dice) {
			for (Integer n : getNeighbours(node, distance))
				reachableNodes.put(n, distance);
		}

		return reachableNodes;
	}

	/**
	 * fuehrt diesen spielzug aus und liefert ob die aenderung in ordnung war
	 */
	public boolean performMove(Move move) {

		if (!isValideMove(move)) {
			return false;
		}

		// zug auspacken
		Sheep sheep = move.sheep;
		Node target = nodes[move.target];

		// den verwendeten wuerfel aud dem vorrat entfernen
		Map<Integer, Integer> validMoves = getValideMoves(sheep);
		removeDice(validMoves.get(move.target));

		// jedes schaf das auf dem zielfeld steht und dem gegenspieler gehoet
		// wird einverleibt. die blumen werden uebernommen. der geleitschutz
		// wird oebernommen
		for (Sheep victim : getSheeps(target.index)) {
			if (victim.owner != sheep.owner) {
				sheep.getSize().add(victim.getSize());
				sheep.addFlowers(victim.getFlowers());
				if (victim.hasSheepdog()) {
					sheep.setSheepdog(true);
				}

				if (victim.hasSharpSheepdog()) {
					sheep.setSharpSheepdog(true);
				}

			}

		}

		// das schaf wird auf das zielfeld bewegt
		sheep.setNode(target.index);

		switch (target.getNodeType()) {
		case GRASS:
		case FENCE:
			// auf normalen feldern werden die blumen aufgesammelt
			sheep.addFlowers(target.getFlowers());
			target.addFlowers(-target.getFlowers());
			break;

		case HOME1:
		case HOME2:
			// wenn ein heimatfeld betreten wird werden die gesammelten blumen
			// und gegnerischen schafe gesichert ...
			Player owner = sheep.owner == PlayerColor.PLAYER1 ? player1
					: player2;

			owner.addCapturedFlowers(sheep.getFlowers());
			owner.addCapturedSheeps(sheep.getSize().getSize(
					owner.getOponentColor()));

			// ... und die eigenen gesammelten schafe freigelassen
			int n = sheep.getSize().getSize(owner.getPlayerColor()) - 1;
			for (int i = 0; i < n; i++) {
				new Sheep(target.index, target.getCounterPart(), sheep.owner);
			}

			// wurde der schaeferhund eingesammelt bleibt er bei diesem schaf
			// und wird scharf
			sheep.getSize().reset();
			sheep.getSize().add(owner.getPlayerColor());
			if (sheep.hasSheepdog()) {
				sheep.getSize().add(PlayerColor.NOPLAYER);
				sheep.setSharpSheepdog(true);
				sheep.setSheepdog(false);
			}

			// das neue ziel ist das gegenueberliegende heimatfeld
			sheep.setTarget(target.getCounterPart());

			// die gesammelten blumen werden weggenommen
			sheep.addFlowers(-sheep.getFlowers());
			break;
		}

		return true;

	}

}
