package sc.player2016.logic;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import sc.player2016.Starter;
import sc.plugin2016.GameState;
import sc.plugin2016.IGameHandler;
import sc.plugin2016.Move;
import sc.plugin2016.Player;
import sc.plugin2016.PlayerColor;
import sc.plugin2016.util.InvalidMoveException;
import sc.shared.GameResult;

/**
 * Das Herz des Simpleclients: Eine sehr simple Logik, die ihre Zuege zufaellig
 * waehlt, aber gueltige Zuege macht. Ausserdem werden zum Spielverlauf
 * Konsolenausgaben gemacht.
 */
public class NotSoSimpleLogic implements IGameHandler {

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
	public NotSoSimpleLogic(Starter client) {
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
	 */
	@Override
	public void onRequestAction() {
		System.out.println("*** Es wurde ein Zug angefordert");
			List<Move> possibleMoves = gameState.getPossibleMoves();
			System.out.println("*** sende zug: ");
			Move selection = moveWithMostPoints(possibleMoves);
			System.out.println("*** setze Strommast auf x="
					+ selection.getX() + ", y="
					+ selection.getY());
			sendAction(selection);
	}

	private Move moveWithMostPoints(List<Move> possibleMoves) {
    int maxPoints = -10000;
    int newMaxPoints = -10000;
    Move bestMove = new Move();
    GameState nextGameState;
    for(Move m : possibleMoves) {
      try {
        nextGameState = (GameState) gameState.clone();
        //nextGameState.prepareNextTurn(possibleMoves.get(i));
        Move currentMove = (Move) m;
          nextGameState.getBoard().put(currentMove.getX(), currentMove.getY(), nextGameState.getCurrentPlayer());
          newMaxPoints = nextGameState.getPointsForPlayer(nextGameState.getCurrentPlayerColor());
        if(newMaxPoints >= maxPoints) {
            bestMove = m;
        }
      } catch (CloneNotSupportedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return bestMove;
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
