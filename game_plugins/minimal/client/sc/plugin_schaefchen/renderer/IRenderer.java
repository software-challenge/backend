/**
 * 
 */
package sc.plugin_schaefchen.renderer;

import java.awt.Image;

import sc.plugin_schaefchen.GameState;
import sc.plugin_schaefchen.Player;
import sc.plugin_schaefchen.PlayerColor;
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
