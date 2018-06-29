package sc.api.plugins;

import sc.api.plugins.exceptions.GameLogicException;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.api.plugins.host.IGameListener;
import sc.framework.plugins.SimplePlayer;
import sc.protocol.responses.ProtocolMessage;
import sc.shared.InvalidGameStateException;
import sc.shared.InvalidMoveException;
import sc.shared.PlayerScore;
import sc.shared.ScoreCause;

import java.util.List;

public interface IGameInstance
{
	/**
	 * XXX can be unique for GamePlugin, adds-player-to-game has to work for imported gameState too
	 * @return SimpleClient
	 * @throws TooManyPlayersException Thrown when already enough player
	 */
	public SimplePlayer onPlayerJoined() throws TooManyPlayersException;

	public void onPlayerLeft(SimplePlayer player);
	
	public void onPlayerLeft(SimplePlayer player, ScoreCause cause);

	/**
	 * Called by the Server once an action was received.
	 * 
	 * @param fromPlayer
	 *            The player who invoked this action.
	 * @param data
	 *            The plugin-secific data.
	 * @throws GameLogicException	if any invalid action is done, i.e. game rule violation
	 */
	public void onAction(SimplePlayer fromPlayer, ProtocolMessage data)
          throws GameLogicException, InvalidGameStateException, InvalidMoveException;

	/**
	 * Extends the set of listeners.
	 * 
	 * @param listener GameListener to be added
	 */
	public void addGameListener(IGameListener listener);

	public void removeGameListener(IGameListener listener);

	/** Server or an administrator requests the game to start now. */
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
	 *
	 * @param file File where the game should be loaded from
	 */
	public void loadFromFile(String file);

	/**
	 * The game is requested to load itself from a file (the board i.e.). This is
	 * like a replay but with actual clients. Turn is used to specify the turn to
	 * load from replay (e.g. if more than one gameState in replay)
	 *
	 * @param file File where the game should be loaded from
	 * @param turn The turn to load
	 */
	public void loadFromFile(String file, int turn);
	
	/**
	 * The game is requested to load itself from a given game information object (could be a board instance for example)
	 * @param gameInfo the stored gameInformation
	 */
	public void loadGameInfo(Object gameInfo);
	
	/**
	 * Returns the players that have won the game, empty if the game has no winners,
	 * or null if the game has not finished.
	 * @return List of SimplePlayers, which won the game
	 */
	public List<SimplePlayer> getWinners();

	/**
	 * Returns pluginUUID. Only used for generating replay name.
	 * @return uuid of plugin
	 */
	public String getPluginUUID();

  /**
   * Returns all players. This should always be 2 and the startplayer should be first in the List.
   * @return List of all players
   */
	public List<SimplePlayer> getPlayers();

  /**
   * Returns the PlayerScore for both players
   * @return List of PlayerScores
   */
	public List<PlayerScore> getPlayerScores();
}
