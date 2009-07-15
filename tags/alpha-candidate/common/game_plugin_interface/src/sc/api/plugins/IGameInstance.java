package sc.api.plugins;

import sc.api.plugins.exceptions.RescueableClientException;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.api.plugins.host.IGameListener;

public interface IGameInstance
{
	/**
	 * 
	 * @return
	 * @throws TooManyPlayersException
	 */
	public IPlayer onPlayerJoined() throws TooManyPlayersException;

	public void onPlayerLeft(IPlayer player);

	/**
	 * Called by the Server once an action was received.
	 * 
	 * @param fromPlayer
	 *            The player who invoked this action.
	 * @param data
	 *            The plugin-secific data.
	 * @throws RescueableClientException
	 */
	public void onAction(IPlayer fromPlayer, Object data)
			throws RescueableClientException;

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
}
