package sc.plugin2017.gui;

import sc.plugin2017.GameState;
import sc.plugin2017.GuiClient;
import sc.plugin2017.IGameHandler;
import sc.plugin2017.Move;
import sc.plugin2017.Player;
import sc.plugin2017.PlayerColor;
import sc.plugin2017.gui.renderer.RenderFacade;
import sc.shared.GameResult;

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
    // updates are handled in gameState update only
	}

	@Override
	public void onRequestAction() {
		RenderFacade.getInstance().requestMove(client.getID());
	}

	@Override
	public void sendAction(Move move) {
		client.sendMove(move);
	}

	@Override
	public void gameEnded(GameResult data, PlayerColor color,
			String errorMessage) {
    /*
     * Human players can't end the game, it is ended by the observer who
	   * received a result. Human players also receive the end result, but when
	   * the observer and both human players call the render facade, it would get
	   * the game end event three times.
     */
	}
}
