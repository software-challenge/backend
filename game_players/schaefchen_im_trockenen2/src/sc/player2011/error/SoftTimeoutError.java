package sc.player2011.error;

import java.util.List;
import java.util.Random;

import sc.player2011.starter.AbstractHandler;
import sc.player2011.starter.StrategyIdentifier;
import sc.plugin2011.DebugHint;
import sc.plugin2011.GameState;
import sc.plugin2011.Move;
import sc.plugin2011.Player;
import sc.plugin2011.PlayerColor;
import sc.shared.GameResult;

@StrategyIdentifier(name = "error2")
public class SoftTimeoutError extends AbstractHandler {
	private GameState gameState;

	@Override
	public void gameEnded(GameResult data, PlayerColor color,
			String errorMessage) {

	}

	@Override
	public void onRequestAction() {

		List<Move> moves = gameState.getValidMoves();
		Random rand = new Random(System.currentTimeMillis());
		Move move = moves.get(rand.nextInt(moves.size()));
		move.addHint(new DebugHint("Strategie", "RANDOM"));
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
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
