package sc.player2011.error;

import java.util.LinkedList;
import java.util.List;

import sc.player2011.starter.AbstractHandler;
import sc.player2011.starter.StrategyIdentifier;
import sc.plugin2011.DebugHint;
import sc.plugin2011.Die;
import sc.plugin2011.GameState;
import sc.plugin2011.Move;
import sc.plugin2011.Player;
import sc.plugin2011.PlayerColor;
import sc.plugin2011.util.Constants;
import sc.shared.GameResult;

@StrategyIdentifier(name = "error6")
public class NodeNotReachableError extends AbstractHandler {
	private GameState gameState;

	@Override
	public void gameEnded(GameResult data, PlayerColor color,
			String errorMessage) {
	}

	@Override
	public void onRequestAction() {

		// im gamestate die liste der vorhandenen augenzahlen invertieren
		List<Die> dice = new LinkedList<Die>();
		for (int i = 1; i <= Constants.DIE_SIZE; i++) {
			dice.add(new Die(i));
		}
		for (Die die : gameState.getDice()) {
			gameState.removeDice(die);
			dice.remove(die);
		}
		for (Die die : dice) {
			gameState.addDice(die);
		}

		// einen nun gueltigen zug raussuchen
		Move move = gameState.getValidMoves().get(0);
		String err = "Schaf #" + move.sheep + " kann das Feld #" + move.target
				+ " nicht erreichen";
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
