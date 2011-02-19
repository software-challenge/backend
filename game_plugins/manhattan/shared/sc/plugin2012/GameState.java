package sc.plugin2012;

import static sc.plugin2012.util.Constants.CARDS_PER_PLAYER;
import static sc.plugin2012.util.Constants.MAX_SEGMENT_SIZE;
import static sc.plugin2012.util.Constants.SEGMENT_AMOUNTS;
import static sc.plugin2012.util.GameStateHelper.createCardStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import sc.plugin2012.Condition;
import sc.plugin2012.PlayerColor;
import sc.plugin2012.util.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * ein spielzustand beinhaltet die liste der spielfelder spielfiguren, die zur
 * verfuegung stehenden wurfel und die teilnehmenden spieler
 * 
 * @author ffa, sca, tkra
 * 
 */
@XStreamAlias(value = "mh:state")
public final class GameState implements Cloneable {

	// momentane rundenzahl
	@XStreamAsAttribute
	private int turn;

	// farbe des startspielers
	@XStreamAsAttribute
	private PlayerColor startPlayer;

	// farbe des aktuellen spielers
	@XStreamAsAttribute
	private PlayerColor currentPlayer;

	// momentan auszufuehrender zug-type
	@XStreamAsAttribute
	private MoveType currentMoveType;

	// die teilenhmenden spieler
	@XStreamImplicit(itemFieldName = "player")
	private final List<Player> player;

	// kartenstapel
	@XStreamOmitField
	private final List<Card> cardStack;

	// listre der gebauten tuerem
	@XStreamImplicit(itemFieldName = "tower")
	private final List<Tower> towers;

	// letzter performte move
	private Move lastMove;

	// endbedingung
	private Condition condition = null;

	public GameState() {

		// liste mit zwei plaetzen initialisieren
		player = new ArrayList<Player>(2);
		player.add(null);
		player.add(null);

		currentPlayer = PlayerColor.RED;
		startPlayer = PlayerColor.RED;
		currentMoveType = MoveType.SELECT;
		cardStack = new LinkedList<Card>();
		towers = new LinkedList<Tower>();
		for (int city = 0; city < Constants.CITIES; city++) {
			for (int slot = 0; slot < Constants.SLOTS_PER_CITY; slot++) {
				towers.add(new Tower(city, slot));
			}
		}

	}

	/**
	 * setzt einen neuen spieler und gibt ihm siene starthand
	 */
	public void addPlayer(Player player) {

		if (player.getPlayerColor() == PlayerColor.RED) {
			this.player.set(0, player);
		} else if (player.getPlayerColor() == PlayerColor.BLUE) {
			this.player.set(1, player);
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
		return currentPlayer == PlayerColor.RED ? player.get(0) : player.get(1);
	}

	/**
	 * liefert den gegenspieler des aktiven spielers
	 */
	public Player getOtherPlayer() {
		return currentPlayer == PlayerColor.RED ? player.get(1) : player.get(0);
	}

	/**
	 * liefert den ersten/roten spieler
	 */
	public Player getRedPlayer() {
		return player.get(0);
	}

	/**
	 * liefert den zweiten/blauenm spieler
	 */
	public Player getBluePlayer() {
		return player.get(1);
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

	/**
	 * liefert den turm an einer gegebenen position, falls vorhanden, null
	 * andernfalls
	 */
	public Tower getTower(int city, int slot) {
		for (Tower tower : towers) {
			if (tower.city == city && tower.slot == slot) {
				return tower;
			}
		}
		return null;
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
		return new String[] { player.get(0).getDisplayName(), player.get(1).getDisplayName() };

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
