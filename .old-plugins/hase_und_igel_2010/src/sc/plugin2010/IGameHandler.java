package sc.plugin2010;

import sc.shared.GameResult;

public interface IGameHandler
{
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
	 * @param board
	 *            the board
	 * @param round
	 *            the round in which the game is
	 */
	void onUpdate(Board board, int round);

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
	void gameEnded(GameResult data, FigureColor color, String errorMessage);
}
