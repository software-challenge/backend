package sc.plugin2017.gui;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.framework.plugins.AbstractPlayer;
import sc.guiplugin.interfaces.IObservation;
import sc.guiplugin.interfaces.listener.IGameEndedListener;
import sc.guiplugin.interfaces.listener.INewTurnListener;
import sc.guiplugin.interfaces.listener.IReadyListener;
import sc.networking.clients.IControllableGame;
import sc.networking.clients.IUpdateListener;
import sc.networking.clients.ObservingClient;
import sc.plugin2017.EPlayerId;
import sc.plugin2017.GameState;
import sc.plugin2017.IGUIObservation;
import sc.plugin2017.IGameHandler;
import sc.plugin2017.Player;
import sc.plugin2017.PlayerColor;
import sc.plugin2017.gui.renderer.RenderFacade;
import sc.plugin2017.util.Configuration;
import sc.protocol.responses.ProtocolErrorMessage;
import sc.shared.GameResult;
import sc.shared.ScoreCause;

/**
 * The observation watches the game and is informed by the GUI client when
 * something happens
 *
 * @author ffi
 *
 */
public class Observation implements IObservation, IUpdateListener, IGUIObservation {
  private IControllableGame conGame;

  private IGameHandler handler;

  private List<IGameEndedListener> gameEndedListeners = new LinkedList<>();
  private List<INewTurnListener> newTurnListeners = new LinkedList<>();
  private List<IReadyListener> readyListeners = new LinkedList<>();

  /**
   * stores which player was active before switching to observer mode (when
   * stepping through a game)
   */
  private EPlayerId lastActivePlayer = EPlayerId.NONE;

  private static final Logger logger = LoggerFactory.getLogger(Observation.class);

  public Observation(IControllableGame conGame, IGameHandler handler) {
    this.conGame = conGame;
    this.handler = handler;
    conGame.addListener(this);
    logger.debug("Creating new observation for rommId: ", ((ObservingClient) conGame).roomId);
  }

  @Override
  public void addGameEndedListener(IGameEndedListener listener) {
    this.gameEndedListeners.add(listener);
  }

  @Override
  public void addNewTurnListener(INewTurnListener listener) {
    this.newTurnListeners.add(listener);
  }

  @Override
  public void addReadyListener(IReadyListener listener) {
    this.readyListeners.add(listener);
  }

  @Override
  public void back() {
    this.conGame.previous();
  }

  @Override
  public void cancel() {
    this.conGame.cancel();
    notifyOnGameEnded(this, this.conGame.getResult());
  }

  @Override
  public void next() {
    this.conGame.next();
  }

  @Override
  public void pause() {
    this.conGame.pause();
  }

  @Override
  public void removeGameEndedListener(IGameEndedListener listener) {
    this.gameEndedListeners.remove(listener);
  }

  @Override
  public void removeNewTurnListener(INewTurnListener listener) {
    this.newTurnListeners.remove(listener);
  }

  @Override
  public void removeReadyListener(IReadyListener listener) {
    this.readyListeners.remove(listener);
  }

  @Override
  public void saveReplayToFile(String filename) throws IOException {
    ReplayBuilder.saveReplay(Configuration.getXStream(), this.conGame, filename);
  }

  @Override
  public void start() {
    this.conGame.unpause();
    RenderFacade.getInstance().switchToPlayer(RenderFacade.getInstance().getActivePlayer());
  }

  @Override
  public void unpause() {
    RenderFacade.getInstance().switchToPlayer(RenderFacade.getInstance().getActivePlayer());
    this.conGame.unpause();
  }

  @Override
  public void ready() {
    logger.debug("got ready event");
    for (IReadyListener listener : this.readyListeners) {
      logger.debug("sending ready event to {}", listener);
      listener.ready();
    }
  }

  private String createGameEndedString(GameResult data) {
    String result = "\n";

    if (data == null) {
      result += "Leeres Spielresultat!";
      return result;
    }

    GameState gameState = (GameState) this.conGame.getCurrentState();

    String name1 = gameState.getPlayerNames()[0];
    String name2 = gameState.getPlayerNames()[1];

    if (this.conGame.getCurrentError() != null) {
      ProtocolErrorMessage error = (ProtocolErrorMessage) this.conGame.getCurrentError();
      result += (gameState.getCurrentPlayer().getPlayerColor() == PlayerColor.RED ? name1 : name2);
      result += " hat einen Fehler gemacht: \n" + error.getMessage() + "\n";
    }

    result += "Spielresultat:\n";

    if (data.getScores().get(0).getCause() == ScoreCause.LEFT) {
      result += name1;
      result += " hat das Spiel verlassen!\n";
    }

    if (data.getScores().get(1).getCause() == ScoreCause.LEFT) {
      result += name2;
      result += " hat das Spiel verlassen!\n";
    }

    if (data.getScores().get(0).getCause() == ScoreCause.RULE_VIOLATION) {
      result += name1;
      result += " hat einen falschen Zug gesetzt!\n";
    }

    if (data.getScores().get(1).getCause() == ScoreCause.RULE_VIOLATION) {
      result += name2;
      result += " hat einen falschen Zug gesetzt!\n";
    }

    if (data.getScores().get(0).getCause() == ScoreCause.HARD_TIMEOUT) {
      result += name1;
      result += " hat das HardTimeout überschritten!\n";
    }

    if (data.getScores().get(1).getCause() == ScoreCause.HARD_TIMEOUT) {
      result += name2;
      result += " hat das HardTimeout überschritten!\n";
    }

    if (data.getScores().get(0).getCause() == ScoreCause.SOFT_TIMEOUT) {
      result += name1;
      result += " hat das SoftTimeout überschritten!\n";
    }

    if (data.getScores().get(1).getCause() == ScoreCause.SOFT_TIMEOUT) {
      result += name2;
      result += " hat das SoftTimeout überschritten!\n";
    }

    String[] results1 = data.getScores().get(0).toStrings();
    String[] results2 = data.getScores().get(1).toStrings();

    String res1 = name1 + ":\n	Punkte: " + results1[0] + "\n";
    String res2 = name2 + ":\n	Punkte: " + results2[0] + "\n";
    // for(int i = 1; i < 6; i += 1) {
    // res1 += " " + GamePlugin.SCORE_DEFINITION.get(i).getName() + ": " +
    // results1[i] + "\n";
    // res2 += " " + GamePlugin.SCORE_DEFINITION.get(i).getName() + ": " +
    // results2[i] + "\n";
    // }
    //
    result += res1 + "\n";
    result += res2;

    List<AbstractPlayer> winners = data.getWinners();
    if (winners.size() > 0) {
      PlayerColor winner = ((Player) data.getWinners().get(0)).getPlayerColor();
      if (winner == PlayerColor.RED) {
        result += "Gewinner: " + name1 + "\n";
      } else if (winner == PlayerColor.BLUE) {
        result += "Gewinner: " + name2 + "\n";
      }
    } else {
      result += "Unentschieden\n";
    }

    return result;
  }

  /**
   * @param data
   *
   */
  private synchronized void notifyOnGameEnded(Object sender, GameResult data) {
    logger.info("Observation notified about game end");
    for (IGameEndedListener listener : this.gameEndedListeners) {
      try {
        listener.onGameEnded(data, createGameEndedString(data));
        logger.info("Create Game Ended String");
      } catch (Exception e) {
        logger.error("GameEnded Notification caused an exception.", e);
      }
    }

    Object errorObject = this.conGame.getCurrentError();
    String errorMessage = null;
    if (errorObject != null) {
      errorMessage = ((ProtocolErrorMessage) errorObject).getMessage();
    }
    Object curStateObject = this.conGame.getCurrentState();
    PlayerColor color = null;
    if (curStateObject != null) {
      GameState gameState = (GameState) curStateObject;
      color = gameState.getCurrentPlayer().getPlayerColor();
    }
    this.handler.gameEnded(data, color, errorMessage);

  }

  private void notifyOnNewTurn() {
    notifyOnNewTurn(0, "");
  }

  private void notifyOnNewTurn(int id, String info) {
    for (INewTurnListener listener : this.newTurnListeners) {
      try {
        listener.newTurn(id, info);
      } catch (Exception e) {
        logger.error("NewTurn Notification caused an exception.", e);
      }
    }
  }

  @Override
  public void newTurn(int id, String info) {
    notifyOnNewTurn(id, info);
  }

  @Override
  public void onUpdate(Object sender) {
    assert sender == this.conGame;
    logger.debug("got update");
    /*
    */
    GameState gameState = (GameState) this.conGame.getCurrentState();
    Object errorObject = this.conGame.getCurrentError();
    if (errorObject != null) {
      ProtocolErrorMessage error = (ProtocolErrorMessage) errorObject;
      logger.info("Received error response:" + error);
    }

    if (gameState != null) {
      // ready(); // FIXME I think this is wrong here
      this.handler.onUpdate(gameState);

      this.handler.onUpdate(gameState.getCurrentPlayer(), gameState.getOtherPlayer());

      // If the following conditional is checked, some problem might occurre for replays:
      // isGameOver is always true for replays.
      // isAtEnd might be set at the beginning of a game, because the currentGameState is the last 
      // GameState in the gameStateQueue. If this happens the result is always null (the result should 
      // only be null in an error case, these are handled separatly), so the result has to be checked,
      // whether the game has truly ended.
      if (this.conGame.isGameOver() && this.conGame.isAtEnd() && this.conGame.getResult() != null) {
        logger.debug("game is over, notifying listeners");
        notifyOnGameEnded(sender, this.conGame.getResult());
      } else {
        logger.debug("game is not yet over");
      }

      // if we are stepping back, set player to observer in order to hide player
      // controls. NOTE that it can NOT be assumed that EPlayerId.PLAYER_ONE ==
      // RED and EPlayerId.PLAYER_TWO == BLUE.
      if (this.conGame.hasNext()) {
        if (RenderFacade.getInstance().getActivePlayer() != EPlayerId.OBSERVER) {
          this.lastActivePlayer = RenderFacade.getInstance().getActivePlayer();
        }
        RenderFacade.getInstance().switchToPlayer(EPlayerId.OBSERVER);
      } else {
        // if we are at the end of available turns, switch control back to
        // player
        if (this.lastActivePlayer != EPlayerId.NONE) {
          RenderFacade.getInstance().switchToPlayer(this.lastActivePlayer);
        }
      }
    }

    notifyOnNewTurn();
  }

  @Override
  public boolean hasNext() {
    return this.conGame.hasNext();
  }

  @Override
  public boolean hasPrevious() {
    return this.conGame.hasPrevious();
  }

  @Override
  public boolean isPaused() {
    return this.conGame.isPaused();
  }

  @Override
  public boolean isFinished() {
    return this.conGame.isGameOver();
  }

  @Override
  public boolean isAtEnd() {
    return this.conGame.isAtEnd();
  }

  @Override
  public boolean isAtStart() {
    return this.conGame.isAtStart();
  }

  @Override
  public void goToFirst() {
    this.conGame.goToFirst();
  }

  @Override
  public void goToLast() {
    this.conGame.goToLast();
  }

  @Override
  public boolean canTogglePause() {
    return this.conGame.canTogglePause();
  }

  @Override
  public void onError(String errorMessage) {
    RenderFacade.getInstance().gameError(errorMessage);
  }

  @Override
  public void reset() {
    goToFirst();
  }
}
