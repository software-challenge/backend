package sc.plugin2011.gui;

import sc.plugin2011.GameState;
import sc.plugin2011.GuiClient;
import sc.plugin2011.IGameHandler;
import sc.plugin2011.Move;
import sc.plugin2011.Player;
import sc.plugin2011.PlayerColor;
import sc.plugin2011.gui.renderer.RenderFacade;
import sc.shared.GameResult;

/**
 * @author ffi
 * 
 */
public class HumanGameHandler implements IGameHandler {

	private GuiClient client;

	public HumanGameHandler(GuiClient client) {
		this.client = client;
	}

	@Override
	public void onUpdate(GameState gameState) {
		RenderFacade.getInstance().updateGameState(gameState);
	}

	@Override
	public void onUpdate(Player player, Player otherPlayer) {
		RenderFacade.getInstance().updatePlayer(player, otherPlayer,
				client.getID());
	}

	public void onUpdate(String chat) {
		RenderFacade.getInstance().updateChat(chat, client.getID());
	}

	@Override
	public void onRequestAction() {
		RenderFacade.getInstance().switchToPlayer(client.getID());
		RenderFacade.getInstance().requestMove(client.getID());
	}

	@Override
	public void sendAction(Move move) {
		client.sendMove(move);
	}

	@Override
	public void gameEnded(GameResult data, PlayerColor color,
			String errorMessage) {
		RenderFacade.getInstance().gameEnded(
				data,
				client.getID(),
				(color == PlayerColor.RED ? PlayerColor.BLUE
						: PlayerColor.RED), errorMessage);
	}
}
