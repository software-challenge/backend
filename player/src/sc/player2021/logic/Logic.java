package sc.player2021.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.framework.plugins.Player;
import sc.player2021.Starter;
import sc.plugin2021.GameState;
import sc.plugin2021.IGameHandler;
import sc.plugin2021.Move;
import sc.plugin2021.Team;
import sc.plugin2021.util.GameRuleLogic;
import sc.shared.GameResult;
import sc.api.plugins.ITeam;

import java.util.List;
import java.util.ArrayList;

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
  public void gameEnded(GameResult data, Team color, String errorMessage) {
    log.info("Das Spiel ist beendet.");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onRequestAction() {
    long startTime = System.currentTimeMillis();
    log.info("Es wurde ein Zug angefordert.");
    List possibleMoves = new ArrayList<>(GameRuleLogic.getPossibleMoves(gameState));
    sendAction((Move) possibleMoves.get((int) (Math.random() * possibleMoves.size())));
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
