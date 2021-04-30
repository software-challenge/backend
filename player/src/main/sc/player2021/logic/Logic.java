package sc.player2021.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.TwoPlayerGameState;
import sc.framework.plugins.Player;
import sc.player.IGameHandler;
import sc.plugin2021.GameState;
import sc.plugin2021.Move;
import sc.plugin2021.util.GameRuleLogic;
import sc.shared.GameResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Das Herz des Clients:
 * Eine sehr simple Logik, die ihre Zuege zufaellig waehlt,
 * aber gueltige Zuege macht.
 * <p>
 * Ausserdem werden zum Spielverlauf Konsolenausgaben gemacht.
 */
public class Logic implements IGameHandler {
  private static final Logger log = LoggerFactory.getLogger(Logic.class);

  /** Aktueller Spielstatus. */
  private GameState gameState;

  public void onGameOver(GameResult data, String errorMessage) {
    log.info("Das Spiel ist beendet, Ergebnis: {}", data);
  }

  @Override
  public Move calculateMove() {
    long startTime = System.currentTimeMillis();
    Player player = gameState.getCurrentPlayer();
    log.info("Es wurde ein Zug von {} angefordert.", player);

    List<Move> possibleMoves = new ArrayList<>(GameRuleLogic.getPossibleMoves(gameState));
    Move move = possibleMoves.get((int) (Math.random() * possibleMoves.size()));

    log.info("Sende {} nach {}ms.", move, System.currentTimeMillis() - startTime);
    return move;
  }

  @Override
  public void onUpdate(TwoPlayerGameState<?> gameState) {
    this.gameState = (GameState) gameState;
    log.info("Zug: {} Dran: {}", gameState.getTurn(), gameState.getCurrentPlayer().getColor());
  }

}
