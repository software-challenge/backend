package sc.player2013;

import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.Random;

import sc.plugin2013.BackwardMove;
import sc.plugin2013.ForwardMove;
import sc.plugin2013.GameState;
import sc.plugin2013.IGameHandler;
import sc.plugin2013.Move;
import sc.plugin2013.MoveContainer;
import sc.plugin2013.Player;
import sc.plugin2013.PlayerColor;
import sc.plugin2013.util.InvalidMoveException;
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
	 * einmalig erzeugt wird und dann immer zur Verfuegung steht.
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
		long currentTime = System.currentTimeMillis();
		System.out.println("*** Es wurde ein Zug angefordert");
		// Erstelle einen neuen MoveContainer, welcher unsere 3 Teilzüge
		// beherbergt
		MoveContainer moveC = new MoveContainer();
		// Schleife die 3 mal durchlaufen wird. i wird jedes mal erhöht
		for (int i = 0; i < 3; i++) {
			// Liste der verfügbaren Züge
			LinkedList<Move> possibleMoves = (LinkedList<Move>) gameState
					.getPossibleMoves();
			System.out.println("*** Anzahl der möglichen Züge:"
					+ possibleMoves.size());
			Move move;
			// Wenn es mögliche Züge gibt:
			if (possibleMoves.size() > 0) {
				// Wähle einen davon zufällig aus.
				move = possibleMoves.get(rand.nextInt(possibleMoves.size()));

				// Je nachdem welcher Zugtyp vorliegt, werden unterschiedliche
				// DebugHints hinzugefügt
				if (move.getClass().equals(BackwardMove.class)) {
					BackwardMove bMove = (BackwardMove) move;
					// Konsolenausgabe
					System.out
							.println("*** Führe Rückwärtstzug aus - Feld mit Index: "
									+ bMove.fieldIndex);
					bMove.addHint("Random Move Backward");
					bMove.addHint("Noch ein Hint");
				} else if (move.getClass().equals(ForwardMove.class)) {
					ForwardMove fMove = (ForwardMove) move;
					// Konsolenausgabe
					System.out
							.println("*** Führe Vorwärtszug aus - Feld mit Index: "
									+ fMove.fieldIndex
									+ " - Symbol : "
									+ fMove.symbol);
					fMove.addHint("Random Move Forward");
				}
			} else {
				move = null;
			}
			// Hinzufügen des Zuges zum Container
			moveC.addMove(move);
			// Lokalen GameState auf den Move aktualisieren.
			if (move != null) {
				try {
					// Führt den Teilzug aus, hier wird überprüft ob der Zug
					// gültig ist.
					move.perform(gameState, gameState.getCurrentPlayer());
					// Aktualisiert die Punktzahl
					gameState.prepareNextTurn(move);
				} catch (InvalidMoveException e) {
					System.out.println("*** Ungültiger Zug ausgeführt");
					e.printStackTrace();
				}
			}
		}

		// Sende den Container mit allen durchgeführten Zügen.
		sendAction(moveC);
		System.out.println("Zugzeit:" + (System.currentTimeMillis() - currentTime));
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
	public void sendAction(MoveContainer move) {
		client.sendMove(move);
	}

}
