package sc.plugin2014;

import static sc.plugin2014.util.Constants.*;
import java.security.SecureRandom;
import java.util.*;
import sc.plugin2014.util.Constants;
import sc.plugin2014.util.GameStateConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

/**
 * Ein {@code GameState} beinhaltet alle Informationen die den Spielstand zu
 * einem gegebenen Zeitpunkt, das heisst zwischen zwei Spielzuegen, beschreiben.
 * Dies umfasst eine fortlaufende Zugnummer ({@link #getTurn() getTurn()}) und
 * was fuer eine Art von Zug ({@link #getCurrentMoveType() getCurrentMoveType()}
 * ) der Spielserver als Antwort von einem der beiden Spieler (
 * {@link #getCurrentPlayer() getCurrentPlayer()}) erwartet. Weiterhin gehoeren
 * die Informationen ueber die beiden Spieler und alle moeglichen Tuerme zum
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
 * abgefragt werden. Insbesondere kann mit der Methode
 * {@link #getPossibleMoves() getPossibleMoves()} eine Liste aller fuer den
 * aktuellen Spieler legalen Bauzuege abgefragt werden. Ist momentan also eine
 * Bauzug zu taetigen, kann eine Spieleclient diese Liste aus dem
 * {@code GameState} erfragen und muss dann lediglich einen Zug aus dieser Liste
 * auswaehlen.
 * 
 * @author tkra
 */
@XStreamAlias(value = "manhattan:state")
@XStreamConverter(GameStateConverter.class)
public class GameState implements Cloneable {

    // momentane rundenzahl
    private int         turn;

    // farbe des startspielers
    private PlayerColor startPlayer;

    // farbe des aktuellen spielers
    private PlayerColor currentPlayer;

    // momentan auszufuehrender zug-type
    private MoveType    currentMoveType;

    // die teilenhmenden spieler
    private Player      red, blue;

    // kartenstapel
    private List<Stone> stoneStack;

    // liste der gebauten tuerem
    private List<Stone> towers;

    // letzter performte move
    private Move        lastMove;

    // endbedingung
    private Condition   condition = null;

    /**
     * Erzeugt einen neuen {@code GameState} in dem alle Informationen so
     * gesetzt
     * sind, wie sie zu Beginn eines Spiels, bevor die Spieler beigetreten sind,
     * gueltig sind.<br/>
     * <br/>
     * 
     * <b>Dieser Konstruktor ist nur fuer den Spielserver relevant und sollte
     * vom
     * Spielclient i.A. nicht aufgerufen werden!</b>
     * 
     * Der Kartenstapel wird nur initialisiert und nicht mit Karten befuellt.
     */
    public GameState() {
        this(true);
    }

    /**
     * Erzeugt einen neuen {@code GameState} in dem alle Informationen so
     * gesetzt
     * sind, wie sie zu Beginn eines Spiels, bevor die Spieler beigetreten sind,
     * gueltig sind.<br/>
     * <br/>
     * 
     * <b>Dieser Konstruktor ist nur fuer den Spielserver relevant und sollte
     * vom
     * Spielclient i.A. nicht aufgerufen werden!</b>
     * 
     * @param suppressStack
     *            Gibt an ob der Kartenstapel nur initialisiert oder auch mit
     *            Karten gefuellt werden soll.
     */
    public GameState(boolean suppressStack) {

        currentPlayer = PlayerColor.RED;
        startPlayer = PlayerColor.RED;
        currentMoveType = MoveType.SELECT;
        stoneStack = new LinkedList<Stone>();
        if (!suppressStack) {
            createCardStack();
        }
        towers = new ArrayList<Stone>(Constants.CITIES * Constants.SLOTS);
        for (int city = 0; city < Constants.CITIES; city++) {
            for (int slot = 0; slot < Constants.SLOTS; slot++) {
                towers.add(new Stone(city, slot));
            }
        }

    }

    /**
     * klont dieses Objekt
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
        if (condition != null) {
            clone.condition = (Condition) condition.clone();
        }
        if (stoneStack != null) {
            clone.stoneStack = new LinkedList<Stone>(stoneStack);
        }
        if (towers != null) {
            clone.towers = new LinkedList<Stone>(towers);
        }
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
     *            Der hinzuzufuegende Spieler.
     */
    public void addPlayer(Player player) {

        if (player.getPlayerColor() == PlayerColor.RED) {
            red = player;
        }
        else if (player.getPlayerColor() == PlayerColor.BLUE) {
            blue = player;
        }

        for (int i = 0; i < STONES_PER_PLAYER; i++) {
            player.addCard(drawCard());
        }
        usedStack.clear();

        for (int i = 1; i <= MAX_SEGMENT_SIZE; i++) {
            player.addSegmet(new Segment(i, SEGMENT_AMOUNTS[i - 1]));
        }

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
     * ist. Dies ist aequivalent zum Aufruf
     * {@code getCurrentPlayer().getPlayerColor()}, aber etwas effizienter.
     * 
     * @return Die Farbe des Spielers, der momentan am Zug ist.
     */
    public PlayerColor getCurrentPlayerColor() {
        return currentPlayer;
    }

    /**
     * Liefert den Spieler, also ein {@code Player}-Objekt, der momentan nicht
     * am
     * Zug ist.
     * 
     * @return Der Spieler, der momentan nicht am Zug ist.
     */
    public Player getOtherPlayer() {
        return currentPlayer == PlayerColor.RED ? blue : red;
    }

    /**
     * Liefert die {@code PlayerColor}-Farbe des Spielers, der momentan nicht am
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
     * Liefert den Spieler, also eine {@code Player}-Objekt, des Spielers, der
     * dem Spiel als erstes beigetreten ist und demzufolge mit der Farbe
     * {@code PlayerColor.RED} spielt.
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
     * Abschnitt begonnen hat. Dies ist aequivalent zum Aufruf
     * {@code getStartPlayer().getPlayerColor()}, aber etwas effizienter.
     * 
     * @return Die Farbe des Spielers, der den aktuellen Abschnitt nicht
     *         begonnen
     *         hat.
     */
    public PlayerColor getStartPlayerColor() {
        return startPlayer;
    }

    /**
     * wechselt den Spieler, der aktuell an der Reihe ist.
     */
    private void switchCurrentPlayer() {
        currentPlayer = currentPlayer == PlayerColor.RED ? PlayerColor.BLUE
                : PlayerColor.RED;
    }

    /**
     * wechselt den Spieler, der den aktuellen Abschnitt begonnen hat.
     */
    private void switchStartPlayer() {
        startPlayer = startPlayer == PlayerColor.RED ? PlayerColor.BLUE
                : PlayerColor.RED;
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
     *            auszufuehrender Zug
     */
    public void prepareNextTurn(Move lastMove) {

        turn++;
        this.lastMove = lastMove;
        if (currentMoveType == MoveType.SELECT) {
            if (currentPlayer == startPlayer) {
                switchCurrentPlayer();
            }
            else {
                setCurrentMoveType(MoveType.BUILD);
            }
        }
        else {
            usedStack.add(new Card(((LayMove) lastMove).slot));
            if ((currentPlayer == startPlayer)
                    && (getCurrentPlayer().getUsableSegmentCount() == 0)) {
                setCurrentMoveType(MoveType.SELECT);
                switchCurrentPlayer();
                switchStartPlayer();
                performScoring();
            }
            else {
                switchCurrentPlayer();
            }
        }

    }

    /**
	 * 
	 */
    private void performScoring() {

        int[][] stats = getGameStats();

        red.addPoints(Constants.POINTS_PER_TOWER * stats[0][0]);
        red.addPoints(Constants.POINTS_PER_OWEND_CITY * stats[0][1]);
        red.addPoints(Constants.POINTS_PER_HIGHEST_TOWER * stats[0][2]);

        blue.addPoints(Constants.POINTS_PER_TOWER * stats[1][0]);
        blue.addPoints(Constants.POINTS_PER_OWEND_CITY * stats[1][1]);
        blue.addPoints(Constants.POINTS_PER_HIGHEST_TOWER * stats[1][2]);

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
     * liefert die naechste Karte vom Stapel. Dazu wird dieser gegebenenfalls
     * neu
     * aufgefuellt durch mischen der verbauchten Karten.
     * 
     * @return naechste Karte
     */
    public synchronized Card drawCard() {
        if (cardStack.isEmpty()) {
            mixCardStack();
        }
        return cardStack.remove(0);
    }

    /**
     * erstellt einen stapel mit ALLEN karten.
     */
    private synchronized void createCardStack() {
        for (int slot = 0; slot < (Constants.SLOTS * Constants.CARDS_PER_SLOT); slot++) {
            cardStack.add(new Card(slot % Constants.SLOTS));
        }
        Collections.shuffle(cardStack, new SecureRandom());
    }

    /**
     * Mischt neuen Stapel aus verbrauchten Karten
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
     * Liefert den Turm an gegebenem Feld (Stadt, Position)
     * 
     * @param city
     *            Stadt des Spielfeldes
     * @param slot
     *            Position des Spielfeldes
     * @return Turm an gegebenem Feld, kann null sein oder Hoehe 0 haben.
     */
    public Stone getTower(int city, int slot) {
        if ((city < 0) || (city >= Constants.CITIES)) {
            if ((slot < 0) || (slot >= Constants.SLOTS)) {
                throw new IllegalArgumentException("no such tower: city "
                        + city + ", slot " + slot);
            }
        }

        return towers.get((city * Constants.SLOTS) + slot);

    }

    /**
     * Liefert eine Liste aller Tuerme
     * 
     * @return Liste aller Tuerme
     */
    public List<Stone> getTowers() {
        List<Stone> towersOfAllCities = new LinkedList<Stone>();

        if (towers != null) {
            for (Stone tower : towers) {
                towersOfAllCities.add(tower);
            }
        }
        return towersOfAllCities;
    }

    /**
     * Liefert eine Liste aller aktuell erlaubten Zuege.
     * 
     * @return Liste erlaubter Spielzuege
     */
    public List<LayMove> getPossibleMoves() {

        return null; // TODO
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
     * <li>[0] - Anzahl Tuerme des Spielers
     * <li>[1] - Anzahl Staedte des SPielers
     * <li>[2] - 1: Spieler hat hoechsten Turm, 0: sonst
     * <li>[3] - Punktekonto des Spielers
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
     * <li>[0] - Anzahl Tuerme des Spielers
     * <li>[1] - Anzahl Staedte des SPielers
     * <li>[2] - 1: Spieler hat hoechsten Turm, 0: sonst
     * <li>[3] - Punktekonto des Spielers
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
        }
        else {
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

        int[][] stats = new int[2][4]; // TODO

        stats[0][3] = red.getPoints();
        stats[1][3] = blue.getPoints();

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
     *            Farbe des Siegers
     * @param reason
     *            Gewinngrund
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
