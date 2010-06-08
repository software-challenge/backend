/**
 * 
 */
package sc.plugin_minimal.renderer;

import java.awt.Image;
import java.util.Map;

import sc.plugin_minimal.Board;
import sc.plugin_minimal.FigureColor;
import sc.plugin_minimal.Player;
import sc.shared.GameResult;

/**
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

	void gameEnded(GameResult data, FigureColor color, String errorMessage);
	
	void gameError(String errorMessage);

	void shown();

	void hidden();
}
