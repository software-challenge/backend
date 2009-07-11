/**
 * 
 */
package sc.plugin2010.gui;

import sc.plugin2010.BoardUpdated;
import sc.plugin2010.Client;
import sc.plugin2010.IGameHandler;
import sc.plugin2010.Move;
import sc.plugin2010.PlayerUpdated;
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
	public void onUpdate(BoardUpdated bu)
	{
		RenderFacade.getInstance().updateBoard(bu, client.getID());
	}

	@Override
	public void onUpdate(PlayerUpdated pu)
	{
		RenderFacade.getInstance().updatePlayer(pu.getPlayer(),
				pu.isOwnPlayer(), client.getID());
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
