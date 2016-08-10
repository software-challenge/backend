package sc.plugin2017;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import sc.plugin2017.util.Constants;
import sc.plugin2017.PlayerColor;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Ein {@code GameState} beinhaltet alle Informationen die den Spielstand zu
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
 * Teilinformationen abgefragt werden. Insbesondere kann mit der Methode
 * {@link #getPossibleActions() getPossibleActions()} eine Liste aller fuer den
 * aktuellen Spieler legalen Teilzuege abgefragt werden. So kann ein Spieleclient
 * diese Liste aus dem {@code GameState} erfragen und muss dann lediglich einen
 * Zug aus dieser Liste auswaehlen.
 * 
 * @author Niklas, Sören
 */
@XStreamAlias(value = "state")
public class GameState implements Cloneable {

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
   * Wurde der Spieler im LastMove abgedrängt. Falls ja ist eine weitere Drehaktion möglich
   */
  private boolean freeTurn;

  /**
   * Endbedingung
   */
  private Condition condition = null;

  /**
   * Erzeugt einen neuen {@code GameState} in dem alle Informationen so gesetzt
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

    currentPlayer = PlayerColor.RED;
    startPlayer = PlayerColor.RED;
    board = new Board();
    freeTurn = false;
  }

  /**
   * erzeugt eine Deepcopy dieses Objekts
   * 
   * @return ein neues Objekt mit gleichen Eigenschaften
   * @throws CloneNotSupportedException falls klonen fehlschlaegt
   */
  @Override
  public Object clone() throws CloneNotSupportedException {
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
    clone.freeTurn = freeTurn;

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
   * Nur für den Server relevant. Gibt das Spielfeld zurueck
   * 
   * @return das Spielfeld
   */
  protected Board getBoard() {
    return this.board;
  }
  
  public Board getVisibleBoard() {
    ArrayList<Tile> tiles = this.board.getVisibleTiles();
    Board visibleBoard = new Board(tiles);
    return visibleBoard;
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
    currentPlayer = currentPlayer == PlayerColor.RED ? PlayerColor.BLUE
        : PlayerColor.RED;
  }

  /**
   * wechselt den Spieler, der den aktuellen Abschnitt begonnen hat.
   */
  /*
   * private void switchStartPlayer() { startPlayer = startPlayer ==
   * PlayerColor.RED ? PlayerColor.BLUE : PlayerColor.RED; }
   */

  /**
   * liefert die aktuelle Zugzahl
   * @return Nummer des aktuellen Zuges (Zaehlung beginnt mit 0)
   */
  public int getTurn() {
    return turn;
  }

  /**
   * Simuliert einen uebergebenen Zug. Dabei werden nur folgende Informationen
   * aktualisiert:
   * <ul>
   * <li>Zugzahl
   * <li>Welcher Spieler an der Reihe ist
   * <li>Was der letzte Zug war
   * <li>die Punkte der Spieler
   * </ul>
   * 
   * @param lastMove
   *          auszufuehrender Zug
   */
  public void prepareNextTurn(Move lastMove) {
    turn++;
    this.lastMove = lastMove;
    int firstTile = Math.min(red.getTile(), blue.getTile());
    int lastTile = Math.max(red.getTile(), blue.getTile());
    for (Tile tile : board.getTiles()) {
      if(tile.getIndex() < firstTile || tile.getIndex() > lastTile + 1) {
        tile.setVisibility(false);
      } else {
        tile.setVisibility(true);
      }
    }
    // wenn auf einen Sandbank abgedrängt wird, gibt es keine Extradrehung
    if(lastMove.containsPushAction() && !(getOtherPlayer().getField(board).getType() == FieldType.SANDBAR)) {
      freeTurn = true;
    } else {
      freeTurn = false;
    }
    this.getCurrentPlayer().setPoints(getPointsForPlayer(currentPlayer));
    switchCurrentPlayer();
    
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
   * Liefert eine Liste aller aktuell erlaubten Teilzuege, des Spielers der
   * aktuell an der Reihe ist.
   * @param coal Anzahl der für die Aktion verbrauchten Kohleeinheiten.
   * @param movement Die Anzahl der Bewegungspunkte, die verwendet werden sollen, falls sich bewegt wird
   * @return Liste erlaubter Teilzuege
   */
  public List<Action> getPossibleActions(int movement, int coal, boolean acceleration) { //TODO Test schreiben
    List<Action> actions = new ArrayList<Action>();
    actions.add(getPossibleMovesInDirection(getCurrentPlayer().getDirection(), movement));
    actions.addAll(getPossibleTurnsWithCoal(coal));
    actions.addAll(getPossiblePushs(movement));
    if(acceleration) {
      actions.addAll(getPossibleAccelerations(coal));
    }
    return actions;
  }

  /**
   * Liefert alle Becshleunigungsaktionen, die höchstens die übergebene Kohlezahl benötigen.
   * @param coal Kohle die für Beschleunigung benötigt wird.
   * @return Liste aller Beschleunigungsaktionen
   */
  public List<Acceleration> getPossibleAccelerations(int coal) {
    ArrayList<Acceleration> acc = new ArrayList<Acceleration>(); 
    for(int i = 0; i <= coal; i++) {
      acc.add(new Acceleration(1 + i));
      acc.add(new Acceleration(-1 - i));
    }
    return acc;
  }

  /**
   * Liefert alle möglichen Abdrängaktionen, die mit den Bewegungspunkten möglich sind.
   * @param movement Anzahl der verfügbaren Bewegungspunkte
   * @return Alle Abdrängaktionen
   */
  public List<Push> getPossiblePushs(int movement) {
    ArrayList<Push> push = new ArrayList<Push>(); 
    Field from = getCurrentPlayer().getField(getVisibleBoard());
    if(from.getType() == FieldType.SANDBAR) { // niemand darf von einer Sandbank berunterpushen.
      return push;
    }
    int direction = getCurrentPlayer().getDirection();
    for(int i = 0;i < 6; i++) {
      Field to = from.getFieldInDirection(i, getVisibleBoard());
      if(to != null && i != GameState.getOppositeDirection(direction) && to.isPassable() && movement >= 1) {
        if(to.getType() == FieldType.LOG && movement >= 2) {
          push.add(new Push(i));
        } else if(to.getType() != FieldType.LOG) {
          push.add(new Push(i));
        }
      }
    }
    return push;
  }

  /**
   * Liefert alle Züge, die höchstens die angegebene Menge an Kohleeinheiten verbrauchen
   * @param coal maximal benötigte Kohleeinheiten
   * @return Liste aller Drehaktionen
   */
  public List<Turn> getPossibleTurnsWithCoal(int coal) {
    ArrayList<Turn> turns = new ArrayList<Turn>(); 
    int start = freeTurn ? 2 : 1;
    for(int i = 0; i <= coal; i++) {
      turns.add(new Turn(start + i));
      turns.add(new Turn(-start - i));
    }
    return turns;
  }

  /**
   * Gibt einen Bewegungsaktion zurück, falls dieser Zug in eine bestimmte Richtung
   * mit einer festen Anzahl von Bewegungspunkten möglich ist. Gibt null zurück, falls Zug nicht möglich
   * @param direction
   * @param movement
   * @return
   */
  public Step getPossibleMovesInDirection(int direction, int movement) {
    Step step = null;
    board = getVisibleBoard();
    Field start = getCurrentPlayer().getField(board);
    int i = 0;
    while(movement > 0) {
      i++;
      Field next = start.getFieldInDirection(direction, board);
      if(next != null && next.isPassable()) {
        movement--;
        if(next.getType() == FieldType.LOG) { // das Überqueren eines Baumstammfeldes verbraucht doppelt so viele Bewegungspunkte
          movement--;
        } else {
          if(next.getType() == FieldType.SANDBAR) {
            step = new Step(i);
            return step;
          }
        }
      } else {
        return step;
      }
    }
    if(movement == 0) { // falls alle Punkte verbraucht wurden, gib den Zug zurück
      step = new Step(i);
    }
    return step;
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
   * <li>[0] - Punktekonto des Spielers (Längste leitung in Spielrichtung)
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
   * Einträgen
   * <ul>
   * <li>[0] - Punktekonto des Spielers (Längste Verbindung in Spielrichtung)
   * </ul>
   * 
   * @param playerColor
   *          Farbe des Spielers
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
   * {@link #getPlayerStats(PlayerColor) Spielerstats}, wobei getGameStats()[0],
   * einem Aufruf von getPlayerStats(PlayerColor.RED) entspricht.
   * 
   * @see #getPlayerStats(PlayerColor)
   * @return Statusinformationen beider Spieler
   */
  public int[][] getGameStats() {

    int[][] stats = new int[2][2];

    stats[0][0] = this.red.getPoints();
    stats[0][1] = this.red.getPassenger();
    stats[1][0] = this.blue.getPoints();
    stats[1][0] = this.blue.getPassenger();
    
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
   * Legt das Spiel als beendet fest, setzt dabei einen Sieger und Gewinngrund
   * 
   * @param winner
   *          Farbe des Siegers
   * @param reason
   *          Gewinngrund
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

  /**
   * Gibt die angezeigte Punktzahl des Spielers zurueck.
   * @param playerColor Farbe des Spielers
   * @return Punktzahl des Spielers
   */
  public int getPointsForPlayer(PlayerColor playerColor) {
    int points = 0;
    if(playerColor.equals(PlayerColor.RED)) {
      points += red.getTile() * Constants.POINTS_PER_TILE;
      points += board.getField(red.getX(), red.getY()).getPoints();
      points += red.getPassenger() * Constants.POINTS_PER_PASSENGER;
    } else {
      points += blue.getTile() * Constants.POINTS_PER_TILE;
      points += board.getField(blue.getX(), blue.getY()).getPoints();
      points += blue.getPassenger() * Constants.POINTS_PER_PASSENGER;
    }
    return points;
  }

  public boolean isFreeTurn() {
    return freeTurn;
  }

  public static int getOppositeDirection(int direction) {
    direction += 3;
    return direction % 6;
  }
  
  /**
   * Setzt ein Schiff auf das Spielfeld und entfernt das alte. Diese Methode ist nur für den
   * Server relevant, da hier keine Fehlerüberprüfung durchgeführt wird. Zum
   * Ausführen von Zügen die
   * {@link sc.plugin2017.Move#perform(GameState, Player) perform}-Methode
   * benutzen.
   * 
   * @param x x-Koordinate
   * @param y y-Koordinate
   *          des Feldes, auf das gesetzt wird
   * @param player der setzende Spieler
   */
  protected void put(int x, int y, Player player) {
    player.put(x, y);
  }

  /**
   * Nur für den Server relevant
   * @param x
   * @param y
   * @param player
   */
  protected void removePassenger(Player player) {
    int x = player.getX();
    int y = player.getY();
    if(board.getField(x, y).getFieldInDirection(0, board).getType() == FieldType.PASSENGER3) {
      board.getField(x, y).getFieldInDirection(0, board).setType(FieldType.BLOCKED);
    } else if(board.getField(x, y).getFieldInDirection(1, board).getType() == FieldType.PASSENGER4) {
      board.getField(x, y).getFieldInDirection(1, board).setType(FieldType.BLOCKED);
    } else if(board.getField(x, y).getFieldInDirection(2, board).getType() == FieldType.PASSENGER5) {
      board.getField(x, y).getFieldInDirection(2, board).setType(FieldType.BLOCKED);
    } else if(board.getField(x, y).getFieldInDirection(3, board).getType() == FieldType.PASSENGER0) {
      board.getField(x, y).getFieldInDirection(3, board).setType(FieldType.BLOCKED);
    } else if(board.getField(x, y).getFieldInDirection(4, board).getType() == FieldType.PASSENGER1) {
      board.getField(x, y).getFieldInDirection(4, board).setType(FieldType.BLOCKED);
    } else if(board.getField(x, y).getFieldInDirection(5, board).getType() == FieldType.PASSENGER2) {
      board.getField(x, y).getFieldInDirection(5, board).setType(FieldType.BLOCKED);
    }
    player.setPassenger(player.getPassenger() + 1);
  }
}














