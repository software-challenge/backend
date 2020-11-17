package sc.framework.plugins;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.IGameInstance;
import sc.api.plugins.IGameState;
import sc.api.plugins.exceptions.GameLogicException;
import sc.api.plugins.host.IGameListener;
import sc.protocol.responses.ProtocolErrorMessage;
import sc.protocol.responses.ProtocolMessage;
import sc.shared.*;

import java.util.*;
import java.util.Map.Entry;

public abstract class RoundBasedGameInstance<P extends Player> implements IGameInstance {
  private static final Logger logger = LoggerFactory.getLogger(RoundBasedGameInstance.class);

  protected P activePlayer = null;

  private int turn = 0;

  @XStreamOmitField
  private Optional<Integer> paused = Optional.empty();

  @XStreamOmitField
  private ActionTimeout requestTimeout = null;

  @XStreamOmitField
  protected final List<IGameListener> listeners = new ArrayList<>();

  @XStreamImplicit(itemFieldName = "player")
  protected final List<P> players = new ArrayList<>();

  @XStreamOmitField
  protected final String pluginUUID;

  protected RoundBasedGameInstance(String pluginUUID) {
    this.pluginUUID = pluginUUID;
  }

  public int getRound() {
    return this.turn / 2;
  }

  /**
   * Called by the Server once an action was received.
   *
   * @param fromPlayer The player who invoked this action.
   * @param data       The plugin-specific data.
   *
   * @throws GameLogicException if any invalid action is done, i.e. game rule violation
   */
  public final void onAction(Player fromPlayer, ProtocolMessage data)
          throws GameLogicException, InvalidMoveException {
    Optional<String> errorMsg = Optional.empty();
    if (fromPlayer.equals(this.activePlayer)) {
      if (wasMoveRequested()) {
        this.requestTimeout.stop();

        if (this.requestTimeout.didTimeout()) {
          logger.warn("Client hit soft-timeout.");
          fromPlayer.setSoftTimeout(true);
          onPlayerLeft(fromPlayer, ScoreCause.SOFT_TIMEOUT);
        } else {
          onRoundBasedAction(fromPlayer, data);
        }
      } else {
        errorMsg = Optional.of("We didn't request a data from you yet.");
      }
    } else {
      errorMsg = Optional.of(String.format(
              "It's not your turn yet; expected: %s, got: %s (msg was %s).",
              this.activePlayer, fromPlayer, data));
    }
    if (errorMsg.isPresent()) {
      fromPlayer.notifyListeners(new ProtocolErrorMessage(data, errorMsg.get()));
      throw new GameLogicException(errorMsg.get());
    }
  }

  private boolean wasMoveRequested() {
    return this.requestTimeout != null;
  }

  protected abstract void onRoundBasedAction(Player fromPlayer, ProtocolMessage data)
          throws GameLogicException, InvalidMoveException;

  /**
   * Checks if a win condition in the current game state is met.
   * Checks round limit and end of round (and playerStats).
   * Checks if goal is reached
   *
   * @return WinCondition with winner and reason or null, if no win condition is
   * yet met.
   */
  public abstract WinCondition checkWinCondition();

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

  /** Server or an administrator requests the game to start now. */
  public void start() {
    next(this.players.get(0), true);
  }

  /**
   * Handle leave of a player.
   *
   * @param player the player that left.
   *
   * @see #onPlayerLeft(Player, ScoreCause)
   */
  public void onPlayerLeft(Player player) {
    onPlayerLeft(player, null);
  }

  /**
   * Handle leave of a player.
   *
   * @param player the player that left.
   * @param cause  the cause for the leave. If none is provided, then it will either be {@link ScoreCause#RULE_VIOLATION}
   *               or {@link ScoreCause#LEFT}, depending on whether the player has {@link Player#hasViolated()}
   */
  public void onPlayerLeft(Player player, ScoreCause cause) {
    if(cause == ScoreCause.REGULAR)
      return;

    if (cause == null) {
      if (!player.hasViolated()) {
        player.setLeft(true);
        cause = ScoreCause.LEFT;
      } else {
        cause = ScoreCause.RULE_VIOLATION;
      }
    }

    Map<Player, PlayerScore> scores = generateScoreMap();

    for (Entry<Player, PlayerScore> entry : scores.entrySet()) {
      if (entry.getKey() == player) {
        PlayerScore score = entry.getValue();
        entry.setValue(new PlayerScore(cause, score.getReason(), score.getParts()));
      }
    }

    notifyOnGameOver(scores);
  }

  protected final void next(P nextPlayer) {
    next(nextPlayer, false);
  }

  protected final void next(P nextPlayer, boolean firstTurn) {
    if (nextPlayer != null) {
      logger.debug("next round ({}) for player {}", getRound(), nextPlayer);
      if (!firstTurn) {
        turn++;
      }
      this.activePlayer = nextPlayer;
      // if paused, notify observers only (so they can update the GUI appropriately)
      notifyOnNewState(getCurrentState(), this.isPaused());
    }
    if (checkWinCondition() != null) {
      notifyOnGameOver(generateScoreMap());
    } else {
      if (!this.isPaused()) {
        notifyActivePlayer();
      }
    }
  }

  public abstract PlayerScore getScoreFor(P p);

  /**
   * Gets the current state representation.
   *
   * @return current state
   */
  protected abstract IGameState getCurrentState();

  /** Notifies the active player that it's his/her time to make a move. */
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
    final ActionTimeout timeout = player.getCanTimeout() ? getTimeoutFor(player)
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

  public final boolean isPaused() {
    return this.paused.map(inTurn -> this.turn >= inTurn).orElse(false);
  }


  /** Notifies players about the new state, sends a MoveRequest to active player */
  public void afterPause() {
    logger.info("Sending MoveRequest to player {}.", this.activePlayer);
    notifyOnNewState(getCurrentState(), false);
    notifyActivePlayer();
  }

  /**
   * Pauses game
   *
   * @param pause true if game should be paused
   */
  public void setPauseMode(Boolean pause) {
    if (pause) {
      if (wasMoveRequested()) {
        // pause in next turn, if client has a pending MoveRequest
        this.paused = Optional.of(this.turn + 1);
      } else {
        this.paused = Optional.of(this.turn);
      }
    } else {
      this.paused = Optional.empty();
    }
  }

  public Map<Player, PlayerScore> generateScoreMap() {
    Map<Player, PlayerScore> map = new HashMap<Player, PlayerScore>();
    for (final P p : this.players) {
      map.put(p, getScoreFor(p));
    }
    return map;
  }

  /**
   * Extends the set of listeners.
   *
   * @param listener GameListener to be added
   */
  public void addGameListener(IGameListener listener) {
    this.listeners.add(listener);
  }

  /**
   * Removes listener TODO check whether this is right/complete?
   *
   * @param listener GameListener to be removed
   */
  public void removeGameListener(IGameListener listener) {
    this.listeners.remove(listener);
  }

  protected void notifyOnGameOver(Map<Player, PlayerScore> map) {
    for (IGameListener listener : this.listeners) {
      try {
        listener.onGameOver(map);
      } catch (Exception e) {
        logger.error("GameOver Notification caused an exception.", e);
      }
    }
  }

  protected void notifyOnNewState(IGameState mementoState, boolean observersOnly) {
    for (IGameListener listener : this.listeners) {
      logger.debug("notifying {} about new game state", listener);
      try {
        listener.onStateChanged(mementoState, observersOnly);
      } catch (Exception e) {
        logger.error("NewState Notification caused an exception.", e);
      }
    }
  }

  /**
   * Catch block, after an invalid move was performed
   *
   * @param e      catched Exception, rethrown at the end
   * @param author player, that caused the exception
   *
   * @throws InvalidMoveException Always thrown
   */
  public void catchInvalidMove(InvalidMoveException e, Player author) throws InvalidMoveException {
    String err = "Ungueltiger Zug von '" + author.getDisplayName() + "'.\n" + e.getMessage();
    logger.error(err, e);
    author.setViolationReason(e.getMessage());
    author.notifyListeners(new ProtocolErrorMessage(e.move, err));
    throw e;
  }

  public String getPluginUUID() {
    return pluginUUID;
  }

}
