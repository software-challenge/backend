package sc.player2015.logic;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import sc.player2015.Starter;
import sc.plugin2015.NullMove;
import sc.plugin2015.RunMove;
import sc.plugin2015.GameState;
import sc.plugin2015.IGameHandler;
import sc.plugin2015.Move;
import sc.plugin2015.MoveType;
import sc.plugin2015.Player;
import sc.plugin2015.PlayerColor;
import sc.plugin2015.SetMove;
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
	 */
	@Override
	public void onRequestAction() {
		System.out.println("*** Es wurde ein Zug angefordert");
		if (gameState.getCurrentMoveType() == MoveType.SET) {
			List<SetMove> possibleMoves = gameState.getPossibleSetMoves();
			System.out.println("*** sende zug: SET ");
			SetMove selection = possibleMoves.get(rand.nextInt(possibleMoves
					.size()));
			System.out.println("*** setze Pinguin auf x="
					+ selection.getSetCoordinates()[0] + ", y="
					+ selection.getSetCoordinates()[1]);
			sendAction(selection);
		} else {
			List<Move> possibleMoves = gameState.getPossibleMoves();
			System.out.println("*** sende zug: RUN ");
			Move selection = possibleMoves.get(rand.nextInt(possibleMoves
					.size()));
			if (selection.getClass() == NullMove.class)
				System.out.println("*** Ich setze aus.");
			else {
				RunMove runSelection = (RunMove) selection;
				System.out.println("*** bewege Pinguin von x="
						+ runSelection.fromX + ", y=" + runSelection.fromY
						+ " auf x=" + runSelection.toX + ", y=" + runSelection.toY);
			}
			sendAction(selection);
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
