package sc.plugin2018;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import sc.shared.PlayerColor;
import sc.plugin2018.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Ein {@code GameState} beinhaltet alle Informationen, die den Spielstand zu
 * einem gegebenen Zeitpunkt, das heisst zwischen zwei Spielzuegen, beschreiben.
 * Dies umfasst eine fortlaufende Zugnummer ({@link #getTurn() getTurn()}), die
 * der Spielserver als Antwort von einem der beiden Spieler (
 * {@link #getCurrentPlayer() getCurrentPlayer()}) erwartet. Weiterhin gehoeren
 * die Informationen ueber die beiden Spieler und das Spielfeld zum Zustand.
 * Zuseatzlich wird ueber den zuletzt getaetigeten Spielzung und ggf. ueber das
 * Spielende informiert.
 *
 *
 * Der {@code GameState} ist damit das zentrale Objekt ueber das auf alle
 * wesentlichen Informationen des aktuellen Spiels zugegriffen werden kann.
 *
 *
 * Der Spielserver sendet an beide teilnehmenden Spieler nach jedem getaetigten
 * Zug eine neue Kopie des {@code GameState}, in dem der dann aktuelle Zustand
 * beschrieben wird. Informationen ueber den Spielverlauf sind nur bedingt ueber
 * den {@code GameState} erfragbar und muessen von einem Spielclient daher bei
 * Bedarf selbst mitgeschrieben werden.
 *
 *
 * Zusaetzlich zu den eigentlichen Informationen koennen bestimmte
 * Teilinformationen abgefragt werden.
 *
 * @author Niklas, Sören
 */
@XStreamAlias(value = "state")
public class GameState implements Cloneable {

  @XStreamOmitField
  private static final Logger logger = LoggerFactory
      .getLogger(GameState.class);
  /**
   * momentane Rundenzahl
   */
  @XStreamAsAttribute
  private int turn;

  /**
   * Farbe des Startspielers
   */
  @XStreamAsAttribute
  private PlayerColor startPlayer;

  /**
   * Farbe des aktuellen Spielers
   */
  @XStreamAsAttribute
  private PlayerColor currentPlayer;

  /**
   * der rote Spieler
   */
  private Player red;
  /**
   * der blaue Spieler
   */
  private Player blue;

  /**
   * Das Spielbrett
   */
  private Board board;

  /**
   * letzter getaetigter Zug
   */
  private Move lastMove;

  /**
   * Erzeugt einen neuen {@code GameState}, in dem alle Informationen so gesetzt
   * sind, wie sie zu Beginn eines Spiels, bevor die Spieler beigetreten sind,
   * gueltig sind.
   *
   *
   * Dieser Konstruktor ist nur fuer den Spielserver relevant und sollte vom
   * Spielclient i.A. nicht aufgerufen werden!
   *
   * Das Spielfeld wird zufällig aufgebaut.
   */
  public GameState() {
    this.turn = 0;
    this.currentPlayer = PlayerColor.RED;
    this.startPlayer = PlayerColor.RED;
    this.board = new Board();
    this.red = new Player(PlayerColor.RED);
    this.blue = new Player(PlayerColor.BLUE);
  }

  /**
   * Erzeugt einen neuen {@code GameState} mit denselben Eigenschaften von
   * stateToClone. Fuer eigene Implementierungen.
   */
  protected GameState(GameState stateToClone) throws CloneNotSupportedException {
    GameState clone = stateToClone.clone();
    setRedPlayer(clone.getRedPlayer());
    setBluePlayer(clone.getBluePlayer());
    setLastMove(clone.getLastMove());
    setBoard(clone.getBoard());
    setCurrentPlayer(clone.getCurrentPlayerColor());
  }

  /**
   * erzeugt eine Deepcopy dieses Objekts
   *
   * @return ein neues Objekt mit gleichen Eigenschaften
   * @throws CloneNotSupportedException falls klonen fehlschlaegt
   */
  @Override
  public GameState clone() throws CloneNotSupportedException {
    GameState clone = (GameState) super.clone();
    if (red != null)
      clone.red = this.red.clone();
    if (blue != null)
      clone.blue = this.blue.clone();
    if (lastMove != null)
      clone.lastMove = (Move) this.lastMove.clone();
    if (board != null)
      clone.board = this.board.clone();
    if (currentPlayer != null)
      clone.currentPlayer = currentPlayer;
    clone.turn = this.turn;
    return clone;
  }

  /**
   * Fuegt einem Spiel einen weiteren Spieler hinzu.
   *
   *
   * Diese Methode ist nur fuer den Spielserver relevant und sollte vom
   * Spielclient i.A. nicht aufgerufen werden!
   *
   * @param player
   *          Der hinzuzufuegende Spieler.
   */
  public void addPlayer(Player player) {

    if (player.getPlayerColor() == PlayerColor.RED) {
      red = player;
    } else if (player.getPlayerColor() == PlayerColor.BLUE) {
      blue = player;
    }
  }

  /**
   * Gibt das Spielfeld zurueck
   *
   * @return das Spielfeld
   */
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
    return (currentPlayer == PlayerColor.RED ? red : blue);
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
   * Nur für den Server relevant
   * @param playerColor PlayerColor of new currentPlayer
   */
  protected void setCurrentPlayer(PlayerColor playerColor) {
    this.currentPlayer = playerColor;
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
   * Liefert den Spieler, also eine {@code Player}-Objekt, des Spielers, der dem
   * Spiel als erstes beigetreten ist und demzufolge mit der Farbe
   * {@code PlayerColor.RED} spielt.
   *
   * @return Der rote Spieler.
   */
  public Player getRedPlayer() {
    return red;
  }

  /**
   * Nur für den Server relevant
   * @param red
   */
  protected void setRedPlayer(Player red) {
    this.red = red;
  }

  /**
   * Liefert den Spieler, also eine {@code Player}-Objekt, des Spielers, der dem
   * Spiel als zweites beigetreten ist und demzufolge mit der Farbe
   * {@code PlayerColor.BLUE} spielt.
   *
   * @return Der blaue Spieler.
   */
  public Player getBluePlayer() {
    return blue;
  }

  /**
   * Nur für den Server relevant
   * @param blue
   */
  protected void setBluePlayer(Player blue) {
    this.blue = blue;
  }

  /**
   * Liefert den Spieler, also eine {@code Player}-Objekt, der das Spiel
   * begonnen hat.
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
   * @return Die Farbe des Spielers, der den aktuellen Abschnitt begonnen
   *         hat.
   */
  public PlayerColor getStartPlayerColor() {
    return startPlayer;
  }

  /**
   * wechselt den Spieler, der aktuell an der Reihe ist.
   */
  private void switchCurrentPlayer() {
    // when a new turn starts, the leading player should move first
    if (turn % 2 == 0) {
      currentPlayer = PlayerColor.RED;
    } else {
      currentPlayer = PlayerColor.BLUE;
    }
  }

  /**
   * Überprüft ob ein Feld durch einen anderen Spieler belegt ist.
   * 
   * @param pos
   *            die Position auf der Rennstrecke
   * @return
   */
  public final boolean isOccupied(final int pos)
  {
    return (red.getFieldIndex() == pos || blue.getFieldIndex() == pos)
        && (pos != 64 || pos == 0);
  }

  /**
   * Überprüft ob der angegebene Spieler an erster Stelle ist. Wenn sich beide
   * Spieler im Ziel befinden wird zusätzlich überprüft, ob <code>p</code>
   * weniger Karotten besitzt als der Gegenspieler.
   * 
   * @param p
   * @return
   */
  public final boolean isFirst(final Player p)
  {
    Player o = this.getOpponent(p);
    boolean isFirst = o.getFieldIndex() <= p.getFieldIndex();
    if (p.inGoal() && o.getFieldIndex() == p.getFieldIndex())
      isFirst = isFirst
          && p.getCarrotsAvailable() < o.getCarrotsAvailable();
    return isFirst;
  }
  
  /**
   * Gibt den Feldtypen an einer bestimmten Position zurück. Liegt die
   * gewählte Position vor dem Startpunkt oder hinter dem Ziel, so wird
   * <code>INVALID</code> zurückgegeben.
   * 
   * @param pos
   *            die Position auf der Rennstrecke
   * @return
   */
  public final FieldType getTypeAt(final int pos)
  {
    return board.getTypeAt(pos);
  }
  
  /**
   * Findet das nächste Spielfeld vom Typ <code>type</code> beginnend an
   * Position <code>pos</code> auf diesem Spielbrett.
   * 
   * @param type
   * @param pos
   * @return
   */
  public final int getNextFieldByType(FieldType type, int pos)
  {
    return this.board.getNextFieldByType(type, pos);
  }

  /**
   * @param type
   * @param pos
   * @return
   */
  public final int getPreviousFieldByType(FieldType type, int pos)
  {
    return this.board.getPreviousFieldByType(type, pos);
  }

  public final boolean canEnterGoal(final Player player)
  {
    return player.getCarrotsAvailable() <= 10
        && player.getSalads() == 0;
  }

  /**
   * liefert die aktuelle Zugzahl
   * @return Nummer des aktuellen Zuges (Zaehlung beginnt mit 0)
   */
  public int getTurn() {
    return turn;
  }

  /**
   * Setzt die aktuelle Zugzahl. Nur für den Server relevant
   * @param turn neue Zugzahl
   */
  public void setTurn(int turn) {
    this.turn = turn;
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
   * Liefert Statusinformationen zu einem Spieler als Array mit folgenden
   * Einträgen:
   * <ul>
   * <li>[0] - Punktekonto des Spielers (Flussfortschritt und Passagiere)
   * <li>[1] - Anzahl eingesammelter Passagiere
   * </ul>
   *
   * @param player
   *          Spieler
   * @return Array mit Statistiken
   */
  public int[] getPlayerStats(Player player) {
    assert player != null;
    return getPlayerStats(player.getPlayerColor());
  }

  /**
   * Liefert Statusinformationen zu einem Spieler als Array mit folgenden
   * Einträgen:
   * <ul>
   * <li>[0] - Punktekonto des Spielers (Flussfortschritt und Passagiere)
   * <li>[1] - Anzahl eingesammelter Passagiere
   * </ul>
   *
   * @param playerColor
   *          Farbe des Spielers
   * @return Array mit Statistiken
   */
  public int[] getPlayerStats(PlayerColor playerColor) {
    assert playerColor != null;

    if (playerColor == PlayerColor.RED) {
      return getGameStats()[Constants.GAME_STATS_RED_INDEX];
    } else {
      return getGameStats()[Constants.GAME_STATS_BLUE_INDEX];
    }
  }

  /**
   * Liefert Statusinformationen zum Spiel. Diese sind ein Array der
   * {@link #getPlayerStats(PlayerColor) Spielerstats}, wobei getGameStats()[0],
   * einem Aufruf von getPlayerStats(PlayerColor.RED) entspricht.
   *
   * @see #getPlayerStats(PlayerColor)
   * @return Statusinformationen beider Spieler
   */
  public int[][] getGameStats() {

    int[][] stats = new int[2][2];

    stats[Constants.GAME_STATS_RED_INDEX][Constants.GAME_STATS_FIELD_INDEX] = this.red.getFieldIndex();
    stats[Constants.GAME_STATS_RED_INDEX][Constants.GAME_STATS_CARROTS] = this.red.getCarrotsAvailable();
    stats[Constants.GAME_STATS_BLUE_INDEX][Constants.GAME_STATS_FIELD_INDEX] = this.blue.getFieldIndex();
    stats[Constants.GAME_STATS_BLUE_INDEX][Constants.GAME_STATS_CARROTS] = this.blue.getCarrotsAvailable();
    return stats;

  }

  /**
   * liefert die Namen den beiden Spieler
   * @return Namen der Spieler
   */
  public String[] getPlayerNames() {
    return new String[] { red.getDisplayName(), blue.getDisplayName() };

  }

  /**
   * Gibt die angezeigte Punktzahl des Spielers zurueck.
   * @param playerColor Farbe des Spielers
   * @return Punktzahl des Spielers
   */
  public int getPointsForPlayer(PlayerColor playerColor) {
    if (playerColor == PlayerColor.RED) {
      return this.getRedPlayer().getFieldIndex();
    } else {
      return this.getBluePlayer().getFieldIndex();
    }
  }

  /**
   * Ueberschreibt das aktuelle Spielbrett. Fuer eigene Implementierungen.
   */
  protected void setBoard(Board newValue) {
    board = newValue;
  }
  
  public Player getOpponent(Player player) {
    if (player.getPlayerColor() == PlayerColor.RED) {
      return this.getBluePlayer();
    } else {
      return this.getRedPlayer();
    }
  }

  /**
   * Changes current Player if necessary
   */
  public void updateCurrentPlayer() {
    if(this.currentPlayer == PlayerColor.RED) {
      this.currentPlayer = PlayerColor.BLUE;
    } else {
      this.currentPlayer = PlayerColor.RED;
    }
  }

  /**
   * Setzt letzten Zug. Nur für den Server relevant.
   * @param lastMove letzter Zug
   */
  protected void setLastMove(Move lastMove) {
    this.lastMove = lastMove;
  }

  /**
   * Setzt letzte Aktion eines Spielers. Für den Server in der Zugvalidierung relevant.
   * @param action letzte Aktion
   */
  public void setLastAction(Action action) {
    if (action instanceof Skip) {
      return;
    }
    if (this.getCurrentPlayerColor() == PlayerColor.RED) {
      this.getRedPlayer().setLastNonSkipAction(action);
    } else {
      this.getBluePlayer().setLastNonSkipAction(action);
    }
  }

  /**
   * Gibt den letzten Zugzurück
   * @return letzter Zug
   */
  public Move getLastMove() {
    return this.lastMove;
  }


  /**
   * Gibt die letzte Aktion des Spielers zurück. Nötig für das erkennen von ungültigen Zügen.
   * @param player Spieler
   * @return letzte Aktion die nicht Skip war
   */
  public Action getLastNonSkipAction(Player player) {
    return getLastNonSkipAction(player.getPlayerColor());
  }

  /**
   * Gibt die letzte Aktion des Spielers der entsprechenden Farbe zurück. Nötig für das erkennen von ungültigen Zügen.
   * @param playerColor Spielerfarbe
   * @return letzte Aktion die nicht Skip war
   */
  public Action getLastNonSkipAction(PlayerColor playerColor) {
    if (playerColor == PlayerColor.RED) {
      return this.getRedPlayer().getLastNonSkipAction();
    } else {
      return this.getBluePlayer().getLastNonSkipAction();
    }
  }

  public FieldType fieldOfCurrentPlayer() {
    return this.getTypeAt(this.getCurrentPlayer().getFieldIndex());
  }

  /**
   * Überprüft ob sich der derzeitige Spieler auf einem Hasenfeld befindet.
   * @return true, falls auf Hasenfeld
   */
  public boolean isOnRabbitField()
  {
    return fieldOfCurrentPlayer().equals(FieldType.RABBIT);
  }

  /**
   * GIbt FIRST, SECOND oder TIE zurück je nachdem, wie die Spieler zueinadner stehen
   * @return Position
   */
  public Position getGameResult()
  {
    Position ret;
    if (this.getOtherPlayer().getFieldIndex() < this.getCurrentPlayer().getFieldIndex())
    {
      ret = Position.FIRST;
    }
    else if (this.getOtherPlayer().getFieldIndex() > this.getCurrentPlayer().getFieldIndex())
    {
      ret = Position.SECOND;
    }
    else
    // Beide Spieler auf dem gleichen Spielfeld (Ziel)
    {
      // nachrangiges Kriterium: Anzahl der Karotten, je weniger desto
      // besser
      if (this.getOtherPlayer().getCarrotsAvailable() > this.getCurrentPlayer().getCarrotsAvailable())
      {
        ret = Position.FIRST;
      }
      else if (this.getOtherPlayer().getCarrotsAvailable() < this.getCurrentPlayer().getCarrotsAvailable())
      {
        ret = Position.SECOND;
      }
      else
      {
        ret = Position.TIE;
      }
    }
    return ret;
  }
}
