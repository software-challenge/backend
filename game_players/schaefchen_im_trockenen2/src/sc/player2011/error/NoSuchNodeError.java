package sc.player2011.error;

import java.util.Random;

import sc.player2011.starter.AbstractHandler;
import sc.player2011.starter.StrategyIdentifier;
import sc.plugin2011.BoardFactory;
import sc.plugin2011.DebugHint;
import sc.plugin2011.GameState;
import sc.plugin2011.Move;
import sc.plugin2011.Player;
import sc.plugin2011.PlayerColor;
import sc.plugin2011.Sheep;
import sc.shared.GameResult;

@StrategyIdentifier(name = "error3")
public class NoSuchNodeError extends AbstractHandler {
	private GameState gameState;
	private Player currentPlayer;

	@Override
	public void gameEnded(GameResult data, PlayerColor color,
			String errorMessage) {
	}

	@Override
	public void onRequestAction() {

		Sheep sheep = gameState.getSheeps(currentPlayer).get(0);
		Random rand = new Random(System.currentTimeMillis());
		int target = rand.nextInt(2) == 0 ? -1 : BoardFactory.nodes.size();
		
		Move move = new Move(sheep.index, target);
		String err = "Es gibt kein Feld #" + target;
		move.addHint(new DebugHint("Erwarteter Fehler:"));
		move.addHint(new DebugHint(err));
		
		sendAction(move);

	}

	@Override
	public void onUpdate(Player player, Player otherPlayer) {
		currentPlayer = player;
	}

	@Override
	public void onUpdate(GameState gameState) {
		this.gameState = gameState;
		currentPlayer = gameState.getCurrentPlayer();
	}

	@Override
	public void sendAction(Move move) {
		getClient().sendMove(move);
	}

}
