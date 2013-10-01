package sc.plugin2013;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

//import sc.plugin2013.util.GameStateConverter;
import sc.plugin2013.util.Constants;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

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
 * @author fdu
 * 
 */
@XStreamAlias(value = "state")
// @XStreamConverter(GameStateConverter.class)
public class GameState implements Cloneable {

	/**
	 * Die aktuelle Zugnummer
	 * 
	 */
	@XStreamAsAttribute
	private int turn;

	/**
	 * Die Spielerobjekte
	 * 
	 */
	private Player red, blue;

	/**
	 * Die Farbe des aktiven Spielers
	 * 
	 */
	@XStreamAsAttribute
	private PlayerColor currentPlayer;

	/**
	 * Der verdeckte Kartenstapel
	 */
	@XStreamOmitField
	private transient List<Card> cardStack;

	/**
	 * Die 12 offen liegenden Karten
	 */
	private List<Card> openCards;

	/**
	 * Der Stapel mit den verbrauchten Karten
	 */
	@XStreamOmitField
	private transient List<Card> usedStack;

	/**
	 * Der zuletzt durchgeführte Zug
	 */
	private MoveContainer lastMove;

	/**
	 * Das Spielbrett
	 */
	private Board board;

	/**
	 * Die Endbedingung
	 */
	private Condition condition = null;

	/**
	 * Erzeugt einen neuen {@code GameState} in dem alle Informationen so
	 * gesetzt sind, wie sie zu Beginn eines Spiels, bevor die Spieler
	 * beigetreten sind, gueltig sind.<br/>
	 * <br/>
	 * 
	 * <b>Dieser Konstruktor ist nur fuer den Spielserver relevant und sollte
	 * vom Spielclient i.A. nicht aufgerufen werden!</b>
	 * 
	 * Der Kartenstapel wird nur initialisiert und nicht mit Karten befuellt.
	 */
	public GameState() {
		this(true);
	}

	/**
	 * Erzeugt einen neuen {@code GameState} in dem alle Informationen so
	 * gesetzt sind, wie sie zu Beginn eines Spiels, bevor die Spieler
	 * beigetreten sind, gueltig sind.<br/>
	 * <br/>
	 * 
	 * <b>Dieser Konstruktor ist nur fuer den Spielserver relevant und sollte
	 * vom Spielclient i.A. nicht aufgerufen werden!</b>
	 * 
	 * @param suppressStack
	 *            Gibt an ob der Kartenstapel nur initialisiert oder auch mit
	 *            Karten gefuellt werden soll.
	 */
	public GameState(boolean suppressStack) {
		currentPlayer = PlayerColor.RED;
		cardStack = new LinkedList<Card>();
		openCards = new ArrayList<Card>(Constants.NUM_OPEN_CARDS);
		usedStack = new LinkedList<Card>();
		if (!suppressStack) {
			initCardStack();
			showCards();
		}
		board = new Board();
	}

	/**
	 * Erzeugt eine deep copy dieses Objektes. Alle enthaltenen Attribute und
	 * Objekte werden ebenfalls geklont
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		GameState clone = (GameState) super.clone();
		if (red != null)
			clone.red = (Player) red.clone();
		if (blue != null)
			clone.blue = (Player) blue.clone();
		if (lastMove != null)
			clone.lastMove = (MoveContainer) lastMove.clone();
		if (cardStack != null) {
			clone.cardStack = new LinkedList<Card>();
			for (Card c : cardStack) {
				clone.cardStack.add((Card) c.clone());
			}
		}
		if (usedStack != null) {
			clone.usedStack = new LinkedList<Card>();
			for (Card c : usedStack) {
				clone.usedStack.add((Card) c.clone());
			}
		}
		if (openCards != null) {
			clone.openCards = new LinkedList<Card>();
			for (Card c : openCards) {
				clone.openCards.add((Card) c.clone());
			}
		}
		if (condition != null)
			clone.condition = (Condition) this.condition.clone();
		if (board != null)
			clone.board = (Board) this.board.clone();
		if (currentPlayer != null)
			clone.currentPlayer = currentPlayer;
		return clone;
	}

	/**
	 * Initialisiert den KartenStapel
	 */
	protected synchronized void initCardStack() {
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
	 * Used to initialize OmittedFields cardStack and used Stack. <b>Diese
	 * Methode ist nur fuer die Deserialisierung relevant und sollte vom
	 * Spielclient nicht aufgerufen werden!</b>
	 * 
	 * @return
	 */
	private Object readResolve() {
		cardStack = new LinkedList<Card>();
		usedStack = new LinkedList<Card>();
		return this;
	}

	/**
	 * Zieht bis zu 12 Karten vom Stapel und legt diese offen hin
	 * 
	 * <b>Diese Methode ist nur fuer die Spieleserver relevant und sollte vom
	 * Spielclient nicht aufgerufen werden!</b>
	 * 
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
	 * Stapel.
	 * 
	 * <b>Diese Methode ist nur fuer die Spieleserver relevant und sollte vom
	 * Spielclient nicht aufgerufen werden!</b>
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
	 * Zieht eine Karte vom offenen Stapel
	 * 
	 * @return die erste Karte des offen Stapels
	 */
	public synchronized Card drawCard() {
		return this.openCards.remove(0);
	}

	/**
	 * Legt eine Karte auf dem benutzten Kartenstapel ab
	 * 
	 * @param c
	 *            die benutzte Karte
	 */
	public synchronized void addUsedCard(Card c) {
		this.usedStack.add(c);
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
			if (this.red == null) {
				this.red = player;
				// Draw initial Cards for Player
				for (int i = 0; i < Constants.INIT_CARDS_PER_PLAYER; i++) {
					player.addCard(cardStack.remove(0));
				}
			} else{
				//Karten auffüllen
				for(Card c:red.getCards()){
					player.addCard(c);
				}
				//Punktestand sezten
				player.setPoints(red.getPoints());
				this.red = player;				
			}
		} else if (player.getPlayerColor() == PlayerColor.BLUE) {
			if (this.blue == null) {
				this.blue = player;
				for (int i = 0; i < Constants.INIT_CARDS_PER_PLAYER; i++) {
					player.addCard(cardStack.remove(0));
				}
			} else {
				//Karten auffüllen
				for(Card c:blue.getCards()){
					player.addCard(c);
				}
				//Punktestand sezten
				player.setPoints(red.getPoints());
				this.blue = player;
			}
		}
	}

	/**
	 * Liefert den Spieler, also ein {@link Player}-Objekt, der momentan am Zug
	 * ist.
	 * 
	 * @return Der Spieler, der momentan am Zug ist.
	 */
	public Player getCurrentPlayer() {
		return currentPlayer == PlayerColor.RED ? this.red : this.blue;
	}

	/**
	 * Liefert die {@link PlayerColor}-Farbe des Spielers, der momentan am Zug
	 * ist. Dies ist äquivalent zum Aufruf
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
	 * Zug ist. Dies ist aequivalent zum Aufruf @
	 * {@code getCurrentPlayerColor.opponent()} oder
	 * {@code getOtherPlayer().getPlayerColor()}, aber etwas effizienter.
	 * 
	 * @return Die Farbe des Spielers, der momentan nicht am Zug ist.
	 */
	public PlayerColor getOtherPlayerColor() {
		return currentPlayer.opponent();
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
	 * Wechselt den Spieler, der aktuell an der Reihe ist.
	 * 
	 * <b>Diese Methode ist nur fuer den Spielserver relevant und sollte vom
	 * Spielclient i.A. nicht aufgerufen werden!</b>
	 */
	private void switchCurrentPlayer() {
		currentPlayer = currentPlayer == PlayerColor.RED ? PlayerColor.BLUE
				: PlayerColor.RED;
	}

	/**
	 * Gibt an, ob das Spiel beendet ist.
	 * 
	 * @return wahr, wenn beendet
	 */
	public boolean gameEnded() {
		return condition != null;
	}

	/**
	 * Liefert die Farbe des Siegers, falls das Spiel beendet ist.
	 * 
	 * @see #gameEnded()
	 * @return Siegerfarbe
	 */
	public PlayerColor winner() {
		return condition == null ? null : condition.winner;
	}

	/**
	 * Liefert die Namen der Spieler in einem String Array
	 * 
	 * @return ein String Array mit den Spierlnamen
	 */
	public String[] getPlayerNames() {
		return new String[] { red.getDisplayName(), blue.getDisplayName() };
	}

	/**
	 * Liefert die aktuelle Zugzahl
	 * 
	 * @return
	 */
	public int getTurn() {
		return this.turn;
	}

	/**
	 * Liefert die aktuelle Runde zurück.
	 * 
	 * @return die aktuelle Runde
	 */
	public int getRound() {
		return turn / 2;
	}

	/**
	 * Gibt den Kartenstapel zurück Nur für den Server relevant
	 * 
	 * @return
	 */
	public List<Card> getCardStack() {
		return cardStack;
	}

	/**
	 * Gibt den Stapel der benutzten Karten zurück Nur für den Server relevant
	 * 
	 * @return
	 */
	public List<Card> getUsedStack() {
		return usedStack;
	}

	/**
	 * Beendet das aktuelle Spiel
	 * 
	 * @param winner
	 *            Der Gewinner
	 * @param reason
	 *            Der Siegesgrund
	 */
	public void endGame(PlayerColor winner, String reason) {
		if (condition == null) {
			condition = new Condition(winner, reason);
		}

	}

	/**
	 * Gibt das Spielbrett zurück
	 * 
	 * @return Das Spielbrett
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * Aktualisiert den Spielzustand welcher durch einen Zug verändert wird
	 * Dabei werden folgende Informationen aktualisiert:
	 * <ul>
	 * <li>Zugzahl
	 * <li>Welcher Spieler an der Reihe ist
	 * <li>Was der letzte Zug war
	 * <li>die Punkte der Spieler
	 * <li>die nächsten ziehbaren Karten
	 * </ul>
	 * 
	 * @param lastMove
	 */
	public void prepareNextTurn(MoveContainer lastMove) {
		turn++;
		this.lastMove = lastMove;
		switchCurrentPlayer();
		showCards();
		performScoring();
	}

	/**
	 * Wird von der Gui benutz um einen Teilzug durchzuführen. Hierbei wird die
	 * Punktzahl aktualisiert. Der Momentane Spieler bleibt gleich, die Zugzahl
	 * wird nicht erhöht.
	 * 
	 * @param move
	 */
	public void prepareNextTurn(Move move) {
		performScoring();
	}

	/**
	 * Aktualisiert die Punkte der Spieler. Dabei wird das Spielbrett
	 * durchgegangen und die Piraten der einzelnen Spieler in den Segmenten
	 * gezählt
	 */
	private void performScoring() {
		// Scoring wird je Segment vergeben
		// Pirat in Segment 1 = 1 Punkt ...
		int scoreRed = 0;
		int scoreBlue = 0;
		for (int i = 1; i <= Constants.SEGMENTS * 6 + 1; i++) {
			Field field = this.board.getField(i);
			int redPirates = field.numPirates(PlayerColor.RED);
			int bluePirates = field.numPirates(PlayerColor.BLUE);
			scoreRed += redPirates * (((i - 1) / Constants.SYMBOLS) + 1);
			scoreBlue += bluePirates * (((i - 1) / Constants.SYMBOLS) + 1);
		}
		getBluePlayer().setPoints(scoreBlue);
		getRedPlayer().setPoints(scoreRed);
	}

	/**
	 * Überprüft ob ein Spieler alle Piraten im Zielfeld hat.
	 * 
	 * @param color
	 *            die Farbe des Spielers
	 * @return true wenn der Spieler alle Piraten im Zilfeld hat
	 */
	public boolean playerFinished(PlayerColor color) {
		if (this.board.numPiratesOf(Constants.SEGMENTS * Constants.SYMBOLS + 1,
				color) == Constants.PIRATES) {
			return true;
		}
		return false;
	}

	/**
	 * Gibt die Liste der offen liegenden Karten zurück
	 * 
	 * @return
	 */
	public List<Card> getOpenCards() {
		return openCards;
	}

	/**
	 * Gibt den zuletzt ausgeführten Zug zurück
	 * 
	 * @return
	 */
	public MoveContainer getLastMove() {
		return lastMove;
	}

	/**
	 * Gibt den Gewinngrund zurück
	 * 
	 * @return
	 */
	public String winningReason() {
		return condition == null ? "" : condition.reason;
	}

	/**
	 * Gibt eine Liste aller Züge zurück, welche der Spieler, welcher momentan
	 * am Zug ist durchführen kann diese können sowohl Vorwärts-, als auch
	 * Rückwärtszüge sein.
	 * 
	 * @return LinkedList der durchführbaren Züge.
	 */
	public List<Move> getPossibleMoves() {
		List<Move> possibleMoves = new LinkedList<Move>();
		Player player = getCurrentPlayer();

		Set<Card> cards = new HashSet<Card>(player.getCards());

		for (int i = 0; i < board.size(); i++) {
			if (board.hasPirates(i, player.getPlayerColor())) {
				if (board.getPreviousField(i) != -1) {
					possibleMoves.add(new BackwardMove(i));
				}
				if (i != board.size() - 1) {
					for (Card c : cards) {
						possibleMoves.add(new ForwardMove(i, c.symbol));
					}
				}
			}
		}

		return possibleMoves;
	}
}
