package sc.plugin_schaefchen;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import sc.plugin_schaefchen.util.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * ein spielbrett beinhaltet die liste der spielfelder und die zur verfuegung
 * stehenden wurfel und die teilnehmenden spieler
 * 
 * @author ffa, sca, tkra
 * 
 */
@XStreamAlias(value = "sit:state")
public final class GameState {

	// der eigentlicher wurfel
	private static final SecureRandom rand = new SecureRandom();

	// momentane rundenzahl
	@XStreamAsAttribute
	private int turn;

	// die teilenhmenden spieler
	@XStreamImplicit(itemFieldName = "player")
	private final List<Player> player;

	// farbe des aktuellen spielers
	@XStreamAsAttribute
	private PlayerColor currentPlayer;

	// liste der vorhandenen schafe
	@XStreamImplicit(itemFieldName = "sheep")
	private final List<Sheep> sheeps;

	// liste der blumen
	@XStreamImplicit(itemFieldName = "flowers")
	private List<Flower> flowers;

	// liste der zur verfuegung stehenden wuerfelergebnisse
	@XStreamImplicit(itemFieldName = "die")
	private final List<Die> dice;

	public GameState() {

		dice = new LinkedList<Die>();
		for (int i = dice.size(); i < Constants.DIE_COUNT; i++) {
			addDice();
		}

		sheeps = BoardFactory.createSheeps();
		flowers = BoardFactory.createFlowers();
		player = new ArrayList<Player>(2);
	}

	/**
	 * fuegt dem spiel einen neuen spieler hinzu und erzeugt huete in den
	 * zugehoerigen basen. es werden nur zwei spieler unterstuetzt
	 */
	public final void addPlayer(final Player player) {
		if (player.getPlayerColor() == PlayerColor.PLAYER1) {
			this.player.add(0, player);
		} else if (player.getPlayerColor() == PlayerColor.PLAYER2) {
			this.player.add(1, player);
		}
	}

	/**
	 * setzt den aktuellen spieler
	 */
	public void setCurrentPlayer(PlayerColor p) {
		assert p == PlayerColor.PLAYER1 || p == PlayerColor.PLAYER2;
		this.currentPlayer = p;
	}

	/**
	 * liefert den spieler der momentan am zug ist
	 */
	public Player getCurrentPlayer() {
		return currentPlayer == PlayerColor.PLAYER1 ? player.get(0) : player
				.get(1);
	}

	/**
	 * liefert den gegenspieler des aktiven spielers
	 */
	public Player getOtherPlayer() {
		return currentPlayer == PlayerColor.PLAYER1 ? player.get(1) : player
				.get(0);
	}

	/**
	 * veraendert die aktuelle rundanzahl
	 */
	public void setTurn(int turn) {
		this.turn = turn;
	}

	/**
	 * liefert den knoten zu einem gegebenen index
	 */
	public Node getNode(int nodeIndex) {
		return BoardFactory.nodes.get(nodeIndex);
	}

	/**
	 * liefert die aktuelle rundenzahl
	 */
	public int getTurn() {
		return turn;
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
	public List<Sheep> getSheeps(Player player) {
		return getSheeps(player.getPlayerColor());
	}

	/**
	 * liefert alle schafe eines spielers
	 */
	public List<Sheep> getSheeps(PlayerColor playerColor) {
		List<Sheep> sheeps = new LinkedList<Sheep>();
		for (Sheep sheep : this.sheeps) {
			if (sheep.owner == playerColor) {
				sheeps.add(sheep);
			}
		}
		return sheeps;
	}

	/**
	 * liefert alle schafe eines knotens
	 */
	public List<Sheep> getSheeps(Node node) {
		return getSheeps(node.index);
	}

	/**
	 * liefert alle schafe eines knotens
	 */
	public List<Sheep> getSheeps(int nodeIndex) {
		List<Sheep> sheeps = new LinkedList<Sheep>();
		for (Sheep sheep : this.sheeps) {
			if (sheep.getNode() == nodeIndex) {
				sheeps.add(sheep);
			}
		}
		return sheeps;
	}

	/**
	 * liefert das schaf mit einem gegebenen index, falls vorhanden, null sonst
	 */
	public Sheep getSheep(int sheepIndex) {
		for (Sheep sheep : this.sheeps) {
			if (sheep.index == sheepIndex) {
				return sheep;
			}
		}
		return null;
	}

	/**
	 * liefert dsie liste aller blumen
	 */
	public List<Flower> getAllFlowers() {
		return flowers;
	}

	/**
	 * liefert die anzahl der blumen einen gegebenen knotens
	 */
	public Flower getFlowers(int nodeIndex) {
		for (Flower flower : flowers) {
			if (flower.node == nodeIndex) {
				return flower;
			}
		}
		return null;
	}

	/**
	 * liefert die anzahl der blumen einen gegebenen knotens
	 */
	public int getFlowerAmount(int nodeIndex) {
		for (Flower flower : flowers) {
			if (flower.node == nodeIndex) {
				return flower.amount;
			}
		}
		return 0;
	}

	/**
	 * liefert die anzahl aller noch vorhandenen blumen
	 */
	public int getTotalFlowerAmount() {
		int result = 0;
		for (Flower flower : flowers) {
			result += flower.amount;
		}
		return result;
	}

	/**
	 * liefert eine liste der verfuegbaren wuerfel
	 */
	public List<Die> getDice() {
		return new LinkedList<Die>(dice);
	}

	/**
	 * entfernt einen wuerfel aus dem vorrat und fuellt den vorrat auf
	 */
	public void removeDice(Die die) {
		dice.remove(die);
		addDice();
	}

	private void addDice() {
		dice.add(new Die(rand.nextInt(Constants.DIE_SIZE) + 1));

		// sicherstellen, dass wenigstens zwei verschiedene wuerfel vorhanden
		// sind
		Die die = dice.get(0);
		boolean same = dice.size() > 1;
		while (same) {
			for (int i = 1; i < dice.size(); i++) {
				if (!dice.get(i).equals(die)) {
					same = false;
					break;
				}
			}
			if (same) {
				dice.remove(0);
				dice.add(new Die(rand.nextInt(Constants.DIE_SIZE) + 1));
			}
		}
	}

	/**
	 * liefert die statusinformationen zu einem gegebenen spieler
	 */
	public int[] getPlayerStats(Player player) {
		assert player != null;
		return getPlayerStats(player.getPlayerColor());
	}

	/**
	 * liefert die statusinformationen zu einem gegebenen spieler
	 */
	public int[] getPlayerStats(PlayerColor playerColor) {
		assert playerColor != null;

		if (playerColor == PlayerColor.PLAYER1) {
			return getGameStats()[0];
		} else {
			return getGameStats()[1];
		}
	}

	/**
	 * liefert die statusinformationen zu beiden spielern
	 */
	public int[][] getGameStats() {

		int[][] stats = new int[2][7];

		stats[0][3] = player.get(0).getStolenSheeps();
		stats[0][5] = player.get(0).getMunchedFlowers();

		stats[1][3] = player.get(1).getStolenSheeps();
		stats[1][5] = player.get(1).getMunchedFlowers();

		int index;
		for (Sheep sheep : getSheeps()) {
			if (sheep.owner != null) {
				index = sheep.owner == PlayerColor.PLAYER1 ? 0 : 1;
				stats[index][1]++;
				stats[index][2] += sheep.getSize(sheep.owner.oponent());
				stats[index][4] += sheep.getFlowers();
			}
		}

		stats[0][6] = Constants.SCORE_PER_CAPTURED_SHEEP * stats[0][2]
				+ Constants.SCORE_PER_STOLEN_SHEEP * stats[0][3]
				+ Constants.SCORE_PER_COLLECTED_FLOWER * stats[0][4]
				+ Constants.SCORE_PER_MUNCHED_FLOWER * stats[0][5];

		stats[1][6] = Constants.SCORE_PER_CAPTURED_SHEEP * stats[1][2]
				+ Constants.SCORE_PER_STOLEN_SHEEP * stats[1][3]
				+ Constants.SCORE_PER_COLLECTED_FLOWER * stats[1][4]
				+ Constants.SCORE_PER_MUNCHED_FLOWER * stats[1][5];

		stats[0][0] = (stats[0][1] > 0 && stats[0][6] > stats[1][6]) ? 1 : 0;
		stats[1][0] = (stats[1][1] > 0 && stats[1][6] > stats[0][6]) ? 1 : 0;

		return stats;

	}

	/**
	 * liefert die namen den beiden spieler
	 */
	public String[] getPlayerNames() {
		return new String[] { player.get(0).getDisplayName(),
				player.get(1).getDisplayName() };

	}

	/**
	 * gibt an ob ein schaf ein knoten betreten darf
	 */
	public boolean isValidTarget(int sheep, int node) {
		return isValidTarget(getSheep(sheep), getNode(node));
	}

	/**
	 * gibt an ob ein schaf ein knoten betreten darf
	 */
	public boolean isValidTarget(int sheep, Node node) {
		return isValidTarget(getSheep(sheep), node);
	}

	/**
	 * gibt an ob ein schaf ein knoten betreten darf
	 */
	public boolean isValidTarget(Sheep sheep, int node) {
		return isValidTarget(sheep, getNode(node));
	}

	/**
	 * gibt an ob ein schaf ein knoten betreten darf
	 */
	public boolean isValidTarget(Sheep sheep, Node node) {

		boolean okay = true;
		switch (node.getNodeType()) {
		case SAVE:
			// es darf kein anderes schaf auf dem spielfeld stehen, es sei
			// den dieses schaf ist in begleitung eines scharfen
			// schaeferhundes
			if (sheep.getDogState() != DogState.ACTIVE
					&& getSheeps(node).size() > 0) {
				okay = false;
				break;
			}

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
			if (node.index != sheep.getTarget()) {
				okay = false;
			}
			break;

		}
		return okay;
	}

	/**
	 * gibt an ob dies ein gueltiger spielzug ist, wenn der spieler dem das
	 * betroffene schaf gehoert zur zeit am zug waere
	 */
	public boolean isValideMove(Move move) {
		return getValidReacheableNodes(move.sheep).contains(
				new Integer(move.target));
	}

	/**
	 * gibt an ob dies ein gueltiger spielzug ist, wenn der spieler dem das
	 * betroffene schaf gehoert zur zeit am zug waere
	 */
	public boolean isValideMove(int sheep, int target) {
		return getValidReacheableNodes(sheep).contains(new Integer(target));
	}

	/**
	 * gibt an ob dies ein gueltiger spielzug ist, wenn der spieler dem das
	 * betroffene schaf gehoert zur zeit am zug waere
	 */
	public boolean isValideMove(int sheep, Node target) {
		return getValidReacheableNodes(sheep).contains(
				new Integer(target.index));
	}

	/**
	 * gibt an ob dies ein gueltiger spielzug ist, wenn der spieler dem das
	 * betroffene schaf gehoert zur zeit am zug waere
	 */
	public boolean isValideMove(Sheep sheep, int target) {
		return getValidReacheableNodes(sheep).contains(new Integer(target));
	}

	/**
	 * gibt an ob dies ein gueltiger spielzug ist, wenn der spieler dem das
	 * betroffene schaf gehoert zur zeit am zug waere
	 */
	public boolean isValideMove(Sheep sheep, Node target) {
		return getValidReacheableNodes(sheep).contains(
				new Integer(target.index));
	}

	/**
	 * liefert alle mit den aktuellen wuerfeln von einem gegebenen schaf
	 * erreichebaren knoten
	 */
	public Set<Integer> getReacheableNodes(Sheep sheep) {
		return getReacheableNodes(sheep.getNode());
	}

	/**
	 * liefert alle mit den aktuellen wuerfeln von einem gegebenen knoten aus
	 * erreichebaren knoten
	 */
	public Set<Integer> getReacheableNodes(int nodeIndex) {
		return getReacheableNodes(getNode(nodeIndex));
	}

	/**
	 * liefert alle mit den aktuellen wuerfeln von einem gegebenen knoten aus
	 * erreichebaren knoten
	 */
	public Set<Integer> getReacheableNodes(Node node) {
		Set<Integer> reachableNodes = new HashSet<Integer>();

		for (Die die : dice) {
			reachableNodes.addAll(node.getNeighbours(die));
		}
		return reachableNodes;

	}

	/**
	 * liefert alle mit den aktuellen wuerfeln von einem gegebenen knoten aus
	 * erreichebaren knoten
	 */
	public Set<Integer> getValidReacheableNodes(int sheepIndex) {
		return getValidReacheableNodes(getSheep(sheepIndex));
	}

	/**
	 * liefert alle mit den aktuellen wuerfeln von einem gegebenen knoten aus
	 * erreichebaren knoten
	 */
	public Set<Integer> getValidReacheableNodes(Sheep sheep) {
		Set<Integer> reachableNodes = getReacheableNodes(sheep);
		Set<Integer> validNodes = new HashSet<Integer>();

		for (Integer target : reachableNodes) {
			if (isValidTarget(sheep, target)) {
				validNodes.add(target);
			}
		}

		return validNodes;
	}

	/**
	 * liefert alle momentan gueltigen zuege
	 */
	public List<Move> getValidMoves() {
		List<Move> validMoves = new LinkedList<Move>();
		for (Sheep sheep : getSheeps(currentPlayer)) {
			for (Integer target : getValidReacheableNodes(sheep)) {
				validMoves.add(new Move(sheep.index, target));
			}
		}
		return validMoves;
	}

	/**
	 * fuehrt diesen spielzug aus und liefert ob die aenderung in ordnung war
	 */
	public boolean performMove(Move move) {

		// zug auspacken

		int sheepIndex = move.sheep;
		int targetIndex = move.target;
		Sheep sheep = getSheep(sheepIndex);
		Node target = getNode(targetIndex);

		if (sheep == null || !isValideMove(move)) {
			return false;
		}

		// den verwendeten wuerfel suchen und aus dem vorrat entfernen
		int nodeIndex = sheep.getNode();
		Node node = getNode(nodeIndex);
		for (Die die : dice) {
			if (node.getNeighbours(die).contains(new Integer(targetIndex))) {
				removeDice(die);
				break;
			}
		}

		// jedes schaf das auf dem zielfeld steht und dem gegenspieler gehoet
		// wird einverleibt. die blumen werden uebernommen. der hund
		// wird uebernommen
		for (Sheep victim : getSheeps(targetIndex)) {
			if (victim.owner != sheep.owner) {
				sheep.addSize(victim);
				sheep.addFlowers(victim.getFlowers());

				// hund, sofern vorhanden, wird uebernommen
				if (victim.getDogState() != null) {
					sheep.setDogState(victim.getDogState());
				}

				// das opfer wird entfernt
				sheeps.remove(victim);
			}
		}

		// das schaf wird auf das zielfeld bewegt
		sheep.setNode(targetIndex);

		switch (target.getNodeType()) {
		default:
			// auf normalen feldern werden die blumen aufgesammelt
			Flower flower = getFlowers(targetIndex);
			if (flower != null) {
				flowers.remove(flower);
				sheep.addFlowers(flower.amount);
			}
			break;

		case SAVE:
			break;

		case HOME1:
		case HOME2:
			// wenn ein heimatfeld betreten wird werden die gesammelten blumen
			// und gegnerischen schafe gesichert ...
			Player owner = sheep.owner == PlayerColor.PLAYER1 ? player.get(0)
					: player.get(1);

			// die gesammelten blumen werden gefressen
			owner.munchFlowers(sheep.getFlowers());
			sheep.addFlowers(-sheep.getFlowers());

			// die gefangenen schafe werden gestohlen
			owner.stealSheeps(sheep.getSize(sheep.owner.oponent()));

			// ... und die eigenen gefangenen schafe freigelassen
			int n = sheep.getSize(owner.getPlayerColor()) - 1;
			for (int i = 0; i < n; i++) {
				sheeps.add(new Sheep(targetIndex, target.getCounterPart(),
						sheep.owner));
			}

			// wurde der schaeferhund eingesammelt bleibt er bei diesem schaf
			// und wird aktiv
			sheep.resetSize();
			sheep.increaseSize(owner.getPlayerColor());
			if (sheep.getDogState() == DogState.PASSIVE) {
				sheep.setDogState(DogState.ACTIVE);
			}

			// das neue ziel ist das gegenueberliegende heimatfeld
			sheep.setTarget(target.getCounterPart());

			break;
		}

		return true;

	}

}