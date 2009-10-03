/**
 * 
 */
package sc.plugin2010.gui;

import sc.plugin2010.Board;
import sc.plugin2010.IGameHandler;
import sc.plugin2010.Move;
import sc.plugin2010.Player;
import sc.shared.GameResult;

/**
 * @author ffi
 * 
 */
public class AdministrativeGameHandler implements IGameHandler
{
	public AdministrativeGameHandler()
	{
	}

	@Override
	public void onUpdate(Board board, int turn)
	{
	}

	@Override
	public void onUpdate(Player player, Player otherPlayer)
	{
	}

	public void onUpdate(String chat)
	{
	}

	@Override
	public void onRequestAction()
	{
	}

	@Override
	public void sendAction(Move move)
	{
	}

	@Override
	public void gameEnded(GameResult data)
	{
	}
}
