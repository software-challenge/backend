/**
 * 
 */
package sc.plugin2010.gui;

import sc.plugin2010.Board;
import sc.plugin2010.EPlayerId;
import sc.plugin2010.FigureColor;
import sc.plugin2010.IGameHandler;
import sc.plugin2010.Move;
import sc.plugin2010.Player;
import sc.plugin2010.renderer.RenderFacade;
import sc.shared.GameResult;

/**
 * @author ffi
 * 
 */
public class ObserverGameHandler implements IGameHandler
{

	public ObserverGameHandler()
	{
	}

	@Override
	public void onUpdate(Board board, int turn)
	{
		RenderFacade.getInstance().updateBoard(board, turn, EPlayerId.OBSERVER);
	}

	@Override
	public void onUpdate(Player player, Player otherPlayer)
	{
		RenderFacade.getInstance().updatePlayer(player, otherPlayer,
				EPlayerId.OBSERVER);
	}

	public void onUpdate(String chat)
	{
		RenderFacade.getInstance().updateChat(chat, EPlayerId.OBSERVER);
	}

	@Override
	public void onRequestAction()
	{
		RenderFacade.getInstance().switchToPlayer(EPlayerId.OBSERVER);
		RenderFacade.getInstance().requestMove(EPlayerId.OBSERVER);
	}

	@Override
	public void sendAction(Move move)
	{
		// observer cant send moves
	}

	@Override
	public void gameEnded(GameResult data, FigureColor color, String errorMessage)
	{
		RenderFacade.getInstance().gameEnded(data, EPlayerId.OBSERVER, color, errorMessage);
	}
}
