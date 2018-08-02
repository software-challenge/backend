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
import sc.shared.GameResult;

@StrategyIdentifier(name = "error4")
public class NoSuchSheepError extends AbstractHandler {
	private GameState gameState;


	@Override
	public void gameEnded(GameResult data, PlayerColor color,
			String errorMessage) {
	}

	@Override
	public void onRequestAction() {

		Random rand = new Random(System.currentTimeMillis());
		int target = rand.nextInt(BoardFactory.nodes.size());
		int sheep = gameState.getSheeps().size() +1;
		
		Move move = new Move(sheep, target);
		String err = "Es gibt kein Schaf #" + sheep;
		move.addHint(new DebugHint("Erwarteter Fehler:"));
		move.addHint(new DebugHint(err));
		
		sendAction(move);

	}

	@Override
	public void onUpdate(Player player, Player otherPlayer) {
	}

	@Override
	public void onUpdate(GameState gameState) {
		this.gameState = gameState;
	}

	@Override
	public void sendAction(Move move) {
		getClient().sendMove(move);
	}

}
