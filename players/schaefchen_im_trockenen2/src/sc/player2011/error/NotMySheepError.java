package sc.player2011.error;



import sc.player2011.starter.AbstractHandler;
import sc.player2011.starter.StrategyIdentifier;
import sc.plugin2011.DebugHint;
import sc.plugin2011.GameState;
import sc.plugin2011.Move;
import sc.plugin2011.Player;
import sc.plugin2011.PlayerColor;
import sc.shared.GameResult;

@StrategyIdentifier(name = "error5")
public class NotMySheepError extends AbstractHandler {
	private GameState gameState;
	private Player currentPlayer;

	@Override
	public void gameEnded(GameResult data, PlayerColor color,
			String errorMessage) {
	}

	@Override
	public void onRequestAction() {

		Move move = gameState.getValidMoves(
				currentPlayer.getPlayerColor().opponent()).get(0);
		String err = "Der aktuelle Spieler darf Schaf #" + move.sheep
				+ "nicht bewegen";
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
