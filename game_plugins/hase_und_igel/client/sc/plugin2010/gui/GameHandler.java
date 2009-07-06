/**
 * 
 */
package sc.plugin2010.gui;

import sc.plugin2010.BoardUpdated;
import sc.plugin2010.IGameHandler;
import sc.plugin2010.Move;
import sc.plugin2010.PlayerUpdated;
import sc.plugin2010.renderer.RenderFacade;

/**
 * @author ffi
 * 
 */
public class GameHandler implements IGameHandler
{
	@Override
	public Move onAction()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onUpdate(BoardUpdated bu)
	{
		RenderFacade.getInstance().updateBoard(bu.getBoard());
	}

	@Override
	public void onUpdate(PlayerUpdated pu)
	{
		RenderFacade.getInstance().updatePlayer(pu.getPlayer(),
				pu.isOwnPlayer());
	}

}
