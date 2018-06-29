package sc.plugin2014.gui.abstractgame;

import sc.plugin2014.GameState;
import sc.plugin2014.IGameHandler;
import sc.plugin2014.entities.Player;
import sc.plugin2014.entities.PlayerColor;
import sc.plugin2014.moves.Move;
import sc.shared.GameResult;

/**
 * @author ffi
 * 
 */
public class AdministrativeGameHandler implements IGameHandler {
    public AdministrativeGameHandler() {}

    @Override
    public void onUpdate(GameState gameState) {}

    @Override
    public void onUpdate(Player player, Player otherPlayer) {}

    public void onUpdate(String chat) {}

    @Override
    public void onRequestAction() {}

    @Override
    public void sendAction(Move move) {}

    @Override
    public void gameEnded(GameResult data, PlayerColor color,
            String errorMessage) {}
}
