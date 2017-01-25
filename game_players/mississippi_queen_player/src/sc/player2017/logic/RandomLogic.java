package sc.player2017.logic;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.player2017.Starter;
import sc.plugin2017.Acceleration;
import sc.plugin2017.Advance;
import sc.plugin2017.Direction;
import sc.plugin2017.Field;
import sc.plugin2017.FieldType;
import sc.plugin2017.GameState;
import sc.plugin2017.IGameHandler;
import sc.plugin2017.Move;
import sc.plugin2017.Player;
import sc.plugin2017.PlayerColor;
import sc.plugin2017.Turn;
import sc.plugin2017.util.InvalidMoveException;
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
	@Override
	public void gameEnded(GameResult data, PlayerColor color,
			String errorMessage) {
		log.info("Das Spiel ist beendet.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onRequestAction(){
	  throw new NullPointerException();
	  /*
		log.info("Es wurde ein Zug angefordert.");
    Move move = new Move();
    // Setze die für perform benötigen Attribute
    currentPlayer.setMovement(currentPlayer.getSpeed());
    currentPlayer.setFreeAcc(1);
    currentPlayer.setFreeTurns(gameState.isFreeTurn() ? 2 : 1);

    List<Move> possibleMoves = new ArrayList<Move>();

    // Sandbank
    if(currentPlayer.getField(gameState.getBoard()).getType() == FieldType.SANDBANK) {
      Field fieldInFront = currentPlayer.getField(gameState.getBoard())
          .getFieldInDirection(currentPlayer.getDirection(), gameState.getBoard());
      Field fieldBehind = currentPlayer.getField(gameState.getBoard())
          .getFieldInDirection(currentPlayer.getDirection().getOpposite(), gameState.getBoard());
      if(fieldInFront != null && fieldInFront.isPassable()) {
        move.actions.add(new Advance(1,0));
      } else if (fieldBehind != null && fieldBehind.isPassable()){
        move.actions.add(new Advance(-1, 0));
      } else {
        // Wir sind auf dieser Sandbank gefangen und können keinen gültigen Zug mehr ausführen.
      }
      log.info("Bin auf Sandbank, sende Zug {}", move);
      sendAction(move);
      return;
    }
    // Zuege in alle Richtungen durchprobieren
    for (Direction direction : Direction.values()) {
      // try moves for current speed
      for (Advance action : gameState.getPossibleMovesInDirection(currentPlayer, currentPlayer.getSpeed(), direction, currentPlayer.getCoal())) {
        Move newMove = new Move();
        int index = 0;
        if (currentPlayer.getDirection() != direction) {
          newMove.actions.add(new Turn(currentPlayer.getDirection().turnToDir(direction), index++));
        }
        newMove.actions.add(new Advance(action.distance, index++));
        possibleMoves.add(newMove);
      }
      // try moves when accelerating or decelerating (depending if we are currently at speed 1 or 2)
      int otherSpeed = currentPlayer.getSpeed() == 1 ? 2 : 1;
      for (Advance action : gameState.getPossibleMovesInDirection(currentPlayer, otherSpeed, direction, currentPlayer.getCoal())) {
        Move newMove = new Move();
        int index = 0;
        newMove.actions.add(new Acceleration(otherSpeed - currentPlayer.getSpeed(), index++));
        if (currentPlayer.getDirection() != direction) {
          newMove.actions.add(new Turn(currentPlayer.getDirection().turnToDir(direction), index++));
        }
        newMove.actions.add(new Advance(action.distance, index++));
        possibleMoves.add(newMove);
      }
    }
    // Finde Zug mit meisten Punkten
    int maxPoints = 0;
    GameState clone = null;
    for (Move possibleMove : possibleMoves) {
      // Klone gameState
      try {
        clone = gameState.clone();
      } catch (CloneNotSupportedException e) {
        log.error("Problem mit dem Klonen des GameState.", e);
      }
      try {
        possibleMove.perform(clone, clone.getCurrentPlayer());

        int points = clone.getPointsForPlayer(clone.getCurrentPlayerColor());
        if(points > maxPoints) {
          maxPoints = points;
          move = possibleMove;
        }
      } catch (InvalidMoveException e) {
        log.debug("Gefundener Zug ist ungültig:", e.getMessage());
      }
    }
    move.orderActions();
    log.info("Sende zug {}", move);
    sendAction(move);
    */
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
