package sc.player2011;

import java.util.List;
import java.util.Random;

import sc.player2011.starter.AbstractHandler;
import sc.player2011.starter.StrategyIdentifier;
import sc.plugin2011.GameState;
import sc.plugin2011.Move;
import sc.plugin2011.Player;
import sc.plugin2011.PlayerColor;
import sc.shared.GameResult;

@StrategyIdentifier(name = "simple", standard = true)
public class SimpleLogic extends AbstractHandler {
	private GameState gameState;
	private Player currentPlayer;

	@Override
	public void gameEnded(GameResult data, PlayerColor color,
			String errorMessage) {

		System.out.println("*** das spiel ist beendet");
	}

	@Override
	public void onRequestAction() {
		System.out.println("*** es wurde ein zug angefordert");

		List<Move> moves = gameState.getValidMoves();
		Random rand = new Random(System.currentTimeMillis());
		Move move = moves.get(rand.nextInt(moves.size()));
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
