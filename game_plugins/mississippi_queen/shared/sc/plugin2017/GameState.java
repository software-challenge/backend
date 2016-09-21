package sc.plugin2017;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import sc.plugin2017.util.Constants;
import sc.plugin2017.util.InvalidMoveException;

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
   * Der Index des Segmentes am weitesten vom Start entfernt welches bisher aufgedeckt wurde. Wird nur intern verwendet.
   */
  private int latestTileIndex = 0;

  /**
   * Wurde der Spieler im LastMove abgedrängt. Falls ja ist eine weitere Drehaktion möglich
   */
  @XStreamAsAttribute
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
  public GameState clone() throws CloneNotSupportedException {
    GameState clone = (GameState) super.clone();
    if (red != null)
      clone.red = (Player) this.red.clone();
    if (blue != null)
      clone.blue = (Player) this.blue.clone();
    if (lastMove != null)
      clone.lastMove = (Move) this.lastMove.clone();
    if (board != null)
      clone.board = this.board.clone();
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
  public Board getBoard() {
    return this.board;
  }

  public Board getVisibleBoard() {
    Board visibleBoard = new Board(false);
    for (Tile tile : board.getTiles()) {
      if(tile.isVisible()) {
        visibleBoard.getTiles().add(tile);
      }
    }
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
    // when a new turn starts, the leading player should move first
    if (turn % 2 == 0) {
      currentPlayer = getLeadingPlayer().getPlayerColor();
    } else {
      currentPlayer = currentPlayer == PlayerColor.RED ? PlayerColor.BLUE : PlayerColor.RED;
    }
  }

  private Player getLeadingPlayer() {
    int redPoints = 0;
    int bluePoints = 0;
    redPoints += red.getTile() * Constants.POINTS_PER_TILE;
    redPoints += board.getField(red.getX(), red.getY()).getPoints();

    bluePoints += blue.getTile() * Constants.POINTS_PER_TILE;
    bluePoints += board.getField(blue.getX(), blue.getY()).getPoints();

    // points are equal to the distance to the goal. Who is nearer to the goal is considered leading
    if (redPoints > bluePoints) {
      return red;
    } else if (bluePoints > redPoints) {
      return blue;
    } else {
      // if both have the same distance to the goal, the one with more speed is leading
      if (red.getSpeed() > blue.getSpeed()) {
        return red;
      } else if (blue.getSpeed() > red.getSpeed()) {
        return blue;
      } else {
        // if both have same speed, the one with more coal is leading
        if (red.getCoal() > blue.getCoal()) {
          return red;
        } else if (blue.getCoal() > red.getCoal()) {
          return blue;
        } else {
          // if both have same coal, choose player which is farer right or farer down
          // this should be unambiguous considering the distance to the goal is equal for both players
          if (red.getX() > blue.getX()) {
            return red;
          } else if (blue.getX() > red.getX()) {
            return blue;
          } else {
            if (red.getY() > blue.getY()) {
              return red;
            } else {
              return blue;
            }
          }
        }
      }
    }

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
    latestTileIndex = Math.max(latestTileIndex, Math.max(red.getTile(), blue.getTile()));
    for (Tile tile : board.getTiles()) {
      if(tile.getIndex() < firstTile || tile.getIndex() > latestTileIndex + 1) {
        tile.setVisibility(false);
      } else {
        tile.setVisibility(true);
      }
    }
    // wenn auf einen Sandbank abgedrängt wird, gibt es keine zusaetzliche Drehung
    if(lastMove.containsPushAction() && !(getOtherPlayer().getField(board).getType() == FieldType.SANDBANK)) {
      freeTurn = true;
      this.getOtherPlayer().setFreeTurns(2);
    } else {
      freeTurn = false;
      this.getOtherPlayer().setFreeTurns(1);
    }
    this.getCurrentPlayer().setMovement(getCurrentPlayer().getSpeed());
    this.getCurrentPlayer().setMovement(getOtherPlayer().getSpeed());
    this.getOtherPlayer().setFreeAcc(1);
    this.getCurrentPlayer().setFreeAcc(1);
    this.getCurrentPlayer().setPoints(getPointsForPlayer(currentPlayer));
    this.getOtherPlayer().setPoints(getPointsForPlayer(getOtherPlayerColor()));
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
   * Liefert eine Liste aller aktuell erlaubten Teilzuege eines Spielers.
   * @param player Spieler für den die Aktionen sind.
   * @param movement Die Anzahl der Bewegungspunkte, die höchstens verwendet werden sollen (sollte kleiner als player.speed sein)
   * @param coal Anzahl der für die Aktion verbrauchten Kohleeinheiten.
   * @param acceleration Gibt an, ob Beschleunigungszüge möglich sein sollen
   * @param freeTurn ist eine freie Drehung verfügbar
   * @return Liste erlaubter Teilzuege
   */
  public List<Action> getPossibleActions(Player player, int movement, int coal, boolean acceleration, boolean freeTurn) { //TODO Test schreiben
    List<Action> actions = new ArrayList<Action>();
    actions.addAll(getPossibleMovesInDirection(player, movement, player.getDirection(), coal));
    actions.addAll(getPossibleTurnsWithCoal(player, freeTurn, coal));
    actions.addAll(getPossiblePushs(player, movement));
    if(acceleration) {
      actions.addAll(getPossibleAccelerations(player, coal));
    }
    return actions;
  }

  /**
   * Liefert alle Becshleunigungsaktionen, die höchstens die übergebene Kohlezahl benötigen.
   * @param player Spieler
   * @param coal Kohle die für Beschleunigung benötigt wird.
   * @return Liste aller Beschleunigungsaktionen
   */
  public List<Acceleration> getPossibleAccelerations(Player player, int coal) {
    ArrayList<Acceleration> acc = new ArrayList<Acceleration>();
    for(int i = 0; i <= coal; i++) {
      if(player.getSpeed() < 6 - i) {
        acc.add(new Acceleration(1 + i)); // es wird nicht zu viel beschleunigt
      }
      if(player.getSpeed() > 1 + i) {
        acc.add(new Acceleration(-1 - i)); // aber zu viel abgebremst
      }
    }
    return acc;
  }

  /**
   * Liefert alle möglichen Abdrängaktionen, die mit den Bewegungspunkten möglich sind.
   * @param player Spieler
   * @param movement Anzahl der verfügbaren Bewegungspunkte
   * @return Alle Abdrängaktionen
   */
  public List<Push> getPossiblePushs(Player player, int movement) {
    ArrayList<Push> push = new ArrayList<Push>();
    Field from = player.getField(getVisibleBoard());
    if(from.getType() == FieldType.SANDBANK) { // niemand darf von einer Sandbank herunterpushen.
      return push;
    }
    Direction direction = player.getDirection();
    for(Direction i: Direction.values()) {
      Field to = from.getFieldInDirection(i, getVisibleBoard());
      if(to != null && i != direction.getOpposite() && to.isPassable() && movement >= 1) {
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
   * @param player Spieler
   * @param freeTurn Ist eine freie Drehung verfügbar?
   * @param coal maximal benötigte Kohleeinheiten
   * @return Liste aller Drehaktionen
   */
  public List<Turn> getPossibleTurnsWithCoal(Player player, boolean freeTurn, int coal) {
    ArrayList<Turn> turns = new ArrayList<Turn>();
    if(player.getField(board).getType() == FieldType.SANDBANK) {
      return turns;
    }
    int start = freeTurn ? 2 : 1;
    for(int i = 0; i <= coal; i++) {
      turns.add(new Turn(start + i));
      turns.add(new Turn(-start - i));
    }
    return turns;
  }

  /**
   * Gibt alle Bewegungsaktionn zurück, die in die Richtung des Spielers
   * mit einer festen Anzahl von Bewegungspunkten möglich sind.
   * @param player Spieler
   * @param movement Anzahl
   * @param direction Richtung
   * @param coal Kohleeinheite die zur Verfügung stehen
   * @return Liste aller möglichen Züge des Spielers in entsprechende Richtung
   */
  public List<Advance> getPossibleMovesInDirection(Player player, int movement, Direction direction, int coal) {
    ArrayList<Advance> step = new ArrayList<Advance>();
    Field start = player.getField(board);
    int i = 0;
    Player enemy = player.getPlayerColor() == PlayerColor.RED ? blue : red;
    if(start.getType() == FieldType.SANDBANK && movement > 0) {
      if(start.getFieldInDirection(direction.getOpposite(), this.board).isPassable()) {
        step.add(new Advance(-1));
      }
      if(coal > 0 || start.getFieldInDirection(direction, this.board).isPassable()) {
        step.add(new Advance(1));
      }
      return step;
    }
    while(movement > 0) {
      i++;
      Field next = start.getFieldInDirection(direction, board);
      if(next != null && next.isPassable()) {
        movement--;
        if(next.getType() == FieldType.LOG) { // das Überqueren eines Baumstammfeldes verbraucht doppelt so viele Bewegungspunkte
          movement--;
          if(movement >= 0) {
            step.add(new Advance(i));
          }
        } else {
          if(movement >= 0) {
            step.add(new Advance(i));
          }
          if(next.getType() == FieldType.SANDBANK || next.equals(enemy.getField(board))) {
            return step;
          }
        }
      } else {
        return step;
      }
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

  /**
   * Setzt ein Schiff auf das Spielfeld und entfernt das alte. Diese Methode ist nur für den
   * Server relevant, da hier keine Fehlerüberprüfung durchgeführt wird. Zum
   * Ausführen von Zügen die
   * {@link sc.plugin2017.Move#perform(GameState, Player) perform}-Methode
   * benutzen. Es wird hier ebenfalls das Spielsegment auf dem sich der Spieler befindet aktualisiert
   *
   * @param x x-Koordinate
   * @param y y-Koordinate
   *          des Feldes, auf das gesetzt wird
   * @param player der setzende Spieler
   */
  protected void put(int x, int y, Player player) {
    int tileNumber = 0;
    for (Tile tile : board.getTiles()) {
      if(tile.getField(x, y) != null) {
        tileNumber = tile.getIndex();
        break;
      }
    }
    player.put(x, y, tileNumber);
  }

  /**
   * Nur für den Server relevant
   * @param player removes passenger
   */
  protected void removePassenger(Player player) {
    int x = player.getX();
    int y = player.getY();
    if(board.getField(x,y).getFieldInDirection(Direction.RIGHT, board) != null &&
        board.getField(x, y).getFieldInDirection(Direction.RIGHT, board).getType() == FieldType.PASSENGER3) {
      board.getField(x, y).getFieldInDirection(Direction.RIGHT, board).setType(FieldType.BLOCKED);
    } else if(board.getField(x,y).getFieldInDirection(Direction.UP_RIGHT, board) != null &&
        board.getField(x, y).getFieldInDirection(Direction.UP_RIGHT, board).getType() == FieldType.PASSENGER4) {
      board.getField(x, y).getFieldInDirection(Direction.UP_RIGHT, board).setType(FieldType.BLOCKED);
    } else if(board.getField(x,y).getFieldInDirection(Direction.UP_LEFT, board) != null &&
        board.getField(x, y).getFieldInDirection(Direction.UP_LEFT, board).getType() == FieldType.PASSENGER5) {
      board.getField(x, y).getFieldInDirection(Direction.UP_LEFT, board).setType(FieldType.BLOCKED);
    } else if(board.getField(x,y).getFieldInDirection(Direction.LEFT, board) != null &&
        board.getField(x, y).getFieldInDirection(Direction.LEFT, board).getType() == FieldType.PASSENGER0) {
      board.getField(x, y).getFieldInDirection(Direction.LEFT, board).setType(FieldType.BLOCKED);
    } else if(board.getField(x,y).getFieldInDirection(Direction.DOWN_LEFT, board) != null &&
        board.getField(x, y).getFieldInDirection(Direction.DOWN_LEFT, board).getType() == FieldType.PASSENGER1) {
      board.getField(x, y).getFieldInDirection(Direction.DOWN_LEFT, board).setType(FieldType.BLOCKED);
    } else if(board.getField(x,y).getFieldInDirection(Direction.DOWN_RIGHT, board) != null &&
        board.getField(x, y).getFieldInDirection(Direction.DOWN_RIGHT, board).getType() == FieldType.PASSENGER2) {
      board.getField(x, y).getFieldInDirection(Direction.DOWN_RIGHT, board).setType(FieldType.BLOCKED);
    }
    player.setPassenger(player.getPassenger() + 1);
  }

  public GameState getVisible() {
    GameState clone = null;
    try {
      clone = this.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    clone.board = new Board(false);
    for(int i = 0; i < Constants.NUMBER_OF_TILES; i++) {
      Tile newTile = board.getTiles().get(i);
      if(newTile.isVisible()) {
        clone.board.getTiles().add(newTile);
      }
    }
    return clone;
  }


  /**
   * Berechent wie viele Gewegungpunkte und Kohleeinheiten der Zug des benötigt
   * Es wird hier davon ausgegangen, dass der Zug möglich ist. Gibt {-1,-1} zurück, falls
   * Zug ungültig ist.
   * @param player Spieler
   * @param freeTurn freie Drehung
   * @param move Zug
   * @return Gewegungspunkte und Kohle
   */
  public int[] getCost(Player player, boolean freeTurn, Move move) {
    int[] cost = new int[2];
    GameState clone = null;
    try {
      clone = this.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    for(Action action : move.actions) {
      if(action.getClass() == Advance.class) { // case Step
        Advance o = (Advance) action;
        cost[0] += o.distance;
        Field start = player.getField(clone.board);
        if(start.getType() == FieldType.SANDBANK) {
          cost[1] += 1;
        }
        for(int i = 1; i <= o.distance; i++) {
          Field next = start.getFieldInDirection(player.getDirection(), clone.board);
          if(next.getType() == FieldType.LOG) {
            cost[0]++;
          }
          start = next;
        }

      } else if(action.getClass() == Acceleration.class) { // case Acceleration
        Acceleration o = (Acceleration) action;
        if(o.acc > 1 || o.acc < -1) {
          cost[1] += Math.abs(o.acc) - 1;
        }
      } else if(action.getClass() == Turn.class) { // case Turn
        Turn o = (Turn) action;
        if(Math.abs(o.direction) > 1 + (freeTurn ? 1 : 0)) {
          cost[1] += Math.abs(o.direction) - (1 + (freeTurn ? 1 : 0));
        }
      } else if(action.getClass() == Push.class) { // case Push
        Push o = (Push) action;
        if(player.getField(clone.board).getFieldInDirection(o.direction, clone.board).getType() == FieldType.LOG) {
          cost[0] += 2;
        } else {
          cost[0] += 1;
        }
      }
      try {
        action.perform(clone, player);
      } catch (InvalidMoveException e) {

        e.printStackTrace();
        int[] array = {-1,-1};
        return array;
      }
    }
    return cost;
  }

  @Override
  public String toString() {
    return "GameState: freeTurn = " + freeTurn + " currentColor: " + currentPlayer + "\n" + board + "\n" + lastMove;
  }

}














