/**
 * 
 */
package sc.plugin_minimal.gui;

import sc.plugin_minimal.Board;
import sc.plugin_minimal.EPlayerId;
import sc.plugin_minimal.PlayerColor;
import sc.plugin_minimal.IGameHandler;
import sc.plugin_minimal.Move;
import sc.plugin_minimal.Player;
import sc.plugin_minimal.renderer.RenderFacade;
import sc.shared.GameResult;

/**
 * 
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
	public void gameEnded(GameResult data, PlayerColor color, String errorMessage)
	{
		RenderFacade.getInstance().gameEnded(data, EPlayerId.OBSERVER, color, errorMessage);
	}
}
