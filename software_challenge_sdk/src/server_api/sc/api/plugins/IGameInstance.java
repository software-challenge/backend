package sc.api.plugins;

import java.util.List;

import sc.api.plugins.exceptions.GameLogicException;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.api.plugins.host.IGameListener;
import sc.shared.ScoreCause;

public interface IGameInstance
{
	/**
	 * XXX can be unique for GamePlugin, adds player to game has to work for imported gameState too
	 * @return
	 * @throws TooManyPlayersException
	 */
	public IPlayer onPlayerJoined() throws TooManyPlayersException;

	public void onPlayerLeft(IPlayer player);
	
	public void onPlayerLeft(IPlayer player, ScoreCause cause);

	/**
	 * Called by the Server once an action was received.
	 * 
	 * @param fromPlayer
	 *            The player who invoked this action.
	 * @param data
	 *            The plugin-secific data.
	 * @throws GameLogicException	if any invalid action is done, i.e. game rule violation
	 */
	public void onAction(IPlayer fromPlayer, Object data)
			throws GameLogicException;

	/**
	 * Extends the set of listeners.
	 * 
	 * @param listener
	 */
	public void addGameListener(IGameListener listener);

	public void removeGameListener(IGameListener listener);

	/**
	 * Server or an administrator requests the game to start now.
	 */
	public void start();

	/**
	 * At any time this method might be invoked by the server. Any open handles
	 * should be removed. No events should be sent out (GameOver etc) after this
	 * method has been called.
	 */
	public void destroy();
	
	/**
	 * The game is requested to load itself from a file (the board i.e.). This is
	 * like a replay but with actual clients.
	 */
	public void loadFromFile(String file);
	
	/**
	 * The game is requested to load itself from a given game information object (could be a board instance for example)
	 */
	public void loadGameInfo(Object gameInfo);
	
	/**
	 * Returns the players that have won the game, empty if the game has no winners,
	 * or null if the game has not finished.
	 * @return
	 */
	public List<IPlayer> getWinners();
	
}
