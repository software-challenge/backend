/**
 * 
 */
package sc.plugin2010.renderer;

import java.awt.Image;

import sc.plugin2010.BoardUpdated;
import sc.plugin2010.Player;

/**
 * @author ffi
 * 
 */
public interface Renderer
{
	void updatePlayer(Player player, boolean own);

	void updateBoard(BoardUpdated bu);

	void updateAction(String doneAction);

	void updateChat(String chatMsg);

	Image getImage();
}
