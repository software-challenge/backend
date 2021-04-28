package sc.player2021.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.TwoPlayerGameState;
import sc.framework.plugins.Player;
import sc.player.IGameHandler;
import sc.plugin2021.*;
import sc.plugin2021.util.GameRuleLogic;
import sc.shared.GameResult;

import java.util.List;
import java.util.ArrayList;

/**
 * Das Herz des Clients:
 * Eine sehr simple Logik, die ihre Zuege zufaellig waehlt,
 * aber gueltige Zuege macht.
 *
 * Ausserdem werden zum Spielverlauf Konsolenausgaben gemacht.
 */
public class Logic implements IGameHandler {
  private static final Logger log = LoggerFactory.getLogger(Logic.class);

  /** Aktueller Spielstatus. */
  private GameState gameState;
  /** Aktueller eigener Spieler. */
  private Player currentPlayer;

  public void gameEnded(GameResult data, String errorMessage) {
    log.info("Das Spiel ist beendet, Ergebnis: {}", data);
  }

  @Override
  public Move calculateMove() {
    long startTime = System.currentTimeMillis();
    log.info("Es wurde ein Zug angefordert.");
    List<Move> possibleMoves = new ArrayList<>(GameRuleLogic.getPossibleMoves(gameState));
    return possibleMoves.get((int) (Math.random() * possibleMoves.size()));
  }

  @Override
  public void onUpdate(Player player, Player otherPlayer) {
    currentPlayer = player;
  }

  @Override
  public void onUpdate(TwoPlayerGameState<?> gameState) {
    this.gameState = (GameState) gameState;
    log.info("Zug: {} Dran: {}", gameState.getTurn(), gameState.getCurrentPlayer().getColor());
  }

}
