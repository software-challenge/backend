/**
 * 
 */
package sc.player2012.logic;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
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
import sc.plugin2012.util.GameStateConverter;
import sc.shared.GameResult;

/**
 * @author Felix Dubrownik
 * @mail 	fdu@informatik.uni-kiel.de
 *
 */
public class NotSoSimpleLogic implements IGameHandler {
	
	private Starter client;
	private GameState gameState;
	private Player currentPlayer;

	private class RatedMove{
		private BuildMove buildMove;
		private GameState gameState;
		private int value = 0;
		
		public RatedMove(BuildMove buildMove, GameState gameState){
			this.buildMove = buildMove;
			this.gameState = gameState;
			rateMove();
			
		}
		
		private void rateMove(){
			int val = 0;
			int[] myOldStats = gameState.getPlayerStats(gameState.getCurrentPlayer());
//			int[] otherOldStats = gameState.getPlayerStats(gameState.getOtherPlayer());
			
			PlayerColor myColor = gameState.getCurrentPlayerColor();
			
//			System.out.println("myOldTowers: "+ myOldStats[0]);
//			System.out.println("myOldTowns: "+ myOldStats[1]);
//			System.out.println("OldHighestTower?: "+ myOldStats[2]);
			
			Tower tower = gameState.getTower(buildMove.city, buildMove.slot);
			List<Tower> towers = gameState.getTowersOfCity(buildMove.city);
			
			//neuer Turm dazu, oder Turm wegnehmen
			if(tower == null){
				val +=1;
			}else if (tower.getOwner() != gameState.getCurrentPlayerColor()){
				val +=2;
			}
			
			//Stadt übernehmen
			int city = 0;
			for(Tower t : towers){
				if(t.getOwner() != myColor){
					city -=1;
				}else if(t.getOwner() == myColor){
					city += 1;
				}
			}
			
			if(city == -1 || city == 0){
				val +=2;
			}
			
			
			//Höchster Turm
			towers = gameState.getTowers();
			Collections.sort(towers, new Comparator<Tower>() {
				@Override
				public int compare(Tower t0, Tower t1) {
					return t0.getHeight() > t1.getHeight() ? 1 : -1;
				}				
			});
			Tower HighestTower = towers.get(towers.size()-1);
			if(HighestTower.getOwner() != myColor && tower.city == HighestTower.city && tower.slot == HighestTower.slot){
				val += 6; //höchsten Turm übernehmen
			}else if(HighestTower.getHeight() < tower.getHeight() + buildMove.size && myOldStats[2] == 0){
				val +=3; //höchsten Turm bauen
			}
			
			this.value = val;
			
			//gameState.prepareNextTurn(this.buildMove); //Zug wird simuliert
			
//			int[] myStats = gameState.getPlayerStats(gameState.getOtherPlayer());
//			int[] otherStats = gameState.getPlayerStats(gameState.getCurrentPlayer());
//			
//			System.out.println("myNewTowers: "+ myStats[0]);
//			System.out.println("myNewTowns: "+ myStats[1]);
//			System.out.println("NewHighestTower?: "+ myStats[2]);
			
//			System.out.println("myPoints: " + myStats[3]);
			
//			System.out.println("City: " + buildMove.city + " Slot: " + buildMove.slot + " Size: " + buildMove.size);
//			System.out.println("OldValue : " + oldVal + " NewValue: " + this.value);
		}
	}
	/*
	 * Klassenweit verfuegbarer Zufallsgenerator der beim Laden der klasse
	 * einmalig erzeugt wird und dann immer zur Verfuegung steht.
	 */
	private static final Random rand = new SecureRandom();

	/**
	 * Erzeugt ein neues Strategieobjekt, das wohlüberlegte Züge tätigt.
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
	public void gameEnded(GameResult data, PlayerColor color, String errorMessage) {

		System.out.println("*** Das Spiel ist beendet");
		System.out.println(data.getWinners().get(0));
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
			 * SelectMove angefordert werden sollen. Im Integer size wird lokal
			 * gespeichert, wie viele Segmente insgesammt angefordert werden.
			 */
			int[] selections = new int[Constants.MAX_SEGMENT_SIZE];
			int size = 0;

			/*
			 * Einen zufaelligen Wert s aus dem Bereich [0,MAX_SEGMENT_SIZE-1]
                         * erzeugen und pruefen, ob noch ein weiteres Segment dieser
			 * Groesse zur Verfuegung steht. Falls ja, wird das Array selections
			 * an der Stelle s und der Wert size jeweils um 1 erhoeht. Dies wird
			 * so oft wiederholt, bis insgesamt SELECTION_SIZE Segmente gewaehlt
			 * wurden.
			 */
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
			 */
			System.out.print("*** sende zug: SELECT ");
			for (int i = 0; i < selections.length; i++) {
				System.out.print(selections[i] + "x" + i + " ");
			}
			System.out.println();
			sendAction(new SelectMove(selections));

		} else {
			/*
			 * Hole Liste aller Möglichen Build Moves, bewerte diese mithilfe der Zugsimulation und wähle den aus, der die meißten Punkte bringt.
			 */

			List<RatedMove> rMoves= new LinkedList<RatedMove>();
			
			for (Move move : gameState.getPossibleMoves()) {
				rMoves.add(new RatedMove((BuildMove) move, this.gameState)); //füttere die Liste mit bewerteten Zügen.
			}
			
			Collections.sort(rMoves, new Comparator<RatedMove>() { //Sortiere die Züge nach Punkten
				@Override
				public int compare(RatedMove o1, RatedMove o2) {
					return o1.value > o2.value ? 1 : -1;
				}
			});
			
			
			BuildMove move = rMoves.get(rMoves.size() -1).buildMove; // get Top Rated Move
			System.out.print("*** sende zug: BUILD city = " + move.city);
			System.out.println(", slot = " + move.slot + ", size = " + move.size);
			System.out.println("MoveValue: " + rMoves.get(rMoves.size()-1).value);
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
