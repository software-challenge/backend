package sc.plugin_schaefchen.simple;

import java.util.List;
import java.util.Random;

import sc.plugin_schaefchen.DebugHint;
import sc.plugin_schaefchen.GameState;
import sc.plugin_schaefchen.IGameHandler;
import sc.plugin_schaefchen.Move;
import sc.plugin_schaefchen.Player;
import sc.plugin_schaefchen.PlayerColor;
import sc.shared.GameResult;

public class RandomLogic implements IGameHandler
{

	private Starter	client;
	private GameState		gameState;
	private Player			currentPlayer;

	public RandomLogic(Starter client)
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

		List<Move> moves = gameState.getValidMoves();
		Random rand = new Random(System.currentTimeMillis());
		Move move = moves.get(rand.nextInt(moves.size()));
		move.addHint(new DebugHint("Strategie", "RANDOM"));
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
