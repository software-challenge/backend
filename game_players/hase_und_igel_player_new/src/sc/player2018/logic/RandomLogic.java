package sc.player2018.logic;

import java.lang.reflect.Array;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.player2018.Starter;
import sc.plugin2018.*;
import sc.plugin2018.util.Constants;
import sc.plugin2018.util.GameRuleLogic;
import sc.shared.PlayerColor;
import sc.shared.InvalidMoveException;
import sc.shared.GameResult;

/**
 * Das Herz des Simpleclients: Eine sehr simple Logik, die ihre Zuege zufaellig
 * waehlt, aber gueltige Zuege macht. Ausserdem werden zum Spielverlauf
 * Konsolenausgaben gemacht.
 */
public class RandomLogic implements IGameHandler {

	private Starter client;
	private GameState gameState;
	private Player currentPlayer;

  private static final Logger log = LoggerFactory.getLogger(RandomLogic.class);
	/*
	 * Klassenweit verfuegbarer Zufallsgenerator der beim Laden der klasse
	 * einmalig erzeugt wird und darn immer zur Verfuegung steht.
	 */
	private static final Random rand = new SecureRandom();

	/**
	 * Erzeugt ein neues Strategieobjekt, das zufaellige Zuege taetigt.
	 *
	 * @param client
	 *            Der Zugrundeliegende Client der mit dem Spielserver
	 *            kommunizieren kann.
	 */
	public RandomLogic(Starter client) {
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
	public void onRequestAction(){
    long startTime = System.nanoTime();
    log.info("Es wurde ein Zug angefordert.");
    ArrayList<Move> possibleMove = gameState.getPossibleMoves(); // Enthält mindestens ein Element
    ArrayList<Move> saladMoves = new ArrayList<>();
    ArrayList<Move> winningMoves = new ArrayList<>();
    ArrayList<Move> selectedMoves = new ArrayList<>();

    int index = currentPlayer.getFieldIndex();
    for (Move move : possibleMove) {
      for (Action action : move.actions) {
        if (action instanceof Advance) {
          Advance advance = (Advance) action;
          if (advance.getDistance() + index == Constants.NUM_FIELDS - 1) {
            // Zug ins Ziel
            winningMoves.add(move);

          } else if (gameState.getBoard().getTypeAt(advance.getDistance() + index) == FieldType.SALAD) {
            // Zug auf Salatfeld
            saladMoves.add(move);
          } else {
            // Ziehe Vorwärts, wenn möglich
            selectedMoves.add(move);
          }
        } else if (action instanceof Card) {
          Card card = (Card) action;
          if (card.getType() == CardType.EAT_SALAD) {
            // Zug auf Hasenfeld und danch Salatkarte
            saladMoves.add(move);
          } // Muss nicht zusätzlich ausgewählt werden, wurde schon durch Advance ausgewählt
        } else if (action instanceof ExchangeCarrots) {
          ExchangeCarrots exchangeCarrots = (ExchangeCarrots) action;
          if (exchangeCarrots.getValue() == 10 && currentPlayer.getCarrots() < 30 && index < 40
                  && !(currentPlayer.getLastNonSkipAction() instanceof ExchangeCarrots)) {
            // Nehme nur Karotten auf, wenn weniger als 30 und nur am Anfang und nicht zwei mal hintereinander
            selectedMoves.add(move);
          } else if (exchangeCarrots.getValue() == -10 && currentPlayer.getCarrots() > 30 && index >= 40) {
            // abgeben von Karotten ist nur am Ende sinnvoll
            selectedMoves.add(move);
          }
        } else if (action instanceof FallBack) {
          if (index > 56 /*letztes Salatfeld*/ && currentPlayer.getSalads() > 0) {
            // Falle nur am Ende (index > 56) zurück, außer du musst noch einen Salat loswerden
            selectedMoves.add(move);
          } else if (index <= 56 && index - gameState.getPreviousFieldByType(FieldType.HEDGEHOG, index) < 5) {
            // Falle zurück, falls sich Rückzug lohnt (nicht zu viele Karotten aufnehmen)
            selectedMoves.add(move);
          }
        } else {
          // Füge Salatessen oder Skip hinzu
          selectedMoves.add(move);
        }
      }
    }
    Move move;
    if (!winningMoves.isEmpty()) {
      log.info("Sende Gewinnzug");
      move = winningMoves.get(rand.nextInt(winningMoves.size()));
    } else if (!saladMoves.isEmpty()) {
      // es gibt die Möglichkeit einen Salat zu essen
      log.info("Sende Zug zum Salatessen");
      move = saladMoves.get(rand.nextInt(saladMoves.size()));
    } else if (!selectedMoves.isEmpty()) {
      move = selectedMoves.get(rand.nextInt(selectedMoves.size()));
    } else {
      move = possibleMove.get(rand.nextInt(possibleMove.size()));
    }
    move.orderActions();
    log.info("Sende zug {}", move);
    long nowTime = System.nanoTime();
    sendAction(move);
    log.warn("Time needed for turn: {}", (nowTime - startTime) / 1000000);
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
