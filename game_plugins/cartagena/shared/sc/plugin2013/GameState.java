package sc.plugin2013;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import sc.plugin2013.util.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Ein {@code GameState} beinhaltet alle Informationen die den Spielstand zu
 * einem gegebenen Zeitpunkt, das heisst zwischen zwei Spielzuegen, beschreiben.
 * Dies umfasst neben einer fortlaufenden Zugnumer ({@link #getTurn() getTurn()}
 * ), Informationen über die Position der Spielfiguren auf dem Spielbrett, sowie
 * Informationen über die Spieler.
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
 * aktuellen Spieler legalen Zuege abgefragt werden. Ist momentan also eine Zug
 * zu taetigen, kann eine Spieleclient diese Liste aus dem {@code GameState}
 * erfragen und muss dann lediglich einen Zug aus dieser Liste auswaehlen.
 * 
 * @author felix
 * 
 */
/**
 * @author felix
 *
 */
/**
 * @author felix
 *
 */
@XStreamAlias(value = "cartagena:state")
public class GameState implements Cloneable {

	// momentane rundenzahl
	private int turn;

	// die teilenhmenden spieler
	private Player red, blue;

	// Farbe des aktiven Spielers
	private PlayerColor currentPlayer;

	// Kartenstapel
	private List<Card> cardStack;

	// offen liegende Karten
	private List<Card> openCards;

	// verbrauchte Karten
	private List<Card> usedStack;

	// letzter Performter move
	private Move lastMove;

	// das Spielbrett
	private Board board;

	// endbedingung
	private Condition condition = null;

	public GameState() {
		currentPlayer = PlayerColor.RED;
		cardStack = new LinkedList<Card>();
		openCards = new ArrayList<Card>(Constants.NUM_OPEN_CARDS);
		usedStack = new LinkedList<Card>();
		initCardStack();
		board = new Board();
		// TODO implementieren
	}

	/**
	 * Initialisiert den KartenStapel
	 */
	private synchronized void initCardStack() {
		// For each symbol
		for (SymbolType symbol : SymbolType.values()) {
			for (int i = 0; i < Constants.CARDS_PER_SYMBOL; i++) {
				// add CARDS_PER_SYMBOL Cards to stack
				cardStack.add(new Card(symbol));
			}
		}
		// shuffle Stack
		Collections.shuffle(cardStack, new SecureRandom());
	}

	/**
	 * Zieht bis zu 12 Karten vom Stapel und legt diese offen hin
	 */
	private synchronized void showCards() {
		// draw as many cards from Stack to fill out 12
		for (int i = openCards.size(); i < Constants.NUM_OPEN_CARDS; i++) {
			if (cardStack.isEmpty()) {
				mixCardStack();
			}
			openCards.add(cardStack.remove(0));
		}
	}

	/**
	 * Mischt die verbrauchten Karten und legt diese auf den unverbrauchten
	 * Stapel
	 */
	private synchronized void mixCardStack() {
		cardStack.clear();
		for (Card c : usedStack) {
			cardStack.add(c);
		}
		usedStack.clear();
		Collections.shuffle(cardStack, new SecureRandom());
	}

	/**
	 * Fügt einen Spieler hinzu
	 */
	public void addPlayer(Player player) {
		if (player.getPlayerColor() == PlayerColor.RED) {
			this.red = player;
		} else if (player.getPlayerColor() == PlayerColor.BLUE) {
			this.blue = player;
		}

		// Draw initial Cards for Player
		for (int i = 0; i < Constants.INIT_CARDS_PER_PLAYER; i++) {
			player.addCard(cardStack.remove(0));
		}

	}

	public Player getCurrentPlayer() {
		return currentPlayer == PlayerColor.RED ? this.red : this.blue;
	}

	/**
	 * setzt den aktuellen spieler
	 */
	public void setCurrentPlayer(PlayerColor p) {
		assert p == PlayerColor.RED || p == PlayerColor.BLUE;
		this.currentPlayer = p;
	}

	/**
	 * liefert den gegenspieler des aktiven spielers
	 */
	public Player getOtherPlayer() {
		return currentPlayer == PlayerColor.RED ? this.blue : this.blue;
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

	public String[] getPlayerNames() {
		return new String[] { red.getDisplayName(), blue.getDisplayName() };
	}

	public int getTurn() {
		return this.turn;
	}

	public void endGame(PlayerColor opponent, String err) {
		// TODO Auto-generated method stub

	}

	// TODO implement GameState
}
