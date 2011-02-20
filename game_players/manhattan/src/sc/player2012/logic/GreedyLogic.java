package sc.player2012.logic;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import sc.player2012.Starter;
import sc.plugin2012.BuildMove;
import sc.plugin2012.GameState;
import sc.plugin2012.IGameHandler;
import sc.plugin2012.Move;
import sc.plugin2012.MoveType;
import sc.plugin2012.Player;
import sc.plugin2012.PlayerColor;
import sc.plugin2012.SelectMove;
import sc.plugin2012.Tower;
import sc.plugin2012.util.Constants;
import sc.shared.GameResult;

public class GreedyLogic implements IGameHandler {

	private Starter client;
	private GameState gameState;
	private Player currentPlayer;

	/*
	 * Klassenweit verfuegbarer Zufallsgenerator der beim Laden der klasse
	 * einmalig erzeugt wird und dann immer zur Verfuegung steht.
	 */
	private static final Random rand = new SecureRandom();

	/**
	 * Erzeugt ein neues Stratefieobjekt, dass zufaellige Zuege taetigt.
	 * 
	 * @param client
	 *            Der Zugrundeliegende Client der mit dem Spielserver
	 *            kommunizieren kann.
	 */
	public GreedyLogic(Starter client) {
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

		if (gameState.getCurrentMoveType() == MoveType.SELECT) {

			/*
			 * Ein Integer-Array vorbereiten in dem an der i-ten Position
			 * gespeichert wird, wie viele Segmente der Groesse (i+1) in diesem
			 * SelectMove angefordert werden sollen. Im Integer Size wird lokal
			 * gespeischet wie viele Segmente insgesammt angefordert werden.
			 */
			int[] selections = new int[Constants.MAX_SEGMENT_SIZE];
			int size = 0;

			/*
			 * Einen zufaelligen Wert s aus dem Bereich 0, ..., MAX_SEGMENT_SIZE
			 * - 1 erzeugen und Pruefen ob noch ein weiteres Segment dieser
			 * Groesse zur Verfuegung steht. Falls ja, wird das Array selections
			 * an der Stelle s und der Wert size jeweils um 1 erhoeht. Dies wird
			 * so oft wiederholt, bis insgesamt SELECTION_SIZE Segmente gewaehlt
			 * wurden.
			 */
			while (size < Constants.SELECTION_SIZE) {
				int s = rand.nextInt(Constants.MAX_SEGMENT_SIZE);
				System.out.println("    Teste Segment der Groesse " + (s + 1));
				if (currentPlayer.getSegment(s + 1).getRetained() > selections[s]) {
					System.out.println("    Waehle Segment der Groesse " + (s + 1));
					selections[s]++;
					size++;
				}
			}

			/*
			 * informationen uber die gewaehlten Segmente auf der Konsole
			 * ausgeben, einen SelectMove mit der getroffenen Auswahl erzeugen
			 * und den Zug abschicken.
			 */
			System.out.print("*** sende zug: SELECT ");
			for (int i = 0; i < selections.length; i++) {
				System.out.print(selections[i] + "x" + i + " ");
			}
			System.out.println();
			sendAction(new SelectMove(selections));

		} else {

			PlayerColor me = gameState.getCurrentPlayerColor();
			List<BuildMove> moves = gameState.getPossibleMoves();
			int[] scores = new int[moves.size()];
			int[] owners = new int[Constants.CITIES];

			int maxHeigth = 0;
			PlayerColor maxOwner = null;
			for (Tower tower : gameState.getTowers()) {
				owners[tower.city] += (tower.getOwner() == me ? 1 : -1);
				if (tower.getHeight() > maxHeigth) {
					maxHeigth = tower.getHeight();
					maxOwner = tower.getOwner();
				}
			}

			int i = 0;
			for (BuildMove move : moves) {
				Tower tower = gameState.getTower(move.city, move.slot);
				if (tower.getHeight() + move.size > maxHeigth) {
					scores[i] += 4;
					if (maxOwner != me) {
						scores[i] += 4;
					}
				}
				if (tower.getHeight() + move.size == maxHeigth) {
					scores[i] += 2;
					if (maxOwner != me) {
						scores[i] += 2;
					}
				}
				if (tower.getOwner() != me) {
					scores[i] += 3;
				}
				if (owners[tower.city] < 0) {
					scores[i] += 7;
				}
				if (owners[tower.city] == 0) {
					scores[i] += 4;
				}
				i++;
			}

			int max = 0;
			for (i = 0; i < scores.length; i++) {
				if (scores[i] > scores[max]) {
					max = i;
				}
			}

			BuildMove move = moves.get(max);
			System.out.print("*** sende zug: BUILD city = " + move.city);
			System.out.println(", slot = " + move.slot + ", size = " + move.size);
			sendAction(move);
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
