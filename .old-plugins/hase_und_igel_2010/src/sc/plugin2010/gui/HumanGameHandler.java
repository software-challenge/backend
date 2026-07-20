/**
 * 
 */
package sc.plugin2010.gui;

import sc.plugin2010.Board;
import sc.plugin2010.FigureColor;
import sc.plugin2010.GuiClient;
import sc.plugin2010.IGameHandler;
import sc.plugin2010.Move;
import sc.plugin2010.Player;
import sc.plugin2010.renderer.RenderFacade;
import sc.shared.GameResult;

/**
 * @author ffi
 * 
 */
public class HumanGameHandler implements IGameHandler
{

	private GuiClient	client;

	public HumanGameHandler(GuiClient client)
	{
		this.client = client;
	}

	@Override
	public void onUpdate(Board board, int turn)
	{
		RenderFacade.getInstance().updateBoard(board, turn, client.getID());
	}

	@Override
	public void onUpdate(Player player, Player otherPlayer)
	{
		RenderFacade.getInstance().updatePlayer(player, otherPlayer,
				client.getID());
	}

	public void onUpdate(String chat)
	{
		RenderFacade.getInstance().updateChat(chat, client.getID());
	}

	@Override
	public void onRequestAction()
	{
		RenderFacade.getInstance().switchToPlayer(client.getID());
		RenderFacade.getInstance().requestMove(client.getID());
	}

	@Override
	public void sendAction(Move move)
	{
		client.sendMove(move);
	}

	@Override
	public void gameEnded(GameResult data, FigureColor color, String errorMessage)
	{
		RenderFacade.getInstance().gameEnded(data, client.getID(), (color == FigureColor.RED ? FigureColor.BLUE : FigureColor.RED), errorMessage);
	}
}
