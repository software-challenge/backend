package sc.player2021.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.framework.plugins.Player;
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

  /** Internet Client, der mit dem Spielserver kommuniziert. */
  private final AbstractClient client;
  /** Aktueller Spielstatus. */
  private GameState gameState;
  /** Aktueller eigener Spieler. */
  private Player currentPlayer;

  /** Erzeugt eine neue Instanz dieser Strategie, die über den mitgegebenen Client Züge absendet. */
  public Logic(AbstractClient client) {
    this.client = client;
  }

  /** {@inheritDoc} */
  public void gameEnded(GameResult data, Team color, String errorMessage) {
    log.info("Das Spiel ist beendet.");
  }

  /** {@inheritDoc} */
  @Override
  public void onRequestAction() {
    long startTime = System.currentTimeMillis();
    log.info("Es wurde ein Zug angefordert.");
    List<Move> possibleMoves = new ArrayList<>(GameRuleLogic.getPossibleMoves(gameState));
    sendAction(possibleMoves.get((int) (Math.random() * possibleMoves.size())));
  }

  /** {@inheritDoc} */
  @Override
  public void onUpdate(Player player, Player otherPlayer) {
    currentPlayer = player;
  }

  /** {@inheritDoc} */
  @Override
  public void onUpdate(GameState gameState) {
    this.gameState = gameState;
    log.info("Zug: {} Dran: {}", gameState.getTurn(), gameState.getCurrentPlayer().getColor());
  }

  /** {@inheritDoc} */
  @Override
  public void sendAction(Move move) {
    client.sendMove(move);
  }

}
