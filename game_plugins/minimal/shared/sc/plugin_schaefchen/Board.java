package sc.plugin_schaefchen;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sc.plugin_schaefchen.util.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * ein spielbrett beinhaltet die liste der spielfelder und die zur verfuegung
 * stehenden wurfel und die teilnehmenden spieler
 * 
 * @author ffa, sca, tkra
 * 
 */
@XStreamAlias(value = "sit:board")
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
			addDice();
		}

		sheeps = new LinkedList<Sheep>();
		for (Node node : nodes) {
			if (node.getNodeType().equals(NodeType.DOGHOUSE)) {
				Sheep sheep = new Sheep(node.index, node.index,
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
					for (int i = 0; i < Constants.SHEEPS_AT_HOME; i++) {
						sheeps.add(new Sheep(node.index, node.getCounterPart(),
								PlayerColor.PLAYER1));
					}
				}
			}
		} else if (player.getPlayerColor().equals(PlayerColor.PLAYER2)) {
			player2 = player;
			for (Node node : nodes) {
				if (node.getNodeType().equals(NodeType.HOME2)) {
					for (int i = 0; i < Constants.SHEEPS_AT_HOME; i++) {
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
	 * liefert alle schafe eines spielers
	 */
	public Sheep getSheepByID(int id) {
		for (Sheep sheep : this.sheeps) {
			if (sheep.index == id) {
				return sheep;
			}
		}
		return null;
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
		addDice();

	}

	private void addDice() {
		dice.add(rand.nextInt(Constants.DIE_SIZE) + 1);

		// sicherstellen, dass wenigstens zwei verschiedene  wuerfel vorhanden sind
		Integer die = dice.get(0);
		boolean same = dice.size()>1;
		while (same) {
			for (int i = 1; i<dice.size(); i++) {
				if (!dice.get(i).equals(die)) {
					same = false;
					break;
				}
			}
			if (same) {
				dice.remove(0);
				dice.add(rand.nextInt(Constants.DIE_SIZE) + 1);
			}
		}
	}

	/**
	 * liefert die anzahl aller noch vorhandenen taler
	 */
	public int getTotalFlowers() {
		int result = 0;
		for (Node node : nodes) {
			result += node.getFlowers();
		}
		return result;
	}

	public int[] getGameStats(PlayerColor p) {

		int[] stats = new int[6];
		if (p.equals(PlayerColor.NOPLAYER)) {
			return stats;
		}

		Player player = p.equals(PlayerColor.PLAYER1) ? player1 : player2;
		stats[2] = player.getCapturedSheeps();
		stats[4] = player.getCapturedFlowers();

		for (Sheep sheep : getSheeps(p)) {
			stats[0]++;
			stats[1] += sheep.getSize().getSize(sheep.owner.oponent());
			stats[3] += sheep.getFlowers();
		}

		stats[5] = Constants.SCORE_PER_SHEEP * stats[1]
				+ Constants.SCORE_PER_SAVE_SHEEP * stats[2]
				+ Constants.SCORE_PER_FLOWER * stats[3]
				+ Constants.SCORE_PER_SAVE_FLOWER * stats[4];

		return stats;

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
		return getValidReachableNodes(move.sheep).keySet().contains(this);
	}

	/**
	 * liefert eine abbildung von den gueltig erreichbaren knoten auf deren
	 * abstand zu diesem knoten
	 */
	public Map<Integer, Integer> getValidReachableNodes(int id) {
		Sheep sheep = getSheepByID(id);

		if (sheep == null) {
			return new HashMap<Integer, Integer>();
		}

		// alle erreichbaren spielfelder
		Map<Integer, Integer> reachableNodes = getReachableNodes(sheep
				.getNode());
		Map<Integer, Integer> validNodes = new HashMap<Integer, Integer>();

		// regelwidrige spielfelder entfernen
		for (Integer node : reachableNodes.keySet()) {
			boolean okay = true;

			switch (nodes[node].getNodeType()) {
			case FENCE:
				// es darf kein anderes schaf auf dem spielfeld stehen, es sei
				// den dieses schaf ist in begleitung eines scharfen
				// schaeferhundes
				if (!sheep.hasSharpSheepdog() && getSheeps(node).size() != 0) {
					okay = false;
					break;
				}

			case DOGHOUSE:
			case GRASS:
				// es darf kein eigenes schaf auf dem spielfeld stehen
				for (Sheep s : getSheeps(node)) {
					if (s.owner == sheep.owner) {
						okay = false;
						break;
					}
				}
				break;

			case HOME1:
			case HOME2:
				// es muss das richtiges zielfeld sein
				if (node != sheep.getTarget()) {
					okay = false;
				}
				break;

			}

			if (okay) {
				validNodes.put(node, reachableNodes.get(node));
			}
		}

		return validNodes;
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

		// zug auspacken
		Sheep sheep = getSheepByID(move.sheep);
		Node target = nodes[move.target];

		if (sheep == null) {
			return false;
		}

		// den verwendeten wuerfel aud dem vorrat entfernen
		Map<Integer, Integer> validMoves = getValidReachableNodes(move.sheep);
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

				sheeps.remove(victim);

			}

		}

		System.out.println("gurkenwasser *** " + sheep.getNode());
		// das schaf wird auf das zielfeld bewegt
		sheep.setNode(target.index);
		System.out.println("gurkenwasser *** " + sheep.getNode());

		System.out.println("gurkenwasser2 *** " + sheep.getFlowers());
		switch (target.getNodeType()) {
		case DOGHOUSE:
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
					sheep.owner.oponent()));

			// ... und die eigenen gesammelten schafe freigelassen
			int n = sheep.getSize().getSize(owner.getPlayerColor()) - 1;
			for (int i = 0; i < n; i++) {
				sheeps.add(new Sheep(target.index, target.getCounterPart(),
						sheep.owner));
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

		System.out.println("gurkenwasser2 *** " + sheep.getFlowers());

		return true;

	}

}