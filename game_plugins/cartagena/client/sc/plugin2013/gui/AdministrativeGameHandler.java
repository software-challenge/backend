/**
 * 
 */
package sc.plugin2013.gui;

import sc.plugin2013.GameState;
import sc.plugin2013.IGameHandler;
import sc.plugin2013.Move;
import sc.plugin2013.MoveContainer;
import sc.plugin2013.Player;
import sc.plugin2013.PlayerColor;
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
	public void sendAction(MoveContainer move) {
	}

	@Override
	public void gameEnded(GameResult data, PlayerColor color,
			String errorMessage) {
	}
}
