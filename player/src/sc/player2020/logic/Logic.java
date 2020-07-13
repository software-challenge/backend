package sc.player2020.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.framework.plugins.Player;
import sc.player2020.Starter;
import sc.plugin2020.GameState;
import sc.plugin2020.IGameHandler;
import sc.plugin2020.Move;
import sc.plugin2020.util.GameRuleLogic;
import sc.shared.GameResult;
import sc.shared.ITeam;

import java.util.List;

/**
 * Das Herz des Clients:
 * Eine sehr simple Logik, die ihre Zuege zufaellig waehlt,
 * aber gueltige Zuege macht.
 * Ausserdem werden zum Spielverlauf Konsolenausgaben gemacht.
 */
public class Logic implements IGameHandler {
  private static final Logger log = LoggerFactory.getLogger(Logic.class);

  private Starter client;
  private GameState gameState;
  private Player currentPlayer;

  /**
   * Erzeugt ein neues Strategieobjekt, das zufaellige Zuege taetigt.
   *
   * @param client Der zugrundeliegende Client, der mit dem Spielserver kommuniziert.
   */
  public Logic(Starter client) {
    this.client = client;
  }

  /**
   * {@inheritDoc}
   */
  public void gameEnded(GameResult data, ITeam color, String errorMessage) {
    log.info("Das Spiel ist beendet.");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onRequestAction() {
    long startTime = System.currentTimeMillis();
    log.info("Es wurde ein Zug angefordert.");
    List<Move> possibleMoves = GameRuleLogic.getPossibleMoves(gameState);
    sendAction(possibleMoves.isEmpty() ? null : possibleMoves.get((int) (Math.random() * possibleMoves.size())));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onUpdate(Player player, Player otherPlayer) {
    currentPlayer = player;
    log.info("Spielerwechsel: " + player.getColor());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onUpdate(GameState gameState) {
    this.gameState = gameState;
    currentPlayer = gameState.getCurrentPlayer();
    log.info("Zug: {} Spieler: {}", gameState.getTurn(), currentPlayer.getColor());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void sendAction(Move move) {
    client.sendMove(move);
  }

}
