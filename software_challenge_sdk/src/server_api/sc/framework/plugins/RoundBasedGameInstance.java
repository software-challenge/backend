package sc.framework.plugins;

import java.util.*;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import sc.api.plugins.IGameInstance;
import sc.api.plugins.exceptions.GameLogicException;
import sc.api.plugins.host.IGameListener;
import sc.protocol.responses.ProtocolErrorMessage;
import sc.protocol.responses.ProtocolMessage;
import sc.protocol.responses.ProtocolMove;
import sc.shared.InvalidMoveException;
import sc.shared.PlayerScore;
import sc.shared.ScoreCause;
import sc.shared.WinCondition;

public abstract class RoundBasedGameInstance<P extends SimplePlayer> implements IGameInstance {
  private static Logger logger = LoggerFactory
          .getLogger(RoundBasedGameInstance.class);
  protected P activePlayer = null;

  /*
   * round equals the turn in the GameState, not the round the game is currently in
   * TODO rename round to turn
   */
  private int round = 0;

  @XStreamOmitField
  private Optional<Integer> paused = Optional.empty();

  @XStreamOmitField
  private Runnable afterPauseAction = null;

  @XStreamOmitField
  private Object afterPauseLock = new Object();

  @XStreamOmitField
  private ActionTimeout requestTimeout = null;

  @XStreamOmitField
  protected final List<IGameListener> listeners = new LinkedList<>();

  @XStreamImplicit(itemFieldName = "player")
  protected final List<P> players = new ArrayList<>();

  @XStreamOmitField
  protected String pluginUUID;

  public int getRound() {
    return this.round;
  }

  /**
   * Called by the Server once an action was received.
   *
   * @param fromPlayer The player who invoked this action.
   * @param data       The plugin-specific data.
   *
   * @throws GameLogicException if any invalid action is done, i.e. game rule violation
   */
  public final void onAction(SimplePlayer fromPlayer, ProtocolMessage data)
          throws GameLogicException {
    Optional<String> errorMsg = Optional.empty();
    if (fromPlayer.equals(this.activePlayer)) {
      if (wasMoveRequested()) {
        this.requestTimeout.stop();

        if (this.requestTimeout.didTimeout()) {
          logger.warn("Client hit soft-timeout.");
          fromPlayer.setSoftTimeout(true);
          onPlayerLeft(fromPlayer, ScoreCause.SOFT_TIMEOUT);
        } else {
          if (!isPaused()){
            onRoundBasedAction(fromPlayer, data);
          } else {
            logger.info("Game is paused. Save this action.");
            final P currentActivePlayer = this.activePlayer;
            synchronized (this.afterPauseLock) {
              logger.debug("Setting AfterPauseAction");

              this.afterPauseAction = () -> {
                requestMove(currentActivePlayer);
                next();
              };

              for (IGameListener listener : this.listeners) {
                listener.onPaused(currentActivePlayer);
              }
            }
          }
        }
      } else {
        errorMsg = Optional.of("We didn't request a data from you yet.");
      }
    } else {
      errorMsg = Optional.of("It's not your turn yet.");
    }
    if (errorMsg.isPresent()) {
      fromPlayer.notifyListeners(new ProtocolErrorMessage(data, errorMsg.get()));
      throw new GameLogicException(errorMsg.get());
    }
  }

  private boolean wasMoveRequested() {
    return this.requestTimeout != null;
  }

  protected abstract void onRoundBasedAction(SimplePlayer fromPlayer, ProtocolMessage data)
          throws GameLogicException;

  /**
   * Checks if a win condition in the current game state is met.
   * Checks round limit and end of round (and playerStats).
   * Checks if goal is reached
   *
   * @return WinCondition with winner and reason or null, if no win condition is
   *         yet met.
   */
  protected abstract WinCondition checkWinCondition();

  /**
   * At any time this method might be invoked by the server. Any open handles
   * should be removed. No events should be sent out (GameOver etc) after this
   * method has been called.
   */
  public void destroy() {
    logger.info("Destroying Game");

    if (this.requestTimeout != null) {
      this.requestTimeout.stop();
      this.requestTimeout = null;
    }
  }

  /**
   * Server or an administrator requests the game to start now.
   */
  public void start() {
    next(this.players.get(0));
  }

  /**
   * On violation player is removed forcefully, if player has not violated, he has left by himself (i.e. Exception)
   *
   * @param player left player
   */
  public void onPlayerLeft(SimplePlayer player) {
    if (!player.hasViolated()) {
      player.setLeft(true);
      onPlayerLeft(player, ScoreCause.LEFT);
    } else {
      onPlayerLeft(player, ScoreCause.RULE_VIOLATION);
    }
  }

  /**
   * Handle leave of player
   */
  public void onPlayerLeft(SimplePlayer player, ScoreCause cause) {
    Map<SimplePlayer, PlayerScore> res = generateScoreMap();

    for (Entry<SimplePlayer, PlayerScore> entry : res.entrySet()) {
      PlayerScore score = entry.getValue();

      if (entry.getKey() == player) {
        score.setCause(cause);
      }
    }

    notifyOnGameOver(res);
  }

  protected void onActivePlayerChanged(P newActivePlayer) {
    // optional callback
  }

  protected void next() {
    next(getPlayerAfter(this.activePlayer));
  }

  protected final P getPlayerAfter(P player) {
    return getPlayerAfter(player, 1);
  }

  protected final P getPlayerAfter(P player, int step) {
    int playerPos = this.players.indexOf(player);
    playerPos = (playerPos + step) % this.players.size();
    return this.players.get(playerPos);
  }

  protected final void next(P nextPlayer) {
    if (increaseTurnIfNecessary(nextPlayer)) {
      this.round++;

      // change player before calling new round callback
      this.activePlayer = nextPlayer;
    }
    logger.debug("next round ({}) for player {}", this.round, nextPlayer);

    this.activePlayer = nextPlayer;
    notifyOnNewState(getCurrentState());

    if (checkWinCondition() != null) {
      notifyOnGameOver(generateScoreMap());
    } else {
      if (!this.isPaused()) {
        notifyActivePlayer();
      }
    }
  }

  public abstract PlayerScore getScoreFor(P p);

  protected boolean increaseTurnIfNecessary(P nextPlayer) {
    return (this.activePlayer != nextPlayer && this.players
            .indexOf(nextPlayer) == 0);
  }

  /**
   * Gets the current state representation.
   *
   * @return current state
   */
  protected abstract Object getCurrentState();

  /**
   * Notifies the active player that it's his/her time to make a move.
   */
  protected final void notifyActivePlayer() {
    requestMove(activePlayer);
  }

  /**
   * Sends a MoveRequest directly to the player (does not take PAUSE into
   * account)
   *
   * @param player player to make a move
   */
  protected synchronized final void requestMove(P player) {
    final ActionTimeout timeout = player.isCanTimeout() ? getTimeoutFor(player)
            : new ActionTimeout(false);

    final Logger logger = RoundBasedGameInstance.logger;
    final P playerToTimeout = player;

    // Signal the JVM to do a GC run now and lower the propability that the GC
    // runs when the player sends back its move, resulting in disqualification
    // because of soft timeout.
    System.gc();

    this.requestTimeout = timeout;
    timeout.start(() -> {
      logger.warn("Player {} reached the timeout of {}ms",
              playerToTimeout, timeout.getHardTimeout());
      playerToTimeout.setHardTimeout(true);
      onPlayerLeft(playerToTimeout, ScoreCause.HARD_TIMEOUT);
    });

    player.requestMove();
  }

  protected ActionTimeout getTimeoutFor(P player) {
    return new ActionTimeout(true);
  }

  protected final boolean isPaused() {

    return this.paused.isPresent() || this.paused.get() > this.round;
  }


  public void afterPause() {
    synchronized (this.afterPauseLock) {
      if (this.afterPauseAction == null) {
        logger
                .error("AfterPauseAction was null. Might cause a deadlock.");
      } else {
        logger.info("Run AfterPauseAction.");
        Runnable action = this.afterPauseAction;
        this.afterPauseAction = null;
        action.run();
      }
    }
  }

  /**
   * Pauses game
   *
   * @param pause true if game should be paused
   */
  public void setPauseMode(Optional<Integer> pause) {
    this.paused = pause;
  }

  public Map<SimplePlayer, PlayerScore> generateScoreMap() {
    Map<SimplePlayer, PlayerScore> map = new HashMap<SimplePlayer, PlayerScore>();

    for (final P p : this.players) {
      map.put(p, getScoreFor(p));
    }

    return map;
  }

  // XXX methods from former SimpleGameInstance

  /**
   * Extends the set of listeners.
   *
   * @param listener GameListener to be added
   */
  public void addGameListener(IGameListener listener) {
    this.listeners.add(listener);
  }

  /**
   * Removes listener XXX is this right/complete?
   *
   * @param listener GameListener to be removed
   */
  public void removeGameListener(IGameListener listener) {
    this.listeners.remove(listener);
  }

  protected void notifyOnGameOver(Map<SimplePlayer, PlayerScore> map) {
    for (IGameListener listener : this.listeners) {
      try {
        listener.onGameOver(map);
      } catch (Exception e) {
        logger.error("GameOver Notification caused an exception.", e);
      }
    }
  }

  protected void notifyOnNewState(Object mementoState) {
    for (IGameListener listener : this.listeners) {
      logger.debug("notifying {} about new game state", listener);
      try {
        listener.onStateChanged(mementoState);
      } catch (Exception e) {
        logger.error("NewState Notification caused an exception.", e);
      }
    }
  }

  /**
   * Catch block, after an invalid move was performed
   * @param e catched Exception
   * @param author player, that caused the exception
   * @throws GameLogicException Always thrown
   */
  public void catchInvalidMove(InvalidMoveException e, SimplePlayer author) throws GameLogicException {
    author.setViolated(true);
    String err = "Ungueltiger Zug von '" + author.getDisplayName() + "'.\n" + e.getMessage();
    author.setViolationReason(e.getMessage());
    logger.error(err, e);
    author.notifyListeners(new ProtocolErrorMessage(e.getMove(), err));
    throw new GameLogicException(err);
  }

  public String getPluginUUID() {
    return pluginUUID;
  }
}
