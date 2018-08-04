package sc.player2011;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import sc.player2011.starter.AbstractHandler;
import sc.player2011.starter.StrategyIdentifier;
import sc.plugin2011.BoardFactory;
import sc.plugin2011.Flower;
import sc.plugin2011.GameState;
import sc.plugin2011.Move;
import sc.plugin2011.NodeType;
import sc.plugin2011.Player;
import sc.plugin2011.PlayerColor;
import sc.plugin2011.Sheep;
import sc.plugin2011.util.Constants;
import sc.shared.GameResult;

@StrategyIdentifier(name = "draw")
public class DrawLogic extends AbstractHandler {

	private GameState gameState;
	private Player currentPlayer;

	private class RatedMove {

		final int sheep;
		final int target;

		int value;
		private final int delta;

		public RatedMove(int sheep, Integer target, int delta) {
			this.sheep = sheep;
			this.target = target;
			this.delta = delta;
			rateMove();
		}

		void rateMove() {

			int value2 = 0;
			Flower flower = gameState.getFlowers(target);
			if (flower != null) {
				value2 += Constants.SCORE_PER_COLLECTED_FLOWER * flower.amount;

			}

			for (Sheep other : gameState.getSheeps(target)) {
				if (other.owner != getClient().getMyColor()) {
					// punkte fuer uebernommene blumen einfach bewerten
					value2 += Constants.SCORE_PER_COLLECTED_FLOWER
							* other.getFlowers();

					// punkte fuer gefangene gegnerische und befreite
					// eigene schafe gleich bewerten
					value2 += Constants.SCORE_PER_CAPTURED_SHEEP
							* (other.getSize(PlayerColor.RED) + other
									.getSize(PlayerColor.BLUE));
				}
			}

			// wenn ein heimatfeld betreten werden kann ...
			if (BoardFactory.nodes.get(target).getNodeType() == NodeType.HOME1
					|| BoardFactory.nodes.get(target).getNodeType() == NodeType.HOME2) {
				Sheep thisSheep = gameState.getSheep(sheep);

				// nach hause gebrachte blumen mit vierfachem wert bewerten
				value2 += Constants.SCORE_PER_COLLECTED_FLOWER
						* thisSheep.getFlowers();

				// gegnerische schafe mit vierfachem wert bewerten
				value2 += Constants.SCORE_PER_STOLEN_SHEEP
						* thisSheep
								.getSize(getClient().getMyColor().opponent());

				// eigene schafe mitdoppeltem wert bewerten
				value2 += Constants.SCORE_PER_STOLEN_SHEEP
						* thisSheep
								.getSize(getClient().getMyColor().opponent());

			}

			this.value = Math.abs(value2 - delta);

		}

	}

	@Override
	public void gameEnded(GameResult data, PlayerColor color,
			String errorMessage) {

		System.out.println("*** das spiel ist beendet");
	}

	@Override
	public void onRequestAction() {
		System.out.println("*** es wurde ein zug angefordert");

		int[][] stats = gameState.getGameStats();
		int myIndex = currentPlayer.getPlayerColor() == PlayerColor.RED ? 0 : 1;
		int delta = stats[myIndex][6] - stats[1-myIndex][6];

		List<RatedMove> moves = new LinkedList<RatedMove>();
		for (Move move : gameState.getValidMoves()) {
			moves.add(new RatedMove(move.sheep, move.target, delta));
		}

		Collections.sort(moves, new Comparator<RatedMove>() {
			@Override
			public int compare(RatedMove o1, RatedMove o2) {
				return o1.value > o2.value ? -1 : 1;
			}
		});

		RatedMove maximalRatedMove = moves.get(moves.size() - 1);
		Move move = new Move(maximalRatedMove.sheep, maximalRatedMove.target);
		move.addHint("delta = " + delta);
		move.addHint("minimum = " + moves.get(moves.size() - 1).value);
		move.addHint("maximum = " + moves.get(0).value);

		sendAction(move);

	}

	@Override
	public void onUpdate(Player player, Player otherPlayer) {
		currentPlayer = player;

		System.out.println("*** spielerwechsel: " + player.getPlayerColor());

	}

	@Override
	public void onUpdate(GameState gameState) {
		this.gameState = gameState;
		currentPlayer = gameState.getCurrentPlayer();

		System.out.println("*** das spiel geht vorran: " + gameState.getTurn()
				+ " " + currentPlayer.getPlayerColor());
	}

	@Override
	public void sendAction(Move move) {
		getClient().sendMove(move);

		System.out.println("*** sende zug: " + move.sheep + " -> "
				+ move.target);
	}

}
