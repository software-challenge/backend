/**
 * 
 */
package sc.plugin2010.renderer;

import sc.plugin2010.Board;
import sc.plugin2010.Player;

/**
 * @author ffi
 * 
 */
public interface Renderer
{
	void updatePlayer(Player player, boolean own);

	void updateBoard(Board bo);

	void updateInfos(int round);

	void updateAction(String doneAction);

	void updateChat(String chatMsg);
}
