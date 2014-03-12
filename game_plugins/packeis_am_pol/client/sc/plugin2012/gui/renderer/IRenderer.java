/**
 * 
 */
package sc.plugin2012.gui.renderer;

import java.awt.Image;

import sc.plugin2012.GameState;
import sc.plugin2012.Player;
import sc.plugin2012.PlayerColor;
import sc.shared.GameResult;

/**
 * This interface describes what the GUI panel assigned to a player/observer has to implement
 * @author ffi
 * 
 */
public interface IRenderer
{
	void updatePlayer(Player player, Player otherPlayer);

	void updateGameState(GameState gameState);

	void updateChat(String chatMsg);

	Image getImage();

	void requestMove();

	void gameEnded(GameResult data, PlayerColor color, String errorMessage);
	
	void gameError(String errorMessage);

	void shown();

	void hidden();
}
