package sc.player2011.error;

import java.util.LinkedList;
import java.util.List;

import sc.player2011.starter.AbstractHandler;
import sc.player2011.starter.StrategyIdentifier;
import sc.plugin2011.DebugHint;
import sc.plugin2011.GameState;
import sc.plugin2011.Move;
import sc.plugin2011.Player;
import sc.plugin2011.PlayerColor;
import sc.plugin2011.Sheep;
import sc.shared.GameResult;

@StrategyIdentifier(name = "error7")
public class TargetNotValidError extends AbstractHandler {
	private GameState gameState;
	private Player currentPlayer;

	@Override
	public void gameEnded(GameResult data, PlayerColor color,
			String errorMessage) {
	}

	@Override
	public void onRequestAction() {

		// liste aller erreichbaren zuege erstellen
		List<Move> possibleMoves = new LinkedList<Move>();
		for (Sheep sheep : gameState.getSheeps(currentPlayer.getPlayerColor())) {
			for (Integer target : gameState.getReacheableNodes(sheep)) {
				possibleMoves.add(new Move(sheep.index, target));
			}
		}

		// alle gueltigen zuge entfernen
		List<Move> validMoves = gameState.getValidMoves();
		possibleMoves.removeAll(validMoves);

		// wenn moeglich, einen ungueltigen zug auswaehlen, sonst normal
		// spielen
		Move move = validMoves.get(0);
		if (!possibleMoves.isEmpty()) {
			move = possibleMoves.get(0);

			String err = "Schaf #" + move.sheep + " darf das Feld #"
					+ move.target + " nicht betreten";
			move.addHint(new DebugHint("Erwarteter Fehler:"));
			move.addHint(new DebugHint(err));
		}

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
