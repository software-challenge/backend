package sc.player2017.logic;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sc.player2017.Starter;
import sc.plugin2017.Acceleration;
import sc.plugin2017.Action;
import sc.plugin2017.GameState;
import sc.plugin2017.IGameHandler;
import sc.plugin2017.Move;
import sc.plugin2017.Player;
import sc.plugin2017.PlayerColor;
import sc.plugin2017.Step;
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

		System.out.println("*** Das Spiel ist beendet");
	}

	/**
	 * {@inheritDoc}
	 * @throws CloneNotSupportedException 
	 */
	@Override
	public void onRequestAction(){
		System.out.println("*** Es wurde ein Zug angefordert");
    Move move = null;
    Random rnd = new Random();
    List<Move> possibleMoves = new ArrayList<Move>();
    int movement = currentPlayer.getSpeed();
    // add all moves for simple client:
    // only Forward
    if(currentPlayer.getSpeed() == 1) {
      Move newMove = new Move();
      newMove.actions.add(new Acceleration(1,0));
      newMove.actions.add(new Step(2,1));
      possibleMoves.add(newMove);
      List<Action> actions = new ArrayList<Action>();
      actions.add(new Step(2,0));
      possibleMoves.add(new Move(actions));
    } else {
      Move newMove = new Move();
      newMove.actions.add(new Acceleration(1,0));
      newMove.actions.add(new Step(2,1));
    }
    List<Action> actions = new ArrayList<Action>();
    // left forward
    if(currentPlayer.getSpeed() == 1) {
      // vor links vor
      actions.add(new Acceleration(1,0));
      actions.add(new Step(1,1));
      actions.add(new Turn(-1,2));
      actions.add(new Step(1,3));
      possibleMoves.add(new Move(actions));
      // links vor
      actions = new ArrayList<Action>();
      actions.add(new Turn(-1,0));
      actions.add(new Step(1,1));
      possibleMoves.add(new Move(actions));
      actions = new ArrayList<Action>();
      // links 2 vor
      actions.add(new Acceleration(1,0));
      actions.add(new Turn(-1,1));
      actions.add(new Step(2,2));
      possibleMoves.add(new Move(actions));
      actions = new ArrayList<Action>();
      // vor rechts vor
      actions.add(new Acceleration(1,0));
      actions.add(new Step(1,1));
      actions.add(new Turn(1,2));
      actions.add(new Step(1,3));
      possibleMoves.add(new Move(actions));
      actions = new ArrayList<Action>();
      // rechts vor
      actions.add(new Turn(1,0));
      actions.add(new Step(1,1));
      possibleMoves.add(new Move(actions));
      actions = new ArrayList<Action>();
   // rechts 2 vor
      actions.add(new Acceleration(1,0));
      actions.add(new Turn(1,1));
      actions.add(new Step(2,2));
      possibleMoves.add(new Move(actions));
      actions = new ArrayList<Action>();
    } else {
   // vor links vor
      actions.add(new Step(1,0));
      actions.add(new Turn(-1,1));
      actions.add(new Step(1,2));
      possibleMoves.add(new Move(actions));
      // links vor
      actions = new ArrayList<Action>();
      actions.add(new Acceleration(-1,0));
      actions.add(new Turn(-1,1));
      actions.add(new Step(1,2));
      possibleMoves.add(new Move(actions));
      actions = new ArrayList<Action>();
      // links 2 vor
      actions.add(new Turn(-1,0));
      actions.add(new Step(2,1));
      possibleMoves.add(new Move(actions));
      actions = new ArrayList<Action>();
      // vor rechts vor
      actions.add(new Step(1,0));
      actions.add(new Turn(1,1));
      actions.add(new Step(1,2));
      possibleMoves.add(new Move(actions));
      actions = new ArrayList<Action>();
      // rechts vor
      actions.add(new Acceleration(-1,0));
      actions.add(new Turn(1,1));
      actions.add(new Step(1,2));
      possibleMoves.add(new Move(actions));
      actions = new ArrayList<Action>();
   // rechts 2 vor
      actions.add(new Turn(1,0));
      actions.add(new Step(2,1));
      possibleMoves.add(new Move(actions));
      actions = new ArrayList<Action>();
    }
    checkMoves(possibleMoves);
    // move with coal
    if(possibleMoves.isEmpty()){
      if(movement == 1) {
        actions.add(new Turn(2,0));
        actions.add(new Step(1,1));
        possibleMoves.add(new Move(actions));
        actions = new ArrayList<Action>();
        actions.add(new Turn(-2,0));
        actions.add(new Step(1,1));
        possibleMoves.add(new Move(actions));
      } else {
        actions.add(new Acceleration(-1,0));
        actions.add(new Turn(2,1));
        actions.add(new Step(1,2));
        possibleMoves.add(new Move(actions));
        actions = new ArrayList<Action>();
        actions.add(new Acceleration(-1,0));
        actions.add(new Turn(-2,1));
        actions.add(new Step(1,2));
        possibleMoves.add(new Move(actions));
      }
    } else {
      int random = rnd.nextInt(possibleMoves.size());
      move = possibleMoves.get(random);
    }
    checkMoves(possibleMoves);
    if(possibleMoves.isEmpty()) {
      actions = new ArrayList<Action>();
      if(movement == 1) {
        actions.add(new Turn(3,0));
        actions.add(new Step(1,1));
      } else {
        actions.add(new Acceleration(-1,0));
        actions.add(new Turn(3,1));
        actions.add(new Step(1,2));
      }
      move = new Move(actions);
    } else {
      int random = rnd.nextInt(possibleMoves.size());
      move = possibleMoves.get(random);
    }
    System.out.println("*** sende zug: ");
//    System.out.println(move);
    sendAction(move);
	}

	private void checkMoves(List<Move> possibleMoves) {
    for (Move move : possibleMoves) {
      GameState clone = null;
      try {
        clone = gameState.clone();
      } catch (CloneNotSupportedException e) {
        e.printStackTrace();
      }
      try {
        move.perform(clone, currentPlayer);
      } catch (InvalidMoveException e) {
        possibleMoves.remove(move);
        e.printStackTrace();
      }
    }
    
  }

  /**
	 * {@inheritDoc}
	 */
	@Override
	public void onUpdate(Player player, Player otherPlayer) {
		currentPlayer = player;

		System.out.println("*** Spielerwechsel: " + player.getPlayerColor());

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onUpdate(GameState gameState) {
		this.gameState = gameState;
		currentPlayer = gameState.getCurrentPlayer();

		System.out.print("*** Das Spiel geht vorran: Zug = "
				+ gameState.getTurn());
		System.out.println(", Spieler = " + currentPlayer.getPlayerColor());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendAction(Move move) {
		client.sendMove(move);
	}

}
