package sc.plugin_minimal;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
	private final List<Node> nodes;
	
	// liste der zur verfuegung stehenden wuerfelergebnisse
	private final List<Integer> dice;

	public Board() {
		nodes = BoardFactory.createNodes(this);
		dice = new LinkedList<Integer>();

		for (int i = dice.size(); i < Constants.DIE_COUNT; i++) {
			dice.add(rand.nextInt(Constants.DIE_SIZE) + 1);
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
						new Sheep(node, node.getCounterPart(), player1);
					}
				}
			}
		} else if (player.getPlayerColor().equals(PlayerColor.PLAYER2)) {
			player2 = player;
			for (Node node : nodes) {
				if (node.getNodeType().equals(NodeType.HOME2)) {
					for (int i = 0; i < Constants.HATS_IN_BASE; i++) {
						new Sheep(node, node.getCounterPart(), player2);
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

	/* liefert die liste an knoten die zu diesem board gehoeren */
	public List<Node> getNodes() {
		return new ArrayList<Node>(nodes);
	}

	/* liefert eine liste der verfuegbaren wuerfel */
	public List<Integer> getDice() {
		return new LinkedList<Integer>(dice);
	}

	/* entfernt einen wuerfel aus dem vorrat und fuellt den vorrat auf */
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
}
