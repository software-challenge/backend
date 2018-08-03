package sc.api.plugins;

import sc.api.plugins.exceptions.GameLogicException;
import sc.api.plugins.exceptions.TooManyPlayersException;
import sc.api.plugins.host.IGameListener;
import sc.framework.plugins.AbstractPlayer;
import sc.protocol.responses.ProtocolMessage;
import sc.shared.InvalidGameStateException;
import sc.shared.InvalidMoveException;
import sc.shared.PlayerScore;
import sc.shared.ScoreCause;

import java.util.List;

public interface IGameInstance {

  /**
   * @return the player that joined
   *
   * @throws TooManyPlayersException thrown when a player can't join
   */
  AbstractPlayer onPlayerJoined() throws TooManyPlayersException;

  void onPlayerLeft(AbstractPlayer player);

  void onPlayerLeft(AbstractPlayer player, ScoreCause cause);

  /**
   * Called by the Server once an action was received.
   *
   * @param fromPlayer The player who invoked this action.
   * @param data       The plugin-secific data.
   *
   * @throws GameLogicException   if any invalid action is done
   * @throws InvalidMoveException if the received move violates the rules
   */
  void onAction(AbstractPlayer fromPlayer, ProtocolMessage data)
          throws GameLogicException, InvalidGameStateException, InvalidMoveException;

  /**
   * Extends the set of listeners.
   *
   * @param listener GameListener to be added
   */
  void addGameListener(IGameListener listener);

  void removeGameListener(IGameListener listener);

  /** Server or an administrator requests the game to start now. */
  void start();

  /**
   * Destroys the Game.
   * Might be invoked by the server at any time. Any open handles should be removed.
   * No events should be sent out (GameOver etc) after this method has been called.
   */
  void destroy();

  /**
   * The game is requested to load itself from a file (the board i.e.). This is
   * like a replay but with actual clients.
   *
   * @param file File where the game should be loaded from
   */
  void loadFromFile(String file);

  /**
   * The game is requested to load itself from a file (the board i.e.). This is
   * like a replay but with actual clients. Turn is used to specify the turn to
   * load from replay (e.g. if more than one gameState in replay)
   *
   * @param file File where the game should be loaded from
   * @param turn The turn to load
   */
  void loadFromFile(String file, int turn);

  /**
   * The game is requested to load itself from a given game information object (could be a board instance for example)
   *
   * @param gameInfo the stored gameInformation
   */
  void loadGameInfo(Object gameInfo);

  /**
   * Returns the players that have won the game, empty if the game has no winners,
   * or null if the game has not finished.
   */
  List<AbstractPlayer> getWinners();

  /** Returns pluginUUID. Only used for generating replay name. */
  String getPluginUUID();

  /** Returns all players. This should always be 2 and the startplayer should be first in the List. */
  List<AbstractPlayer> getPlayers();

  /** Returns the PlayerScores for both players */
  List<PlayerScore> getPlayerScores();
}
