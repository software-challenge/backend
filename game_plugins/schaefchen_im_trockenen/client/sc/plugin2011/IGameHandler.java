package sc.plugin2011;

import sc.plugin2011.GameState;
import sc.plugin2011.Move;
import sc.plugin2011.Player;
import sc.plugin2011.PlayerColor;
import sc.shared.GameResult;

/**
 * The game handler is used to interface the client with the observation
 * 
 * @author sven
 * 
 */
public interface IGameHandler {
	/**
	 * called, when player is updated
	 * 
	 * @param player
	 *            own player
	 * @param otherPlayer
	 *            the player object of the other player
	 */
	void onUpdate(Player player, Player otherPlayer);

	/**
	 * called, when board is updated
	 * 
	 * @param gameState
	 *            the board
	 * @param round
	 *            the round in which the game is
	 */
	void onUpdate(GameState gameState);

	/**
	 * request of a move to the client
	 */
	void onRequestAction();

	/**
	 * sends the move to the server
	 * 
	 * @param move
	 *            move to do
	 */
	void sendAction(Move move);

	/**
	 * called, when the game has ended
	 * 
	 * @param data
	 *            game results in getScores()
	 */
	void gameEnded(GameResult data, PlayerColor color, String errorMessage);
}
