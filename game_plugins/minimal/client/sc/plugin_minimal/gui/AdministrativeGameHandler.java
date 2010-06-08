/**
 * 
 */
package sc.plugin_minimal.gui;

import sc.plugin_minimal.Board;
import sc.plugin_minimal.FigureColor;
import sc.plugin_minimal.IGameHandler;
import sc.plugin_minimal.Move;
import sc.plugin_minimal.Player;
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
	public void gameEnded(GameResult data, FigureColor color, String errorMessage)
	{
	}
}
