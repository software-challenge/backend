/**
 * 
 */
package sc.plugin_minimal.gui;

import sc.plugin_minimal.Board;
import sc.plugin_minimal.PlayerColor;
import sc.plugin_minimal.GuiClient;
import sc.plugin_minimal.IGameHandler;
import sc.plugin_minimal.Move;
import sc.plugin_minimal.Player;
import sc.plugin_minimal.renderer.RenderFacade;
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
	public void gameEnded(GameResult data, PlayerColor color, String errorMessage)
	{
		RenderFacade.getInstance().gameEnded(data, client.getID(), (color == PlayerColor.PLAYER1 ? PlayerColor.PLAYER2 : PlayerColor.PLAYER1), errorMessage);
	}
}
