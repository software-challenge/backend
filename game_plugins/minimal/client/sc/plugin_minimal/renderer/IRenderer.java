/**
 * 
 */
package sc.plugin_minimal.renderer;

import java.awt.Image;
import java.util.Map;

import sc.plugin_minimal.Board;
import sc.plugin_minimal.PlayerColor;
import sc.plugin_minimal.Player;
import sc.shared.GameResult;

/**
 * This interface describes what the GUI panel assigned to a player/observer has to implement
 * @author ffi
 * 
 */
public interface IRenderer
{
	void updatePlayer(Player player, Player otherPlayer);

	void updateBoard(Board board, int round);

	void updateChat(String chatMsg);

	Image getImage();

	void requestMove();

	void gameEnded(GameResult data, PlayerColor color, String errorMessage);
	
	void gameError(String errorMessage);

	void shown();

	void hidden();
}
