package sc.plugin2012;

import static sc.plugin2012.util.Constants.CARDS_PER_PLAYER;
import static sc.plugin2012.util.Constants.CARDS_PER_SLOT;
import static sc.plugin2012.util.Constants.MAX_SEGMENT_SIZE;
import static sc.plugin2012.util.Constants.SEGMENT_AMOUNTS;
import static sc.plugin2012.util.Constants.SLOTS;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import sc.plugin2012.util.Constants;
import sc.plugin2012.util.GameStateConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

/**
 * Ein {@code GameState} beinhaltet alle Informationen die den Spielstand zu
 * einem gegebenen Zeitpunkt, das heisst zwischen zwie Spielzuegen, beschreiben.
 * Dies umfasst eine fortlaufende Zugnummer ({@link getTurn() getTurn()}) und
 * was fuer eine Art von Zug ({@link getCurrentMoveType() getCurrentMoveType()})
 * der Spielserver als Antwort von einem der beiden Spieler ({@link
 * getCurrentPlayer() getCurrentPlayer()}) erwartet. Weiterhin gehoeren die
 * Informationen ueber die beiden Spieler und alle moeglichen Tuerme zum
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
 * Teilinformationen, zum Beispiele die Liste aller Tuerme eines Spielers,
 * abgefragt werden. Insbesondere kann mit der Methode {@link getPossibleMoves()
 * getPossibleMoves()} eine Liste aller fuer den aktuellen Spieler legalen
 * Bauzuege abgefragt werden. Ist momentan also eine Bauzug zu taetigen, kann
 * eine Spieleclient diese Liste aus dem {@code GameState} erfragen und muss
 * dann lediglich einen Zug aus dieser Liste auswaehlen.
 * 
 * @author tkra
 */
@XStreamAlias(value = "manhattan:state")
@XStreamConverter(GameStateConverter.class)
public final class GameState implements Cloneable {

	// momentane rundenzahl
	private int turn;

	// farbe des startspielers
	private PlayerColor startPlayer;

	// farbe des aktuellen spielers
	private PlayerColor currentPlayer;

	// momentan auszufuehrender zug-type
	private MoveType currentMoveType;

	// die teilenhmenden spieler
	private Player red, blue;

	// kartenstapel
	private final List<Card> cardStack;

	// listre der gebauten tuerem
	private final List<Tower> towers;

	// letzter performte move
	private Move lastMove;

	// endbedingung
	private Condition condition = null;

	/**
	 * Erzeugt einen neuen {@code GameState} in dem alle Informationen so
	 * gesetzt sind, wie sie zu Beginn eines Spiels, bevor die Spieler
	 * beigetreten sind, gueltig sind.<br/>
	 * <br/>
	 * 
	 * <b>Dieser Konstruktor ist nur fuer den Spielserver relevant und sollte
	 * vom Spielclient i.A. nicht aufgerufen werden!</b>
	 */
	public GameState() {

		currentPlayer = PlayerColor.RED;
		startPlayer = PlayerColor.RED;
		currentMoveType = MoveType.SELECT;
		cardStack = new LinkedList<Card>();
		towers = new ArrayList<Tower>(Constants.CITIES * Constants.SLOTS);
		for (int city = 0; city < Constants.CITIES; city++) {
			for (int slot = 0; slot < Constants.SLOTS; slot++) {
				towers.add(new Tower(city, slot));
			}
		}

	}

	/**
	 * Fuegt einem Spiel einen weiteren Spieler hinzu.<br/>
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

		for (int i = 0; i < CARDS_PER_PLAYER; i++) {
			player.addCard(drawCard());
		}

		for (int i = 1; i <= MAX_SEGMENT_SIZE; i++) {
			player.addSegmet(new Segment(i, SEGMENT_AMOUNTS[i - 1]));
		}

	}

	/**
	 * Liefert den Spieler, also eine {@code Player}-Objekt, der momentan am Zug
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
	 * Liefert den Spieler, also eine {@code Player}-Objekt, der momentan nicht
	 * am Zug ist.
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
	 * dem Spiel als zweites beigetreten ist und demzufolge mit der Farbe
	 * {@code PlayerColor.BLUE} spielt.
	 * 
	 * @return Der blaue Spieler.
	 */
	public Player getBluePlayer() {
		return blue;
	}

	/**
	 * Liefert den Spieler, also eine {@code Player}-Objekt, der den aktuellen
	 * Durchgang begonnen hat. Also den Spieler, der in der letzten Auswahlphase
	 * zls erster Bauelemente waehlen musste und danach als erster eine Segment
	 * an einen Turm angebaut hat.
	 * 
	 * @return Der Spieler, der momentan Startspieler ist.
	 */
	public Player getStartPlayer() {
		return startPlayer == PlayerColor.RED ? red : blue;
	}

	/**
	 * Liefert die {@code PlayerColor}-Farbe des Spielers, der den aktuellen
	 * Durchgang begonnen hat. Dies ist aequivalent zum Aufruf {@code
	 * getStartPlayer().getPlayerColor()}, aber etwas effizienter.
	 * 
	 * @return Die Farbe des Spielers, der momentan nicht am Zug ist.
	 */
	public PlayerColor getStartPlayerColor() {
		return startPlayer;
	}

	private void switchCurrentPlayer() {
		currentPlayer = currentPlayer == PlayerColor.RED ? PlayerColor.BLUE : PlayerColor.RED;
	}

	private void switchStartPlayer() {
		startPlayer = startPlayer == PlayerColor.RED ? PlayerColor.BLUE : PlayerColor.RED;
	}

	/**
	 * liefert den momentan auszufuehrenden zugtyp
	 */
	public MoveType getCurrentMoveType() {
		return currentMoveType;
	}

	/**
	 * liefert den momentan auszufuehrenden zugtyp
	 */
	public void setCurrentMoveType(MoveType moveType) {
		currentMoveType = moveType;
	}

	/**
	 * liefert die aktuelle zugzahl
	 */
	public int getTurn() {
		return turn;
	}

	/**
	 * liefert die aktuelle zugzahl
	 */
	public void prepareNextTurn(Move lastMove) {

		turn++;
		this.lastMove = lastMove;

		if (currentPlayer != startPlayer) {
			if (currentMoveType == MoveType.SELECT) {
				setCurrentMoveType(MoveType.BUILD);
				switchCurrentPlayer();
			} else if (getCurrentPlayer().getUsableSegmentCount() == 0) {
				setCurrentMoveType(MoveType.SELECT);
				switchStartPlayer();
				currentPlayer = startPlayer;
				performScoring();
			} else {
				switchCurrentPlayer();

				/*
				 * im letzten durchgang kann es prinzipiell moeglich sein, dass
				 * kein zug mehr moeglich ist. dann bekommt der aktuelle spieler
				 * eine ersatzkarte statt seiner zuletzt gezogenen karte
				 */
				Player currentPlayer = getCurrentPlayer();
				if (currentPlayer.getRetainedSegmentCount() == 0) {
					while (getPossibleMoves().isEmpty()) {
						currentPlayer.removeCard(0);
						currentPlayer.addCard(drawCard());
					}
				}

			}
		} else {
			switchCurrentPlayer();
		}

	}

	private void performScoring() {

		Player red = getRedPlayer();
		Player blue = getBluePlayer();
		int[] majorities = new int[Constants.CITIES];
		Tower highestTower = null;
		boolean ambiguous = true;
		int redTowers = 0;
		int blueTowers = 0;

		// tuerme durchgehen und bei den zug. staedten ink. fuer rot
		// und dek. fuer blau. tuerme der beiden spieler zaehlen
		for (Tower tower : towers) {
			if (tower.getOwner() == PlayerColor.RED) {
				majorities[tower.city]++;
				redTowers++;
			} else if (tower.getOwner() == PlayerColor.BLUE) {
				majorities[tower.city]--;
				blueTowers++;
			}

			// pruefen ob turm groesster turm ist
			// oder bisherigem groessten turm gleicht
			if (highestTower == null) {
				highestTower = tower;
				ambiguous = false;
			} else {
				if (tower.getHeight() == highestTower.getHeight()) {
					ambiguous = true;
				} else if (tower.getHeight() > highestTower.getHeight()) {
					if (tower.getOwner() != highestTower.getOwner()) {
						highestTower = tower;
						ambiguous = false;
					}
				}
			}
		}

		// punkte fuer eindeutigen groessten turm
		if (!ambiguous) {
			((highestTower.getOwner() == PlayerColor.RED) ? red : blue)
					.addPoints(Constants.POINTS_PER_HIGHEST_TOWER);
		}

		// punkte fuer eindeutig zugewiesenen staedte
		for (int i = 0; i < Constants.CITIES; i++) {
			if (majorities[i] > 0) {
				red.addPoints(Constants.POINTS_PER_OWEND_CITY);
			} else if (majorities[i] < 0) {
				blue.addPoints(Constants.POINTS_PER_OWEND_CITY);
			}
		}

		// punkte fuer tuerme
		red.addPoints(redTowers * Constants.POINTS_PER_TOWER);
		blue.addPoints(blueTowers * Constants.POINTS_PER_TOWER);

	}

	/**
	 * liefert die aktuelle rundenzahl
	 */
	public int getRound() {
		return turn / 2;
	}

	/**
	 * liefert die oberste karte vom kartenstapel und fuellt den kartenstapel
	 * falls noetig vorher auf
	 */
	public Card drawCard() {
		if (cardStack.isEmpty()) {
			createCardStack();
		}
		return cardStack.remove(0);
	}

	private void createCardStack() {

		int[] cardsInGame = new int[Constants.SLOTS];

		if (null != red && null != blue) {
			for (Card card : red.getCards()) {
				cardsInGame[card.slot]++;
			}
			for (Card card : blue.getCards()) {
				cardsInGame[card.slot]++;
			}
		}

		cardStack.clear();
		for (int slot = 0; slot < SLOTS; slot++) {
			int cards = CARDS_PER_SLOT - cardsInGame[slot];
			for (int card = 0; card < cards; card++) {
				cardStack.add(new Card(slot));
			}
		}
		Collections.shuffle(cardStack, new SecureRandom());
	}

	/**
	 * liefert den turm an einer gegebenen position, falls vorhanden, null
	 * andernfalls
	 */
	public Tower getTower(int city, int slot) {
		if (city < 0 || city >= Constants.CITIES) {
			if (slot < 0 || slot >= Constants.SLOTS) {
				throw new IllegalArgumentException("no such tower: city " + city + ", slot " + slot);
			}
		}

		return towers.get(city * Constants.SLOTS + slot);

	}

	/**
	 * liefert alle
	 */
	public List<Tower> getTowers() {
		List<Tower> towersOfAllCities = new LinkedList<Tower>();

		if (towers != null) {
			for (Tower tower : towers) {
				towersOfAllCities.add(tower);
			}
		}
		return towersOfAllCities;
	}

	/**
	 * liefert alle tuerme einer stadt
	 */
	public List<Tower> getTowersOfCity(int city) {
		List<Tower> towersOfCity = new LinkedList<Tower>();

		if (towers != null) {
			for (Tower tower : towers) {
				if (tower.city == city) {
					towersOfCity.add(tower);
				}
			}
		}
		return towersOfCity;
	}

	/**
	 * liefert alle tuerme einer stadt und eines spielers
	 */
	public List<Tower> getTowersOfCity(int city, PlayerColor color) {
		List<Tower> towersOfCity = new LinkedList<Tower>();
		for (Tower tower : towers) {
			if (tower.city == city && tower.getOwner() == color) {
				towersOfCity.add(tower);
			}
		}
		return towersOfCity;
	}

	/**
	 * liefert alle tuerme auf gegebenen slot
	 */
	public List<Tower> getTowersOnSlot(int slot) {
		List<Tower> towersOfCity = new LinkedList<Tower>();
		for (Tower tower : towers) {
			if (tower.slot == slot) {
				towersOfCity.add(tower);
			}
		}
		return towersOfCity;
	}

	/**
	 * liefert alle tuerme auf gegebenen slot und eines spielers
	 */
	public List<Tower> getTowersOnSlot(int slot, PlayerColor color) {
		List<Tower> towersOfCity = new LinkedList<Tower>();
		for (Tower tower : towers) {
			if (tower.slot == slot && tower.getOwner() == color) {
				towersOfCity.add(tower);
			}
		}
		return towersOfCity;
	}

	/**
	 * liefert alle tuerme eines spielers
	 */
	public List<Tower> getTowersWithOwner(PlayerColor color) {
		List<Tower> towersOfCity = new LinkedList<Tower>();
		for (Tower tower : towers) {
			if (tower.getOwner() == color) {
				towersOfCity.add(tower);
			}
		}
		return towersOfCity;
	}

	public List<BuildMove> getPossibleMoves() {
		List<BuildMove> moves = new LinkedList<BuildMove>();
		Player player = getCurrentPlayer();

		// liste der verwendbaren segmente erstellen
		List<Segment> segments = new LinkedList<Segment>();
		for (Segment segment : player.getSegments()) {
			if (segment.getUsable() > 0) {
				segments.add(segment);
			}
		}

		// menge der einzigartigen karten erstellen
		Set<Card> cards = new HashSet<Card>(player.getCards());

		if (towers != null) {
			for (Tower tower : towers) {
				for (Card card : cards) {
					if (tower.slot == card.slot) {
						for (Segment segment : segments) {
							if (tower.canAddPart(player, segment.size)) {
								moves.add(new BuildMove(tower.city, tower.slot, segment.size));
							}
						}
					}
				}
			}
		}

		return moves;
	}

	/**
	 * liefert den zuletzt ausgefuehrten zug
	 */
	public Move getLastMove() {
		return lastMove;
	}

	/**
	 * liefert die statusinformationen zu einem gegebenen spieler
	 */
	public int[] getPlayerStats(Player player) {
		assert player != null;
		return getPlayerStats(player.getPlayerColor());
	}

	/**
	 * liefert die statusinformationen zu einem gegebenen spieler
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
	 * liefert die statusinformationen zu beiden spielern
	 */
	public int[][] getGameStats() {

		int[][] stats = new int[2][4];

		int highestHeigth = 0;
		PlayerColor highestOwner = null;
		int[] cityTowers = new int[Constants.CITIES];

		int highestTowerPtr = -1;
		for (Tower tower : getTowers()) {

			if (tower.getHeight() > 0) {

				int ptr = tower.getOwner() == PlayerColor.RED ? 0 : 1;
				int dir = tower.getOwner() == PlayerColor.RED ? 1 : -1;
				cityTowers[tower.city] += dir;
				stats[ptr][0]++;

				if (tower.getHeight() > highestHeigth) {
					highestHeigth = tower.getHeight();
					highestTowerPtr = ptr;
					highestOwner = tower.getOwner();
				} else if (tower.getHeight() == highestHeigth) {
					if (tower.getOwner() != highestOwner) {
						highestTowerPtr = -1;
					}
				}
			}
		}

		for (int i = 0; i < cityTowers.length; i++) {
			if (cityTowers[i] > 0) {
				stats[0][1]++;
			} else if (cityTowers[i] < 0) {
				stats[1][1]++;
			}
		}

		if (highestTowerPtr > -1) {
			stats[highestTowerPtr][2] = 1;
		}

		stats[0][3] = red.getPoints();
		stats[1][3] = blue.getPoints();

		return stats;

	}

	/**
	 * liefert die namen den beiden spieler
	 */
	public String[] getPlayerNames() {
		return new String[] { red.getDisplayName(), blue.getDisplayName() };

	}

	/**
	 * das spiel alsbeendet markieren, gewinner und gewinnggrund festlegen.
	 */
	public void endGame(PlayerColor winner, String reason) {
		if (condition == null) {
			condition = new Condition(winner, reason);
		}
	}

	/**
	 * gibt an, ob das spiel als beendet markeirt wurde.
	 */
	public boolean gameEnded() {
		return condition != null;
	}

	/**
	 * liefert den gewinner des spiels, falls gameEnded() true liefert. sonst
	 * undefiniert.
	 */
	public PlayerColor winner() {
		return condition == null ? null : condition.winner;
	}

	/**
	 * liefert den gewinngrund des spiels, falls gameEnded() true liefert. sonst
	 * undefiniert.
	 */
	public String winningReason() {
		return condition == null ? "" : condition.reason;
	}

}
