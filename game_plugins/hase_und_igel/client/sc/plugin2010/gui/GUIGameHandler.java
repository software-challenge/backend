/**
 * 
 */
package sc.plugin2010.gui;

import sc.plugin2010.Board;
import sc.plugin2010.Client;
import sc.plugin2010.IGameHandler;
import sc.plugin2010.Move;
import sc.plugin2010.Player;
import sc.plugin2010.renderer.RenderFacade;

/**
 * @author ffi
 * 
 */
public class GUIGameHandler implements IGameHandler
{

	private Client	client;

	public GUIGameHandler(Client client)
	{
		this.client = client;
	}

	@Override
	public void onUpdate(Board board, int turn)
	{
		RenderFacade.getInstance().updateBoard(board, turn, client.getID());
	}

	@Override
	public void onUpdate(Player player, boolean own)
	{
		RenderFacade.getInstance().updatePlayer(player, own, client.getID());
	}

	public void onUpdate(String chat)
	{
		RenderFacade.getInstance().updateChat(chat, client.getID());
	}

	@Override
	public void onRequestAction()
	{
		RenderFacade.getInstance().switchToPlayer(client.getID());
	}

	@Override
	public void sendAction(Move move)
	{
		client.sendMove(move);
	}
}
