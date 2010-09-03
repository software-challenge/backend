package sc.player2011.simple;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import sc.player2011.Starter;
import sc.plugin2011.BoardFactory;
import sc.plugin2011.DebugHint;
import sc.plugin2011.DogState;
import sc.plugin2011.Flower;
import sc.plugin2011.GameState;
import sc.plugin2011.IGameHandler;
import sc.plugin2011.Move;
import sc.plugin2011.NodeType;
import sc.plugin2011.Player;
import sc.plugin2011.PlayerColor;
import sc.plugin2011.Sheep;
import sc.plugin2011.util.Constants;
import sc.shared.GameResult;

public class GreedyLogic implements IGameHandler
{

	private Starter	client;
	private GameState		gameState;
	private Player			currentPlayer;

	private class RatedMove
	{

		final int	sheep;
		final int	target;

		int			value;

		public RatedMove(int sheep, Integer target)
		{
			this.sheep = sheep;
			this.target = target;
			rateMove();
		}

		void rateMove()
		{

			Flower flower = gameState.getFlowers(target);
			if (flower != null)
			{
				value += Constants.SCORE_PER_COLLECTED_FLOWER * flower.amount;

			}

			for (Sheep other : gameState.getSheeps(target))
			{
				if (other.owner != client.getMyColor())
				{
					// punkte fuer uebernommene blumen einfach bewerten
					value += Constants.SCORE_PER_COLLECTED_FLOWER
							* other.getFlowers();

					// punkte fuer gefangene gegnerische und befreite
					// eigene schafe gleich bewerten
					value += Constants.SCORE_PER_CAPTURED_SHEEP
							* (other.getSize(PlayerColor.RED) + other
									.getSize(PlayerColor.BLUE));
				}
			}

			// wenn ein heimatfeld betreten werden kann ...
			if (BoardFactory.nodes.get(target).getNodeType() == NodeType.HOME1
					|| BoardFactory.nodes.get(target).getNodeType() == NodeType.HOME2)
			{
				Sheep thisSheep = gameState.getSheep(sheep);

				// nach hause gebrachte blumen mit vierfachem wert bewerten
				value += 4 * Constants.SCORE_PER_COLLECTED_FLOWER
						* thisSheep.getFlowers();

				// gegnerische schafe mit vierfachem wert bewerten
				value += 4 * Constants.SCORE_PER_STOLEN_SHEEP
						* thisSheep.getSize(client.getMyColor().opponent());

				// eigene schafe mitdoppeltem wert bewerten
				value += 2 * Constants.SCORE_PER_STOLEN_SHEEP
						* thisSheep.getSize(client.getMyColor().opponent());

				// 15 extrapunkte wwenn man einen passiven hund hat
				// der durch's nachhausebringen aktiv wird
				if (thisSheep.getDogState() == DogState.PASSIVE)
				{
					value += 15;
				}
			}

		}

	}

	public GreedyLogic(Starter client)
	{
		this.client = client;
	}

	@Override
	public void gameEnded(GameResult data, PlayerColor color,
			String errorMessage)
	{

		System.out.println("*** das spiel ist beendet");
	}

	@Override
	public void onRequestAction()
	{
		System.out.println("*** es wurde ein zug angefordert");

		List<RatedMove> moves = new LinkedList<RatedMove>();
		for (Move move : gameState.getValidMoves())
		{
			moves.add(new RatedMove(move.sheep, move.target));
		}

		Collections.sort(moves, new Comparator<RatedMove>() {
			@Override
			public int compare(RatedMove o1, RatedMove o2)
			{
				return o1.value > o2.value ? 1 : -1;
			}
		});

		RatedMove maximalRatedMove = moves.get(moves.size() - 1);
		Move move = new Move(maximalRatedMove.sheep, maximalRatedMove.target);


		move.addHint(new DebugHint("Strategie: unschlagar"));
		move.addHint(new DebugHint("Maximum", Integer.toString(moves.get(moves
				.size() - 1).value)));
		move.addHint(new DebugHint("Minimum", Integer.toString(moves.get(0).value)));
		move.addHint(new DebugHint("barney stinson, awesome!"));
		
		sendAction(move);

	}

	@Override
	public void onUpdate(Player player, Player otherPlayer)
	{
		currentPlayer = player;

		System.out.println("*** spielerwechsel: " + player.getPlayerColor());

	}

	@Override
	public void onUpdate(GameState gameState)
	{
		this.gameState = gameState;
		currentPlayer = gameState.getCurrentPlayer();

		System.out.println("*** das spiel geht vorran: " + gameState.getTurn()
				+ " " + currentPlayer.getPlayerColor());
	}

	@Override
	public void sendAction(Move move)
	{
		client.sendMove(move);

		System.out.println("*** sende zug: " + move.sheep + " -> "
				+ move.target);
	}

}
