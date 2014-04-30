package sc.plugin2015;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


import sc.plugin2015.util.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

/**
 * Ein {@code GameState} beinhaltet alle Informationen die den Spielstand zu
 * einem gegebenen Zeitpunkt, das heisst zwischen zwei Spielzuegen, beschreiben.
 * Dies umfasst eine fortlaufende Zugnummer ({@link #getTurn() getTurn()}) und
 * was fuer eine Art von Zug ({@link #getCurrentMoveType() getCurrentMoveType()}
 * ) der Spielserver als Antwort von einem der beiden Spieler (
 * {@link #getCurrentPlayer() getCurrentPlayer()}) erwartet. Weiterhin gehoeren
 * die Informationen ueber die beiden Spieler und das Spielfeld zum
 * Zustand. Zuseatzlich wird ueber den zuletzt getaetigeten Spielzung und ggf.
 * ueber das Spielende informiert.<br/>
 * <br/>
 * 
 * Der {@code GameState} ist damit das zentrale Objekt ueber das auf alle
 * wesentlichen Informationen des aktuellen Spiels zugegriffen werden kann.<br/>
 * <br/>
 * 
 * Der Spielserver sendet an beide teilnehmende Spieler nach jedem getaetigten
 * Zug eine neue Kopie des {@code GameState}, in dem der dann aktuelle Zustand
 * beschrieben wird. Informationen ueber den Spielverlauf sind nur bedingt ueber
 * den {@code GameState}erfragbar und muessen von einem Spielclient daher bei
 * Bedarf selbst mitgeschrieben werden.<br/>
 * <br/>
 * 
 * Zusaetzlich zu den eigentlichen Informationen koennen bestimmte
 * Teilinformationen abgefragt werden. Insbesondere kann mit der Methode
 * {@link #getPossibleMoves() getPossibleMoves()} eine Liste aller fuer den
 * aktuellen Spieler legalen Laufzuege und mit 
 * {@link #getPossibleSetMoves() getPossibleSetMoves()} eine Liste aller für
 * den aktuellen Spieler legalen Setzzüge abgefragt werden. Ist momentan also ein
 * Laufzug oder ein Setzzug zu taetigen, kann eine Spieleclient diese Liste
 * aus dem {@code GameState} erfragen und muss dann lediglich einen Zug aus 
 * dieser Liste auswaehlen.
 * 
 * @author Niklas, Sören
 */
@XStreamAlias(value = "state")
public class GameState implements Cloneable {

	// momentane rundenzahl
	private int turn;

	// farbe des startspielers
	private PlayerColor startPlayer;

	// farbe des aktuellen spielers
	private PlayerColor currentPlayer;

	// momentan auszufuehrender zug-type
	private MoveType currentMoveType;

	// die teilnehmenden spieler
	private Player red, blue;
		
	 //Das Spielbrett
	 
	private Board board;

	// letzter performte move
	private Move lastMove;

	// endbedingung
	private Condition condition = null;

	/**
	 * Erzeugt einen neuen {@code GameState} in dem alle Informationen so gesetzt
	 * sind, wie sie zu Beginn eines Spiels, bevor die Spieler beigetreten sind,
	 * gueltig sind.<br/>
	 * <br/>
	 * 
	 * <b>Dieser Konstruktor ist nur fuer den Spielserver relevant und sollte vom
	 * Spielclient i.A. nicht aufgerufen werden!</b>
	 * 
	 * Der Kartenstapel wird nur initialisiert und nicht mit Karten befuellt.
	 */
	public GameState() {

		currentPlayer = PlayerColor.RED;
		startPlayer = PlayerColor.RED;
		currentMoveType = MoveType.SET;
		board = new Board();
	}
	
         /**
         * klont dieses Objekt
         * @return ein neues Objekt mit gleichen Eigenschaften
         * @throws CloneNotSupportedException 
         */
	@Override
	public Object clone() throws CloneNotSupportedException{
		GameState clone = (GameState) super.clone();
                if (red != null)
                    clone.red = (Player) red.clone();
                if (blue != null)
                    clone.blue = (Player) blue.clone();
                if (lastMove != null)
                    clone.lastMove = (Move) lastMove.clone();
                if (board != null)
        			clone.board = (Board) this.board.clone();
                if (condition != null)
                    clone.condition = (Condition) condition.clone();
                if (currentPlayer != null)
        			clone.currentPlayer = currentPlayer;
                
		return clone;
        }
        
	/**
	 * Fuegt einem Spiel einen weiteren Spieler hinzu.<br/>
	 * <br/>
	 * 
	 * <b>Diese Methode ist nur fuer den Spielserver relevant und sollte vom
	 * Spielclient i.A. nicht aufgerufen werden!</b>
	 * 
	 * @param player
	 *           Der hinzuzufuegende Spieler.
	 */
	public void addPlayer(Player player) {

		if (player.getPlayerColor() == PlayerColor.RED) {
			red = player;
		} else if (player.getPlayerColor() == PlayerColor.BLUE) {
			blue = player;
		}
	}
	
	public Board getBoard() {
		return this.board;
	}

	/**
	 * Liefert den Spieler, also ein {@code Player}-Objekt, der momentan am Zug
	 * ist.
	 * 
	 * @return Der Spieler, der momentan am Zug ist.
	 */
	public Player getCurrentPlayer() {
		return currentPlayer == PlayerColor.RED ? red : blue;
	}

	/**
	 * Liefert die {@code PlayerColor}-Farbe des Spielers, der momentan am Zug
	 * ist. Dies ist aequivalent zum Aufruf {@code
	 * getCurrentPlayer().getPlayerColor()}, aber etwas effizienter.
	 * 
	 * @return Die Farbe des Spielers, der momentan am Zug ist.
	 */
	public PlayerColor getCurrentPlayerColor() {
		return currentPlayer;
	}

	/**
	 * Liefert den Spieler, also ein {@code Player}-Objekt, der momentan nicht am
	 * Zug ist.
	 * 
	 * @return Der Spieler, der momentan nicht am Zug ist.
	 */
	public Player getOtherPlayer() {
		return currentPlayer == PlayerColor.RED ? blue : red;
	}

	/**
	 * Liefert die {@code PlayerColor}-Farbe des Spielers, der momentan nicht am
	 * Zug ist. Dies ist aequivalent zum Aufruf @{@code
	 * getCurrentPlayerColor.opponent()} oder {@code
	 * getOtherPlayer().getPlayerColor()}, aber etwas effizienter.
	 * 
	 * @return Die Farbe des Spielers, der momentan nicht am Zug ist.
	 */
	public PlayerColor getOtherPlayerColor() {
		return currentPlayer.opponent();
	}

	/**
	 * Liefert den Spieler, also eine {@code Player}-Objekt, des Spielers, der
	 * dem Spiel als erstes beigetreten ist und demzufolge mit der Farbe {@code
	 * PlayerColor.RED} spielt.
	 * 
	 * @return Der rote Spieler.
	 */
	public Player getRedPlayer() {
		return red;
	}

	/**
	 * Liefert den Spieler, also eine {@code Player}-Objekt, des Spielers, der
	 * dem Spiel als zweites beigetreten ist und demzufolge mit der Farbe {@code
	 * PlayerColor.BLUE} spielt.
	 * 
	 * @return Der blaue Spieler.
	 */
	public Player getBluePlayer() {
		return blue;
	}

	/**
	 * Liefert den Spieler, also eine {@code Player}-Objekt, der den aktuellen
	 * Abschnitt begonnen hat. Also den Spieler, der in der letzten Auswahlphase
	 * als erster Bauelemente waehlen musste und dann als zweiter gebaut hat.
	 * 
	 * @return Der Spieler, der momentan Startspieler ist.
	 */
	public Player getStartPlayer() {
		return startPlayer == PlayerColor.RED ? red : blue;
	}

	/**
	 * Liefert die {@code PlayerColor}-Farbe des Spielers, der den aktuellen
	 * Abschnitt begonnen hat. Dies ist aequivalent zum Aufruf {@code
	 * getStartPlayer().getPlayerColor()}, aber etwas effizienter.
	 * 
	 * @return Die Farbe des Spielers, der den aktuellen Abschnitt nicht begonnen
	 *         hat.
	 */
	public PlayerColor getStartPlayerColor() {
		return startPlayer;
	}

	/**
	 * wechselt den Spieler, der aktuell an der Reihe ist.
	 */
	private void switchCurrentPlayer() {
		currentPlayer = currentPlayer == PlayerColor.RED ? PlayerColor.BLUE : PlayerColor.RED;
	}

	/**
	 * wechselt den Spieler, der den aktuellen Abschnitt begonnen hat.
	 */
	private void switchStartPlayer() {
		startPlayer = startPlayer == PlayerColor.RED ? PlayerColor.BLUE : PlayerColor.RED;
	}

	/**
	 * liefert den momentan auszufuehrenden Zugtyp
	 */
	public MoveType getCurrentMoveType() {
		return currentMoveType;
	}

	/**
	 * setzt den momentan auszufuehrenden Zugtyp
	 */
	public void setCurrentMoveType(MoveType moveType) {
		currentMoveType = moveType;
	}

	/**
	 * liefert die aktuelle Zugzahl
	 */
	public int getTurn() {
		return turn;
	}

	/**
	 * Simuliert einen uebergebenen Zug. Dabei werden folgende Informationen
	 * aktualisiert:
	 * <ul>
	 * <li>Zugzahl
	 * <li>Welcher Spieler an der Reihe ist
	 * <li>Welcher Spieler erster der Spielphase ist
	 * <li>Was der letzte Zug war
	 * <li>Was der aktuell erwartete Zug ist
	 * <li>die Punkte der Spieler
	 * </ul>
	 * 
	 * @param lastMove
	 *           auszufuehrender Zug
	 */
	public void prepareNextTurn(Move lastMove) {
		
		turn++;
		this.lastMove = lastMove;
		
		if(turn == Constants.PENGUINS*2+1) {
			setCurrentMoveType(MoveType.RUN);
			switchCurrentPlayer();
		} else {
			switchCurrentPlayer();
		}
	}

	/**
	 * 
	 */
	private void performScoring() {

		int[][] stats = getGameStats();


	}

	/**
	 * liefert die aktuelle Rundenzahl
	 * 
	 * @return aktuelle Rundenzahl
	 */
	public int getRound() {
		return turn / 2;
	}

	/**
	 * Liefert eine Liste aller aktuell erlaubten Zuege.
	 * 
	 * @return Liste erlaubter Spielzuege
	 */
	public List<RunMove> getPossibleMoves() {
		List<RunMove> moves = new ArrayList<RunMove>();
		for(int x = 0; x < Constants.COLUMNS; x++) {
			for(int y = 0; y < Constants.ROWS; y++) {
				if(this.board.hasPinguin(x, y, getCurrentPlayerColor())) {
					moves.addAll(leftOfPenguin(x, y));
					moves.addAll(rightOfPenguin(x, y));
					moves.addAll(topLeftOfPenguin(x, y));
					moves.addAll(bottomRightOfPenguin(x, y));
					moves.addAll(topRightOfPenguin(x, y));
					moves.addAll(bottomLeftOfPenguin(x, y));
				}
			}
		}
		moves.add(new NullMove());
		return moves;
	}
	public List<RunMove> getPossibleMoves(PlayerColor playerColor) {
		List<RunMove> moves = new ArrayList<RunMove>();
		for(int x = 0; x < Constants.COLUMNS; x++) {
			for(int y = 0; y < Constants.ROWS; y++) {
				if(this.board.hasPinguin(x, y, playerColor)) {
					moves.addAll(leftOfPenguin(x, y));
					moves.addAll(rightOfPenguin(x, y));
					moves.addAll(topLeftOfPenguin(x, y));
					moves.addAll(bottomRightOfPenguin(x, y));
					moves.addAll(topRightOfPenguin(x, y));
					moves.addAll(bottomLeftOfPenguin(x, y));
				}
			}
		}
		moves.add(new NullMove());
		return moves;
	}
	
	
	private List<RunMove> leftOfPenguin(int x, int y) {
		boolean done = false;
		int currentX = x - 1;
		List<RunMove> moves = new ArrayList <RunMove>();
		while(!done) {
			if(currentX < 0 || this.board.getPenguin(currentX, y) != null || this.board.getFish(currentX, y) == 0) {
				done = true;
			} else {
				moves.add(new RunMove(x,y,currentX,y));
				currentX--;
			}
		}
		return moves;
	}
	
	private List<RunMove> rightOfPenguin(int x, int y) {
		boolean done = false;
		int currentX = x + 1;
		List<RunMove> moves = new ArrayList <RunMove>();
		while(!done) {
			if(currentX >= Constants.COLUMNS || this.board.getPenguin(currentX, y) != null || this.board.getFish(currentX, y) == 0) {
				done = true;
			} else {
				moves.add(new RunMove(x,y,currentX,y));
				currentX++;
			}
		}
		return moves;
	}
	
	private List<RunMove> topLeftOfPenguin(int x, int y) {
		boolean done = false;
		int currentX;
		if((y & 1) == 0) {
			currentX = x;
		} else {
			currentX = x - 1;
		}
		int currentY = y - 1;
		List<RunMove> moves = new ArrayList <RunMove>();
		while(!done) {
			if(currentX < 0 || currentY < 0 || this.board.getPenguin(currentX, currentY) != null || this.board.getFish(currentX, currentY) == 0) {
				done = true;
			} else {
				moves.add(new RunMove(x,y,currentX,currentY));
				if((currentY & 1) == 1)
					currentX--;
				currentY--;
			}
		}
		return moves;
	}
	
	private List<RunMove> topRightOfPenguin(int x, int y) {
		boolean done = false;
		int currentX;
		if((y & 1) == 1) {
			currentX = x;
		} else {
			currentX = x + 1;
		}
		int currentY = y - 1;
		List<RunMove> moves = new ArrayList <RunMove>();
		while(!done) {
			if(currentX >= Constants.COLUMNS || currentY < 0 || this.board.getPenguin(currentX, currentY) != null || this.board.getFish(currentX, currentY) == 0) {
				done = true;
			} else {
				moves.add(new RunMove(x,y,currentX,currentY));
				if((currentY & 1) == 0)
					currentX++;
				currentY--;
			}
		}
		return moves;
	}
	
	private List<RunMove> bottomRightOfPenguin(int x, int y) {
		boolean done = false;
		int currentX;
		if((y & 1) == 1) {
			currentX = x;
		} else {
			currentX = x + 1;
		}
		int currentY = y + 1;
		List<RunMove> moves = new ArrayList <RunMove>();
		while(!done) {
			if(currentX >= Constants.COLUMNS || currentY >= Constants.ROWS || this.board.getPenguin(currentX, currentY) != null || this.board.getFish(currentX, currentY) == 0) {
				done = true;
			} else {
				moves.add(new RunMove(x,y,currentX,currentY));
				if((currentY & 1) == 0)
					currentX++;
				currentY++;
			}
		}
		return moves;
	}
	
	private List<RunMove> bottomLeftOfPenguin(int x, int y) {
		boolean done = false;
		int currentX;
		if((y & 1) == 0) {
			currentX = x;
		} else {
			currentX = x - 1;
		}
		int currentY = y + 1;
		List<RunMove> moves = new ArrayList <RunMove>();
		while(!done) {
			if(currentX < 0 || currentY >= Constants.ROWS || this.board.getPenguin(currentX, currentY) != null || this.board.getFish(currentX, currentY) == 0) {
				done = true;
			} else {
				moves.add(new RunMove(x,y,currentX,currentY));
				if((currentY & 1) == 1)
					currentX--;
				currentY++;
			}
		}
		return moves;
	}
	
	public List<SetMove> getPossibleSetMoves() {
		List<SetMove> moves = new ArrayList<SetMove>();
		for(int x = 0; x < Constants.COLUMNS; x++) {
			for(int y = 0; y < Constants.ROWS; y++) {
				if(board.getFish(x, y) == 1 && board.getPenguin(x, y) == null)
					moves.add(new SetMove(x,y));
			}
		}
		return moves;
	}
	
	/*
	 * Verteilt die Punkte am Ende des Spiels für die Figuren, die noch auf dem Spielfeld stehen.
	 */
	protected void clearEndGame() {
		for(int i = 0; i < Constants.ROWS; i++) {
			for(int j = 0; j < Constants.COLUMNS; j++) {
				if(this.board.hasPinguin(i, j, PlayerColor.BLUE)) {
					this.blue.addPoints(this.board.getFish(i, j));
					this.blue.addField();
				} else if(this.board.hasPinguin(i, j, PlayerColor.RED)) {
					this.red.addPoints(this.board.getFish(i, j));
					this.red.addField();
				}
			}
		}
	}

	/**
	 * Liefert den zuletzt ausgefuehrten Zug
	 * 
	 * @return letzter Zug
	 */
	public Move getLastMove() {
		return lastMove;
	}

	/**
	 * Liefert Statusinformationen zu einem Spieler als Array mit folgenden
	 * Einträgen
	 * <ul>
	 * <li>[0] - Punktekonto des Spielers
	 * <li>[1] - Anzahl der Plättchen
	 * </ul>
	 * 
	 * @param player
	 *           Spieler
	 * @return Array mit Statistiken
	 */
	public int[] getPlayerStats(Player player) {
		assert player != null;
		return getPlayerStats(player.getPlayerColor());
	}

	/**
	 * Liefert Statusinformationen zu einem Spieler als Array mit folgenden
	 * Einträgen
	 * <ul>
	 * <li>[0] - Punktekonto des Spielers
	 * <li>[1] - Anzahl der Plättchen
	 * </ul>
	 * 
	 * @param playerColor
	 *           Farbe des Spielers
	 * @return Array mit Statistiken
	 */
	public int[] getPlayerStats(PlayerColor playerColor) {
		assert playerColor != null;

		if (playerColor == PlayerColor.RED) {
			return getGameStats()[0];
		} else {
			return getGameStats()[1];
		}
	}

	/**
	 * Liefert Statusinformationen zum Spiel. Diese sind ein Array der
	 * {@link #getPlayerStats(PlayerColor) Spielerstats}, wobei
	 * getGameStats()[0], einem Aufruf von getPlayerStats(PlayerColor.RED)
	 * entspricht.
	 * 
	 * @see #getPlayerStats(PlayerColor)
	 * @return Statusinformationen beider Spieler
	 */
	public int[][] getGameStats() {

		int[][] stats = new int[2][2];
		
		stats[0][0] = this.red.getPoints();
		stats[0][1] = this.red.getFields();
		stats[1][0] = this.blue.getPoints();
		stats[1][1] = this.blue.getFields();

		return stats;

	}

	/**
	 * liefert die Namen den beiden Spieler
	 */
	public String[] getPlayerNames() {
		return new String[] { red.getDisplayName(), blue.getDisplayName() };

	}

	/**
	 * Legt das Spiel als beendet fest, setzt dabei einen Sieger und Gewinngrund
	 * 
	 * @param winner
	 *           Farbe des Siegers
	 * @param reason
	 *           Gewinngrund
	 */
	public void endGame(PlayerColor winner, String reason) {
		if (condition == null) {
			condition = new Condition(winner, reason);
		}
	}

	/**
	 * gibt an, ob das Spiel beendet ist
	 * 
	 * @return wahr, wenn beendet
	 */
	public boolean gameEnded() {
		return condition != null;
	}

	/**
	 * liefert die Farbe des Siegers, falls das Spiel beendet ist.
	 * 
	 * @see #gameEnded()
	 * @return Siegerfarbe
	 */
	public PlayerColor winner() {
		return condition == null ? null : condition.winner;
	}

	/**
	 * liefert den Gewinngrund, falls das Spiel beendet ist.
	 * 
	 * @see #gameEnded()
	 * @return Gewinngrund
	 */
	public String winningReason() {
		return condition == null ? "" : condition.reason;
	}

}
