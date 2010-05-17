/**
 * 
 */
package sc.plugin2010.renderer;

import java.awt.Image;
import java.util.Map;

import sc.plugin2010.Board;
import sc.plugin2010.FigureColor;
import sc.plugin2010.Player;
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
