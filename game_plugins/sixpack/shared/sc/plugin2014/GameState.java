package sc.plugin2014;

import static sc.plugin2014.util.Constants.*;
import java.util.List;
import sc.plugin2014.converters.GameStateConverter;
import sc.plugin2014.entities.*;
import sc.plugin2014.moves.Move;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

/**
 * Ein {@code GameState} beinhaltet alle Informationen die den Spielstand zu
 * einem gegebenen Zeitpunkt, das heisst zwischen zwei Spielzuegen, beschreiben.
 * 
 * Dies umfasst eine fortlaufende Zugnummer ({@link #getTurn() getTurn()}) und
 * welcher Spierler momentan an der Reihe ist {@link #getCurrentPlayer()
 * getCurrentPlayer()}). Weiterhin gehoeren die Informationen ueber die beiden
 * Spieler({@link #getBluePlayer()},{@link #getRedPlayer()}, das Spielbrett (
 * {@link #getBoard()}) und die als nächstes ziehbaren Spielsteine (
 * {@link #getNextStonesInBag()}) dazu. Zuseatzlich wird ueber den zuletzt
 * getaetigeten Spielzung {@link #getLastMove()}) und ggf. ueber das Spielende
 * informiert ({@link #gameEnded()}, {@link #winningReason()}, {@link #winner()}
 * ).<br/>
 * 
 * <br/>
 * Der {@code GameState} ist damit das zentrale Objekt ueber das auf alle
 * wesentlichen Informationen des aktuellen Spiels zugegriffen werden kann.<br/>
 * <br/>
 * 
 * Der Spielserver sendet an beide teilnehmende Spieler nach jedem getaetigten
 * Zug eine neue Kopie des {@code GameState}, in dem der dann aktuelle Zustand
 * beschrieben wird. Informationen ueber den Spielverlauf sind nur bedingt ueber
 * den {@code GameState} erfragbar und muessen von einem Spielclient daher bei
 * Bedarf selbst mitgeschrieben werden.<br/>
 * <br/>
 * 
 * Zusaetzlich zu den eigentlichen Informationen koennen bestimmte
 * Teilinformationen, zum Beispiele die Liste aller Spielsteine eines Spielers,
 * abgefragt werden. Insbesondere kann mit der Methode
 * {@link #getPossibleMoves()} eine Liste aller fuer den aktuellen Spieler
 * legalen Zuege abgefragt werden.
 * 
 * @author ffi
 */
@XStreamAlias(value = "state")
@XStreamConverter(GameStateConverter.class)
public class GameState implements Cloneable {

	// momentane rundenzahl
	private int turn;

	// farbe des startspielers
	private final PlayerColor startPlayer;

	// farbe des aktuellen spielers
	private PlayerColor currentPlayer;

	private Player red, blue;

	private Board board;

	private StoneBag stoneBag;

	private int stonesInBag;

	private List<Stone> nextStones;

	private Move lastMove;

	private WinnerAndReason endCondition = null;

	/**
	 * Erzeugt einen neuen GameState. Dabei wird eine neues {@link Board
	 * Spielbrett}, sowie ein {@link StoneBag Spielsteinbeutel} generiert.
	 * 
	 */
	public GameState() {
		currentPlayer = PlayerColor.RED;
		startPlayer = PlayerColor.RED;
		stoneBag = new StoneBag();
		board = new Board();
		StoneIdentifierGenerator.reset();
	}

	/**
	 * Klont dieses Objekt. (deep copy)
	 * 
	 * @return ein neues Objekt mit gleichen Eigenschaften
	 * @throws CloneNotSupportedException
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		GameState clone = (GameState) super.clone();
		if (red != null) {
			clone.red = (Player) red.clone();
		}
		if (blue != null) {
			clone.blue = (Player) blue.clone();
		}
		if (lastMove != null) {
			clone.lastMove = (Move) lastMove.clone();
		}
		if (endCondition != null) {
			clone.endCondition = (WinnerAndReason) endCondition.clone();
		}
		if (stoneBag != null) {
			clone.stoneBag = (StoneBag) stoneBag.clone();
		}
		if (board != null) {
			clone.board = (Board) board.clone();
		}
		return clone;
	}

	/**
	 * Fügt einem Spiel einen weiteren Spieler hinzu.<br/>
	 * <br/>
	 * 
	 * <b>Diese Methode ist nur fuer den Spielserver relevant und sollte vom
	 * Spielclient i.A. nicht aufgerufen werden!</b>
	 * 
	 * @param player
	 *            Der hinzuzufuegende Spieler.
	 */
	public void addPlayer(Player player) {

		if (player.getPlayerColor() == PlayerColor.RED) {
			red = player;
		} else if (player.getPlayerColor() == PlayerColor.BLUE) {
			blue = player;
		}

		for (int i = 0; i < STONES_PER_PLAYER; i++) {
			player.addStone(stoneBag.drawStone());
		}

		updateStonesInBag();
	}

	/**
	 * Liefert den Spieler, also ein {@link Player}-Objekt, der momentan am Zug
	 * ist.
	 * 
	 * @return Der Spieler, der momentan am Zug ist.
	 */
	public Player getCurrentPlayer() {
		return currentPlayer == PlayerColor.RED ? red : blue;
	}

	/**
	 * Liefert die {@link PlayerColor}-Farbe des Spielers, der momentan am Zug
	 * ist. Dies ist aequivalent zum Aufruf
	 * {@code getCurrentPlayer().getPlayerColor()}, aber etwas effizienter.
	 * 
	 * @return Die Farbe des Spielers, der momentan am Zug ist.
	 */
	public PlayerColor getCurrentPlayerColor() {
		return currentPlayer;
	}

	/**
	 * Liefert den Spieler, also ein {@link Player}-Objekt, der momentan nicht
	 * am Zug ist.
	 * 
	 * @return Der Spieler, der momentan nicht am Zug ist.
	 */
	public Player getOtherPlayer() {
		return currentPlayer == PlayerColor.RED ? blue : red;
	}

	/**
	 * Liefert die {@link PlayerColor}-Farbe des Spielers, der momentan nicht am
	 * Zug ist. Dies ist aequivalent zum Aufruf
	 * {@link #getCurrentPlayerColor.opponent()} oder {@link #getOtherPlayer()
	 * .getPlayerColor()}, aber etwas effizienter.
	 * 
	 * @return Die Farbe des Spielers, der momentan nicht am Zug ist.
	 */
	public PlayerColor getOtherPlayerColor() {
		return currentPlayer.getOpponent();
	}

	/**
	 * Liefert den Spieler, also eine {@link Player}-Objekt, des Spielers, der
	 * dem Spiel als erstes beigetreten ist und demzufolge mit der Farbe
	 * {@code PlayerColor.RED} spielt.
	 * 
	 * @return Der rote Spieler.
	 */
	public Player getRedPlayer() {
		return red;
	}

	/**
	 * Liefert den Spieler, also eine {@link Player}-Objekt, des Spielers, der
	 * dem Spiel als zweites beigetreten ist und demzufolge mit der Farbe
	 * {@code PlayerColor.BLUE} spielt.
	 * 
	 * @return Der blaue Spieler.
	 */
	public Player getBluePlayer() {
		return blue;
	}

	/**
	 * Liefert den Spieler, also eine {@link Player}-Objekt, der den aktuellen
	 * Abschnitt begonnen hat. Also den Spieler, der in der letzten Auswahlphase
	 * als erster Bauelemente waehlen musste und dann als zweiter gebaut hat.
	 * 
	 * @return Der Spieler, der momentan Startspieler ist.
	 */
	public Player getStartPlayer() {
		return startPlayer == PlayerColor.RED ? red : blue;
	}

	/**
	 * Liefert die {@link PlayerColor}-Farbe des Spielers, der den aktuellen
	 * Abschnitt begonnen hat. Dies ist aequivalent zum Aufruf {@link
	 * getStartPlayer().getPlayerColor()}, aber etwas effizienter.
	 * 
	 * @return Die Farbe des Spielers, der den aktuellen Abschnitt nicht
	 *         begonnen hat.
	 */
	public PlayerColor getStartPlayerColor() {
		return startPlayer;
	}

	/**
	 * Liefert die aktuelle Zugzahl.
	 * 
	 * @return Die aktuelle Zugzahl
	 */
	public int getTurn() {
		return turn;
	}

	/**
	 * Liefert die aktuelle Rundenzahl. (Hälfte der Zugzahl)
	 * 
	 * @return aktuelle Rundenzahl
	 */
	public int getRound() {
		return turn / 2;
	}

	/**
	 * Liefert den zuletzt ausgeführten Zug.
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
	 * </ul>
	 * 
	 * @param player
	 *            Spieler
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
	 * </ul>
	 * 
	 * @param playerColor
	 *            Farbe des Spielers
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

		int[][] stats = new int[2][1];

		stats[0][0] = red.getPoints();
		stats[1][0] = blue.getPoints();

		return stats;

	}

	/**
	 * Liefert die Namen den beiden Spieler.
	 * 
	 * @return Ein Array mit den Namen. Position 0: roter Spieler, Position 1:
	 *         blauer Spieler
	 */
	public String[] getPlayerNames() {
		return new String[] { red.getDisplayName(), blue.getDisplayName() };

	}

	/**
	 * Legt das Spiel als beendet fest, setzt dabei einen Sieger und
	 * Gewinngrund.
	 * 
	 * @param winner
	 *            Farbe des Siegers
	 * @param reason
	 *            Gewinngrund
	 */
	public void endGame(PlayerColor winner, String reason) {
		if (endCondition == null) {
			endCondition = new WinnerAndReason(winner, reason);
		}
	}

	/**
	 * Gibt an, ob das Spiel beendet ist.
	 * 
	 * @return true, wenn beendet
	 */
	public boolean gameEnded() {
		return endCondition != null;
	}

	/**
	 * Liefert die Farbe des Siegers, falls das Spiel beendet ist.
	 * 
	 * @see #gameEnded()
	 * @return Siegerfarbe
	 */
	public PlayerColor winner() {
		return endCondition == null ? null : endCondition.winner;
	}

	/**
	 * Liefert den Gewinngrund, falls das Spiel beendet ist.
	 * 
	 * @see #gameEnded()
	 * @return Gewinngrund, leerer String, wenn das Spiel nicht beendet ist.
	 */
	public String winningReason() {
		return endCondition == null ? "" : endCondition.reason;
	}

	/**
	 * <b>Diese Methode ist nur fuer den Spielserver relevant und sollte vom
	 * Spielclient i.A. nicht aufgerufen werden!</b>
	 * 
	 * @return
	 */
	public Stone drawStone() {
		return stoneBag.drawStone();
	}

	/**
	 * <b>Diese Methode ist nur fuer den Spielserver relevant und sollte vom
	 * Spielclient i.A. nicht aufgerufen werden!</b>
	 * 
	 * @param stone
	 */
	public void putBackStone(Stone stone) {
		stoneBag.putBackStone(stone);
	}

	/**
	 * Gibt die Anzahl der Steine zurück, die sich im Beutel befinden.
	 * 
	 * @return Anzahl der Steine im Beutel.
	 */
	public int getStoneCountInBag() {
		return stonesInBag;
	}

	/**
	 * Liefert eine Liste mit Spielsteinen zurück, welche als nächstes gezogen
	 * werden können, also schon aufgedeckt sind.
	 * 
	 * @return Liste mit Spielsteinen
	 */
	public List<Stone> getNextStonesInBag() {
		return nextStones;
	}

	/**
	 * Aktualisiert den GameState. Dabei werden aktualisiert:
	 * <ul>
	 * <li>Die aktuelle Zugzahl {@link #getTurn()}
	 * <li>Der Letzte Zug {@link #getLastMove()}
	 * <li>Der aktuelle Spieler {@link #getCurrentPlayer()}
	 * </ul>
	 * Um einen Zug durchzuführen muss zusätzlich
	 * {@link Move#perform(GameState, Player)} ausgeführt werden.
	 * 
	 * @param move
	 *            Zug welcher ausgeführt werden soll.
	 */
	public void prepareNextTurn(Move move) {
		turn++;
		lastMove = move;
		switchCurrentPlayer();
	}

	/**
	 * Wechselt den Spieler, der aktuell an der Reihe ist.</br> <b>Diese Methode
	 * ist nur fuer den Spielserver relevant und sollte vom Spielclient i.A.
	 * nicht aufgerufen werden!</b>
	 */
	private void switchCurrentPlayer() {
		currentPlayer = currentPlayer == PlayerColor.RED ? PlayerColor.BLUE
				: PlayerColor.RED;
	}

	/**
	 * Legt einen Spielstein auf eine Boardposition. </br> <b>Diese Methode ist
	 * nur fuer den Spielserver relevant und sollte vom Spielclient i.A. nicht
	 * aufgerufen werden!</b>
	 * 
	 * @param stoneToLay
	 * @param posX
	 * @param posY
	 */
	public void layStone(Stone stoneToLay, int posX, int posY) {
		board.layStone(stoneToLay, posX, posY);
	}

	/**
	 * Liefert das aktuelle Spielbrett zurück {@link Board}
	 * 
	 * @return das aktuelle Spielbrett
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * Aktualisiert die Steine im Beutel </br> <b>Diese Methode ist nur fuer den
	 * Spielserver relevant und sollte vom Spielclient i.A. nicht aufgerufen
	 * werden!</b>
	 */
	public void updateStonesInBag() {
		nextStones = stoneBag.getNextStonesInBag();
		stonesInBag = stoneBag.getStoneCountInBag();
	}

	/**
	 * Lädt eine Spielsituation aus einem Gamestate. </br> <b>Diese Methode ist
	 * nur fuer den Spielserver relevant und sollte vom Spielclient i.A. nicht
	 * aufgerufen werden!</b>
	 * 
	 * @param gs
	 *            GameState der geladen werden soll
	 */
	public void loadFromFile(GameState gs) {
		stoneBag.loadFromFile(gs); // filters out the stone Bag
		// Sets the Board
		if (gs.getBoard().hasStones()) {
			for (Field field : gs.getBoard().getFields()) {
				if (!field.isFree()) {
					layStone(field.getStone(), field.getPosX(), field.getPosY());
				}
			}
		}

		// Set turn
		this.turn = gs.getTurn();
		// Set activePlayer
		this.currentPlayer = gs.getCurrentPlayerColor();
	}
}
