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
 * Das Herz des Simpleclients: Eine sehr simple Logik,
 * die ihre Zuege zufaellig waehlt, aber gueltige Zuege macht.
 * Ausserdem werden zum Spielverlauf Konsolenausgaben gemacht.
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
	public void gameEnded(GameResult data, PlayerColor color, String errorMessage) {

		System.out.println("*** Das Spiel ist beendet");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onRequestAction() {
		System.out.println("*** Es wurde ein Zug angefordert");
		// wähle eine zufällige Anzahl von Zügen zwischen 0 und 3
		int numMoves = rand.nextInt(4);
		System.out.println("*** Anzahl der diesmal ausgewählten Züge: " + numMoves);
		MoveContainer moveC = new MoveContainer();
		for(int i = 0 ; i < 3 ; i++){
			// Liste der verfügbaren Züge
			LinkedList<Move> possibleMoves = (LinkedList<Move>) gameState.getPossibleMoves();
			System.out.println("*** Anzahl der möglichen Züge:"  + possibleMoves.size());
			// Zufällige Auswahl eines Zuges
			Move move = possibleMoves.get(rand.nextInt(possibleMoves.size()));
			if(move.getClass().equals(BackwardMove.class)){
				BackwardMove bMove = (BackwardMove) move;
				System.out.println("*** Führe Rückwärtstzug aus - Feld mit Index: "  + bMove.fieldIndex);
				bMove.addHint("Random Move Backward");
				bMove.addHint("Noch ein Hint");
			} else if(move.getClass().equals(ForwardMove.class)){
				ForwardMove fMove = (ForwardMove) move;
				System.out.println("*** Führe Vorwärtszug aus - Feld mit Index: "  + fMove.fieldIndex + " - Symbol : " + fMove.symbol);
				fMove.addHint("Random Move Forward");
			}
			// Hinzufügen des Zuges zum Container
			moveC.addMove(move);
			// Lokalen GameState auf den Move aktualisieren.
			try {
				move.perform(gameState, gameState.getCurrentPlayer());
				gameState.prepareNextTurn(move);
			} catch (InvalidMoveException e) {
				System.out.println("*** Ungültiger Zug ausgeführt");
				e.printStackTrace();
			}			
		}
		
		// Sende den Container mit allen durchgeführten Zügen.
		sendAction(moveC);
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

		System.out.print("*** Das Spiel geht vorran: Zug = " + gameState.getTurn());
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
