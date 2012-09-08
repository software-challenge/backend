/**
 * 
 */
package sc.plugin2014.gui.interface_implementation;

import sc.plugin2014.*;
import sc.plugin2014.entities.Player;
import sc.plugin2014.entities.PlayerColor;
import sc.plugin2014.gui.renderer.RenderFacade;
import sc.plugin2014.interfaces.IGameHandler;
import sc.plugin2014.moves.Move;
import sc.shared.GameResult;

/**
 * @author ffi
 * 
 */
public class HumanGameHandler implements IGameHandler {

    private final GuiClient client;

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
        RenderFacade.getInstance().requestMove(client.getID());
    }

    @Override
    public void sendAction(Move move) {
        client.sendMove(move);
    }

    @Override
    public void gameEnded(GameResult data, PlayerColor color,
            String errorMessage) {
        RenderFacade.getInstance()
                .gameEnded(
                        data,
                        client.getID(),
                        (color == PlayerColor.RED ? PlayerColor.BLUE
                                : PlayerColor.RED), errorMessage);
    }
}
