/**
 * 
 */
package sc.plugin2010.renderer;

import java.awt.Image;

import sc.plugin2010.Board;
import sc.plugin2010.Player;

/**
 * @author ffi
 * 
 */
public interface Renderer
{
	void updatePlayer(Player player, boolean own);

	void updateBoard(Board board, int round);

	void updateAction(String doneAction);

	void updateChat(String chatMsg);

	Image getImage();

	void requestMove();
}
