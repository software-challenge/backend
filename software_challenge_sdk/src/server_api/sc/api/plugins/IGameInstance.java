package sc.api.plugins;

import sc.api.plugins.exceptions.GameLogicException;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.api.plugins.host.IGameListener;
import sc.shared.ScoreCause;

public interface IGameInstance
{
	/**
	 * 
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
	 * start() will only be called once this method returns true.
	 * 
	 * @return
	 */
	public boolean ready();
	
	/**
	 * The game is requested to load itself from a file (the board i.e.). This is
	 * like a replay but with actual clients.
	 */
	public void loadFromFile(String file);
	
	/**
	 * The game is requested to load itself from a given game information object (could be a board instance for example)
	 */
	public void loadGameInfo(Object gameInfo);
	
}
