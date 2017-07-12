package sc.player2018.logic;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.player2018.Starter;
import sc.plugin2018.*;
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
    ArrayList<Move> possibleMove = new ArrayList<>();
    ArrayList<Action> actions = new ArrayList<>();
    if (GameRuleLogic.isValidToEat(gameState)) {
      // Wenn ein Salat gegessen werden kann, muss auch ein Salat gegessen werden
      actions.add(new EatSalad());
      sendAction(new Move(actions));
      return;
    } else if (GameRuleLogic.isValidToExchangeCarrots(gameState, 10)) {
      actions.add(new ExchangeCarrots(10));
      possibleMove.add(new Move(actions));
      actions.clear();
    } else if (GameRuleLogic.isValidToExchangeCarrots(gameState, -10)) {
      actions.add(new ExchangeCarrots(-10));
      possibleMove.add(new Move(actions));
      actions.clear();
    } else if (GameRuleLogic.isValidToFallBack(gameState)) {
      actions.add(new FallBack());
      possibleMove.add(new Move(actions));
      actions.clear();
    } else {
      // Generiere mögliche Vorwärtszüge
      for (int i = 1; i < GameRuleLogic.calculateMoveableFields(currentPlayer.getCarrotsAvailable()); i++) {
        GameState clone = null;
        try {
          clone = gameState.clone();
        } catch (CloneNotSupportedException e) {
          e.printStackTrace();
        }
        // Überrüfe ob Vorwärtszug möglich ist
        if (GameRuleLogic.isValidToAdvance(clone, i)) {
          Advance tryAdvance = new Advance(i);
          try {
            tryAdvance.perform(clone);
          } catch (InvalidMoveException e) {
            // Sollte nicht passieren, da Zug valide ist
            e.printStackTrace();
            break;
          }
          actions.add(tryAdvance);
          // überprüfe, ob eine Karte gespielt werden muss/kann
          if (clone.getCurrentPlayer().mustPlayCard()) {
            possibleMove.addAll(checkForPlayableCards(gameState, actions));
          } else {
            // Füge möglichen Vorwärtszug hinzu
            possibleMove.add(new Move(actions));
          }
        }
        actions.clear();
      }
    }
    Move move;
    if (possibleMove.isEmpty()) {
      actions.add(new Skip());
      move = new Move(actions);
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
   * Überprüft für übergebenen GameState und bisher getätigte Züge,
   * ob das Ausspielen einer Karte nötig/möglich ist
   * @param state
   * @param actions
   * @return
   */
	private ArrayList<Move> checkForPlayableCards(GameState state, ArrayList<Action> actions) {
	  ArrayList<Move> possibleMove = new ArrayList<>();
    if (state.getCurrentPlayer().mustPlayCard()) { // überprüfe, ob eine Karte gespielt werden muss
      if (GameRuleLogic.isValidToPlayEatSalad(state)) {
        actions.add(new Card(CardType.EAT_SALAD, actions.size()));
        possibleMove.add(new Move(actions));

        actions.remove(new Card(CardType.EAT_SALAD, 1));
      }
      if (GameRuleLogic.isValidToPlayTakeOrDropCarrots(state, 20)) {
        actions.add(new Card(CardType.TAKE_OR_DROP_CARROTS, 20,  actions.size()));
        possibleMove.add(new Move(actions));

        actions.remove(new Card(CardType.TAKE_OR_DROP_CARROTS, 20, actions.size()));
      }
      if (GameRuleLogic.isValidToPlayTakeOrDropCarrots(state, -20)) {
        actions.add(new Card(CardType.TAKE_OR_DROP_CARROTS, -20,  actions.size()));
        possibleMove.add(new Move(actions));

        actions.remove(new Card(CardType.TAKE_OR_DROP_CARROTS, -20, actions.size()));
      }
      if (GameRuleLogic.isValidToPlayTakeOrDropCarrots(state, 0)) {
        actions.add(new Card(CardType.TAKE_OR_DROP_CARROTS, 0,  actions.size()));
        possibleMove.add(new Move(actions));

        actions.remove(new Card(CardType.TAKE_OR_DROP_CARROTS, 0,  actions.size()));
      }
      if (GameRuleLogic.isValidToPlayHurryAhead(state)) {
        actions.add(new Card(CardType.HURRY_AHEAD,  actions.size()));
        // Überprüfe ob wieder auf Hasenfeld gelandet:
        GameState clone = null;
        try {
          clone = state.clone();
        } catch (CloneNotSupportedException e) {
          e.printStackTrace();
        }
        ArrayList<Move> moves = checkForPlayableCards(clone, actions);
        if (!moves.isEmpty()) {
          possibleMove.addAll(moves);
        }

        actions.remove(new Card(CardType.HURRY_AHEAD,  actions.size()));
      }
      if (GameRuleLogic.isValidToPlayFallBack(state)) {
        actions.add(new Card(CardType.FALL_BACK,  actions.size()));
        // Überprüfe ob wieder auf Hasenfeld gelandet:
        GameState clone = null;
        try {
          clone = state.clone();
        } catch (CloneNotSupportedException e) {
          e.printStackTrace();
        }
        ArrayList<Move> moves = checkForPlayableCards(clone, actions);
        if (!moves.isEmpty()) {
          possibleMove.addAll(moves);
        }

        actions.remove(new Card(CardType.FALL_BACK,  actions.size()));
      }

    }
    return possibleMove;
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
