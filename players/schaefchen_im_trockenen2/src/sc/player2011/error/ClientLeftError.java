package sc.player2011.error;

import sc.player2011.starter.AbstractHandler;
import sc.player2011.starter.StrategyIdentifier;
import sc.plugin2011.GameState;
import sc.plugin2011.Move;
import sc.plugin2011.Player;
import sc.plugin2011.PlayerColor;
import sc.shared.GameResult;

@StrategyIdentifier(name = "error0")
public class ClientLeftError extends AbstractHandler {

	@Override
	public void gameEnded(GameResult data, PlayerColor color,
			String errorMessage) {
	}

	@Override
	public void onRequestAction() {

		System.exit(1);

	}

	@Override
	public void onUpdate(Player player, Player otherPlayer) {
	}

	@Override
	public void onUpdate(GameState gameState) {
	}

	@Override
	public void sendAction(Move move) {
		getClient().sendMove(move);
	}

}
