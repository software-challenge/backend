package sc.player2019.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.player2019.Starter;
import sc.plugin2019.GameState;
import sc.plugin2019.IGameHandler;
import sc.plugin2019.Move;
import sc.plugin2019.Player;
import sc.shared.GameResult;
import sc.shared.PlayerColor;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

/**
 * Das Herz des Simpleclients: Eine sehr simple Logik, die ihre Zuege zufaellig
 * waehlt, aber gueltige Zuege macht. Ausserdem werden zum Spielverlauf
 * Konsolenausgaben gemacht.
 */
public class Logic implements IGameHandler {

  private Starter client;
  private GameState gameState;
  private Player currentPlayer;

  private static final Logger log = LoggerFactory.getLogger(Logic.class);
  /*
   * Klassenweit verfuegbarer Zufallsgenerator der beim Laden der klasse
   * einmalig erzeugt wird und darn immer zur Verfuegung steht.
   */
  private static final Random rand = new SecureRandom();

  /**
   * Erzeugt ein neues Strategieobjekt, das zufaellige Zuege taetigt.
   *
   * @param client Der Zugrundeliegende Client der mit dem Spielserver
   *               kommunizieren kann.
   */
  public Logic(Starter client) {
    this.client = client;
  }

  /**
   * {@inheritDoc}
   */
  public void gameEnded(GameResult data, PlayerColor color,
                        String errorMessage) {
    log.info("Das Spiel ist beendet.");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onRequestAction() {
    long startTime = System.nanoTime();
    log.info("Es wurde ein Zug angefordert.");
    ArrayList<Move> possibleMove = gameState.getPossibleMoves(); // Enthält mindestens ein Element

    sendAction(possibleMove.get((int)(Math.random()*possibleMove.size())));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onUpdate(Player player, Player otherPlayer) {
    currentPlayer = player;
    log.info("Spielerwechsel: " + player.getPlayerColor());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onUpdate(GameState gameState) {
    this.gameState = gameState;
    currentPlayer = gameState.getCurrentPlayer();
    log.info("Das Spiel geht voran: Zug: {}", gameState.getTurn());
    log.info("Spieler: {}", currentPlayer.getPlayerColor());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void sendAction(Move move) {
    client.sendMove(move);
  }

}
