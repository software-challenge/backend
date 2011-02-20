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
 * ein spielzustand beinhaltet die liste der spielfelder spielfiguren, die zur
 * verfuegung stehenden wurfel und die teilnehmenden spieler
 * 
 * @author ffa, sca, tkra
 * 
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
	 * setzt einen neuen spieler und gibt ihm siene starthand
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
	 * liefert den spieler der momentan am zug ist
	 */
	public Player getCurrentPlayer() {
		return currentPlayer == PlayerColor.RED ? red : blue;
	}

	/**
	 * liefert den spieler der momentan am zug ist
	 */
	public PlayerColor getCurrentPlayerColor() {
		return currentPlayer;
	}

	/**
	 * liefert den gegenspieler des aktiven spielers
	 */
	public Player getOtherPlayer() {
		return currentPlayer == PlayerColor.RED ? blue : red;
	}

	/**
	 * liefert den ersten/roten spieler
	 */
	public Player getRedPlayer() {
		return red;
	}

	/**
	 * liefert den zweiten/blauenm spieler
	 */
	public Player getBluePlayer() {
		return blue;
	}

	/**
	 * wechselt den aktuellen startspieler
	 */
	private void switchCurrentPlayer() {
		currentPlayer = currentPlayer == PlayerColor.RED ? PlayerColor.BLUE : PlayerColor.RED;
	}

	/**
	 * wechselt den aktuellen startspieler
	 */
	private void switchStartPlayer() {
		startPlayer = startPlayer == PlayerColor.RED ? PlayerColor.BLUE : PlayerColor.RED;
	}

	/**
	 * leifert den aktuellen startspieler
	 */
	public PlayerColor getStartPlayer() {
		return startPlayer;
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
			} else if (getCurrentPlayer().getSegmentCount() == 0) {
				setCurrentMoveType(MoveType.SELECT);
				switchStartPlayer();
				currentPlayer = startPlayer;
				performScoring();
			} else {
				switchCurrentPlayer();
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
					highestTower = tower;
					ambiguous = false;
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
			cardStack.addAll(createCardStack());
		}

		return cardStack.remove(0);

	}

	private static List<Card> createCardStack() {

		List<Card> cardStack = new ArrayList<Card>(SLOTS * CARDS_PER_SLOT);

		for (int slot = 0; slot < SLOTS; slot++) {
			for (int card = 0; card < CARDS_PER_SLOT; card++) {
				cardStack.add(new Card(slot));
			}
		}

		Collections.shuffle(cardStack, new SecureRandom());
		return cardStack;

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

		int[][] stats = new int[2][1];

		// TODO

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
