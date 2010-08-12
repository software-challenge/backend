package sc.plugin_minimal;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sc.plugin_minimal.PlayerColor;
import sc.plugin_minimal.Player;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * @author ffa, sca
 * 
 */
@XStreamAlias(value = "minimal:board")
public class Board {
	

	// spieler
	protected Player player1;
	protected Player player2;

	// liste von feldern
	protected List<Node> nodes;

	private Board() {

	}

	/**
	 * New empty board
	 * 
	 * @return
	 */
	protected static Board create() {
		Board b = new Board();
		b.initialize();
		return b;
	}

	/**
	 * Create initial board here, i.e. if you need to randomly place things on
	 * the board
	 */
	private final void initialize() {
		//nodes = BoardFactory.createNodes();
	}

	/**
	 * Add player to this board. Here only two players are supported.
	 * 
	 * @param player
	 */
	protected final void addPlayer(final Player player) {
		if (player.getColor().equals(PlayerColor.PLAYER1))
			player2 = player;
		else
			player1 = player;
	}

	protected final void setPlayer1(final Player player) {
		player2 = player;
	}

	protected final void setPlayer2(final Player player) {
		player1 = player;
	}

	public final Player getOtherPlayer(final Player player) {
		assert player1 != null;
		assert player2 != null;
		return player.getColor().equals(PlayerColor.PLAYER1) ? player1
				: player2;
	}

	// liefert die liste an knoten die zu diesem board gehoeren
	public List<Node> getNodes() {
		return nodes;
	}
}