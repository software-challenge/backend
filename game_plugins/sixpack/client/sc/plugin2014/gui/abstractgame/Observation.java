package sc.plugin2014.gui.abstractgame;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.IPlayer;
import sc.api.plugins.host.ReplayBuilder;
import sc.guiplugin.interfaces.IObservation;
import sc.guiplugin.interfaces.listener.*;
import sc.networking.clients.IControllableGame;
import sc.networking.clients.IUpdateListener;
import sc.plugin2014.GameState;
import sc.plugin2014.IGameHandler;
import sc.plugin2014.entities.Player;
import sc.plugin2014.entities.PlayerColor;
import sc.plugin2014.gui.renderer.RenderFacade;
import sc.plugin2014.util.XStreamConfiguration;
import sc.protocol.responses.ErrorResponse;
import sc.shared.GameResult;
import sc.shared.ScoreCause;

/**
 * The observation watches the game and is informed by the GUI client when
 * something happens
 * 
 * @author ffi
 * 
 */
public class Observation implements IObservation, IUpdateListener,
        IGUIObservation {
    private final IControllableGame        conGame;

    private final IGameHandler             handler;

    private final List<IGameEndedListener> gameEndedListeners  = new LinkedList<IGameEndedListener>();
    private final List<INewTurnListener>   newTurnListeners    = new LinkedList<INewTurnListener>();
    private final List<IReadyListener>     readyListeners      = new LinkedList<IReadyListener>();

    private boolean                        notifiedOnGameEnded = false;

    private static final Logger            logger              = LoggerFactory
                                                                       .getLogger(Observation.class);

    public Observation(IControllableGame conGame, IGameHandler handler) {
        this.conGame = conGame;
        this.handler = handler;
        conGame.addListener(this);
    }

    @Override
    public void addGameEndedListener(IGameEndedListener listener) {
        gameEndedListeners.add(listener);
    }

    @Override
    public void addNewTurnListener(INewTurnListener listener) {
        newTurnListeners.add(listener);
    }

    @Override
    public void addReadyListener(IReadyListener listener) {
        readyListeners.add(listener);
    }

    @Override
    public void back() {
        conGame.previous();
    }

    @Override
    public void cancel() {
        conGame.cancel();
        notifyOnGameEnded(this, conGame.getResult());
    }

    @Override
    public void next() {
        conGame.next();
        // showActivePlayerIfNecessary();
    }

    // private void showActivePlayerIfNecessary() {
    // if (!conGame.hasNext()) {
    // if (RenderFacade.getInstance().getActivePlayer() != null) {
    // RenderFacade.getInstance().switchToPlayer(
    // RenderFacade.getInstance().getActivePlayer());
    // }
    // }
    // }

    @Override
    public void pause() {
        conGame.pause();
    }

    @Override
    public void removeGameEndedListener(IGameEndedListener listener) {
        gameEndedListeners.remove(listener);
    }

    @Override
    public void removeNewTurnListener(INewTurnListener listener) {
        newTurnListeners.remove(listener);
    }

    @Override
    public void removeReadyListener(IReadyListener listener) {
        readyListeners.remove(listener);
    }

    @Override
    public void saveReplayToFile(String filename) throws IOException {
        ReplayBuilder.saveReplay(XStreamConfiguration.getXStream(), conGame,
                filename);
    }

    @Override
    public void start() {
        conGame.unpause();
        // if (RenderFacade.getInstance().getActivePlayer() != null) {
        // RenderFacade.getInstance().switchToPlayer(
        // RenderFacade.getInstance().getActivePlayer());
        // }
    }

    @Override
    public void unpause() {
        // RenderFacade.getInstance().switchToPlayer(
        // RenderFacade.getInstance().getActivePlayer());
        conGame.unpause();
    }

    @Override
    public void ready() {
        for (IReadyListener list : readyListeners) {
            list.ready();
        }
    }

    private String createGameEndedString(GameResult data) {
        String result = "\n";

        if (data == null) {
            result += "Leeres Spielresultat!";
            return result;
        }

        GameState gameState = (GameState) conGame.getCurrentState();

        String name1 = gameState.getPlayerNames()[0];
        String name2 = gameState.getPlayerNames()[1];

        if (conGame.getCurrentError() != null) {
            ErrorResponse error = (ErrorResponse) conGame.getCurrentError();
            result += (gameState.getCurrentPlayer().getPlayerColor() == PlayerColor.RED ? name1
                    : name2);
            result += " hat einen Fehler gemacht: \n" + error.getMessage()
                    + "\n";
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
        // res1 += "	" + GamePlugin.SCORE_DEFINITION.get(i).getName() + ": " +
        // results1[i] + "\n";
        // res2 += "	" + GamePlugin.SCORE_DEFINITION.get(i).getName() + ": " +
        // results2[i] + "\n";
        // }
        //
        result += res1 + "\n";
        result += res2;

        List<IPlayer> winners = data.getWinners();
        if (winners.size() > 0) {
            PlayerColor winner = ((Player) data.getWinners().get(0))
                    .getPlayerColor();
            if (winner == PlayerColor.RED) {
                result += "Gewinner: " + name1 + "\n";
            }
            else if (winner == PlayerColor.BLUE) {
                result += "Gewinner: " + name2 + "\n";
            }
        }
        else {
            result += "Unentschieden\n";
        }

        return result;
    }

    /**
     * @param data
     * 
     */
    private synchronized void notifyOnGameEnded(Object sender, GameResult data) {
        if (!notifiedOnGameEnded) {
            notifiedOnGameEnded = true;

            for (IGameEndedListener listener : gameEndedListeners) {
                try {
                    listener.onGameEnded(data, createGameEndedString(data));
                }
                catch (Exception e) {
                    logger.error("GameEnded Notification caused an exception.",
                            e);
                }
            }
        }

        Object errorObject = conGame.getCurrentError();
        String errorMessage = null;
        if (errorObject != null) {
            errorMessage = ((ErrorResponse) errorObject).getMessage();
        }
        Object curStateObject = conGame.getCurrentState();
        PlayerColor color = null;
        if (curStateObject != null) {
            GameState gameState = (GameState) curStateObject;
            color = gameState.getCurrentPlayer().getPlayerColor();
        }
        handler.gameEnded(data, color, errorMessage);
    }

    private void notifyOnNewTurn() {
        notifyOnNewTurn(0, "");
    }

    private void notifyOnNewTurn(int id, String info) {
        for (INewTurnListener listener : newTurnListeners) {
            try {
                listener.newTurn(id, info);
            }
            catch (Exception e) {
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
        assert sender == conGame;
        GameState gameState = (GameState) conGame.getCurrentState();
        Object errorObject = conGame.getCurrentError();
        if (errorObject != null) {
            ErrorResponse error = (ErrorResponse) errorObject;
            logger.info("Received error response:" + error);
        }

        if (gameState != null) {
            // ready();
            handler.onUpdate(gameState);

            handler.onUpdate(gameState.getCurrentPlayer(),
                    gameState.getOtherPlayer());

            if (conGame.isGameOver() && conGame.isAtEnd()) {
                notifyOnGameEnded(sender, conGame.getResult());
            }

            if (conGame.hasNext()) {
                // RenderFacade.getInstance().switchToPlayer(EPlayerId.OBSERVER);
            }
        }

        notifyOnNewTurn();
    }

    @Override
    public boolean hasNext() {
        return conGame.hasNext();
    }

    @Override
    public boolean hasPrevious() {
        return conGame.hasPrevious();
    }

    @Override
    public boolean isPaused() {
        return conGame.isPaused();
    }

    @Override
    public boolean isFinished() {
        return conGame.isGameOver();
    }

    @Override
    public boolean isAtEnd() {
        return conGame.isAtEnd();
    }

    @Override
    public boolean isAtStart() {
        return conGame.isAtStart();
    }

    @Override
    public void goToFirst() {
        conGame.goToFirst();
    }

    @Override
    public void goToLast() {
        conGame.goToLast();
        // showActivePlayerIfNecessary();
    }

    @Override
    public boolean canTogglePause() {
        return conGame.canTogglePause();
    }

    @Override
    public void onError(String errorMessage) {
        RenderFacade.getInstance().gameError(errorMessage);
    }

    @Override
    public void reset() {
        goToFirst();
        notifiedOnGameEnded = false;
    }
}
