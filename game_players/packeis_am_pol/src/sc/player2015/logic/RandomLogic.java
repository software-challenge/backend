package sc.player2015.logic;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import sc.player2015.Starter;
import sc.plugin2015.RunMove;
import sc.plugin2015.GameState;
import sc.plugin2015.IGameHandler;
import sc.plugin2015.Move;
import sc.plugin2015.MoveType;
import sc.plugin2015.Player;
import sc.plugin2015.PlayerColor;
import sc.plugin2015.SetMove;
import sc.plugin2015.util.Constants;
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
		if(gameState.getCurrentMoveType() == MoveType.SET) {
			List<SetMove> possibleMoves = gameState.getPossibleSetMoves();
			System.out.println("*** sende zug: SET ");
			SetMove selection = possibleMoves.get(rand.nextInt(possibleMoves.size()));
			System.out.println("*** setze Pinguin auf x=" + selection.getSet()[0] + ", y=" + selection.getSet()[1]);
			sendAction(selection);
		} else {
			List<RunMove> possibleMoves = gameState.getPossibleMoves();
			System.out.println("*** sende zug: RUN ");
			RunMove selection = possibleMoves.get(rand.nextInt(possibleMoves.size()));
			System.out.println("*** bewege Pinguin von x=" + selection.fromX + ", y=" + selection.fromY +" auf x=" + selection.toX + ", y=" + selection.toY);
			sendAction(selection);			
		}

		/*if (gameState.getCurrentMoveType() == MoveType.SELECT) {

			/*
			 * Ein Integer-Array vorbereiten in dem an der i-ten Position
			 * gespeichert wird, wie viele Segmente der Groesse (i+1) in diesem
			 * SelectMove angefordert werden sollen. Im Integer size wird lokal
			 * gespeichert, wie viele Segmente insgesammt angefordert werden.
			 *
			int[] selections = new int[Constants.MAX_SEGMENT_SIZE];
			int size = 0;

			/*
			 * Einen zufaelligen Wert s aus dem Bereich [0,MAX_SEGMENT_SIZE-1]
                         * erzeugen und pruefen, ob noch ein weiteres Segment dieser
			 * Groesse zur Verfuegung steht. Falls ja, wird das Array selections
			 * an der Stelle s und der Wert size jeweils um 1 erhoeht. Dies wird
			 * so oft wiederholt, bis insgesamt SELECTION_SIZE Segmente gewaehlt
			 * wurden.
			 *
			while (size < Constants.SELECTION_SIZE) {
				int s = rand.nextInt(Constants.MAX_SEGMENT_SIZE);
				System.out.println("    Teste Segment der Groesse " + (s+1));
				if (currentPlayer.getSegment(s + 1).getRetained() > selections[s]) {
					System.out.println("    Waehle Segment der Groesse " + (s+1));
					selections[s]++;
					size++;
				}
			}

			/*
			 * informationen uber die gewaehlten Segmente auf der Konsole
			 * ausgeben, einen SelectMove mit der getroffenen Auswahl erzeugen
			 * und den Zug abschicken.
			 *
			System.out.print("*** sende zug: SELECT ");
			for (int i = 0; i < selections.length; i++) {
				System.out.print(selections[i] + "x" + i + " ");
			}
			System.out.println();
			sendAction(new SelectMove(selections));

		} else {
			/*
			 * Hole Liste aller moeglichen BuildMoves und waehle einen
			 * zufaelligen Zug aus dieser Liste.
                         * gibt die Informationen zu diesem Zug auf der Konsole aus
                         * und sendet den Zug an den Spielserver
			 *
			List<BuildMove> moves = gameState.getPossibleMoves();
			BuildMove move = moves.get(rand.nextInt(moves.size()));
			System.out.print("*** sende zug: BUILD city = " + move.city);
			System.out.println(", slot = " + move.slot + ", size = " + move.size);
			sendAction(move);
		}*/

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
	public void sendAction(Move move) {
		client.sendMove(move);
	}

}
