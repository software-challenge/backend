/**
 * 
 */
package sc.plugin2016.gui;

import sc.plugin2016.GameState;
import sc.plugin2016.IGameHandler;
import sc.plugin2016.Move;
import sc.plugin2016.Player;
import sc.plugin2016.PlayerColor;
import sc.shared.GameResult;

/**
 * @author ffi
 * 
 */
public class AdministrativeGameHandler implements IGameHandler {
	public AdministrativeGameHandler() {
	}

	@Override
	public void onUpdate(GameState gameState) {
	}

	@Override
	public void onUpdate(Player player, Player otherPlayer) {
	}

	public void onUpdate(String chat) {
	}

	@Override
	public void onRequestAction() {
	}

	@Override
	public void sendAction(Move move) {
	}

	@Override
	public void gameEnded(GameResult data, PlayerColor color,
			String errorMessage) {
	}
}
