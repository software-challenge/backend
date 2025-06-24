package sc.plugin2017;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import sc.plugin2017.util.Constants;
import sc.plugin2017.util.InvalidMoveException;

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
   * Der Index des am weitesten vom Start entfernten Segmentes, welches bisher aufgedeckt wurde. Wird nur intern verwendet.
   */
  @XStreamOmitField
  private int latestTileIndex = 0;

  /**
   * Wurde der Spieler im LastMove abgedrängt. Falls ja ist eine weitere Drehaktion möglich
   */
  @XStreamAsAttribute
  private boolean freeTurn;

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
    currentPlayer = PlayerColor.RED;
    startPlayer = PlayerColor.RED;
    board = new Board();
    freeTurn = false;
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
    setFreeTurn(clone.getFreeTurn());
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
   * Gibt das Spielfeld zurueck
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
      currentPlayer = getLeadingPlayer().getPlayerColor();
    } else {
      currentPlayer = currentPlayer == PlayerColor.RED ? PlayerColor.BLUE : PlayerColor.RED;
    }
  }

  private Player getLeadingPlayer() {
    // points are equal to the distance to the goal. Who is nearer to the goal is considered leading
    int redDistanceFromBlue = distanceRedFromBlue();
    logger.debug("DistanceRedFromBlue = " + redDistanceFromBlue);
    if (redDistanceFromBlue > 0) {
      return red;
    } else if (redDistanceFromBlue < 0) {
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
          // if both have same coal, choose player which is farer right or farer down (highest x / highest y coordinate)
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
   * returns how far in front the red player is compared to the blue player. negative number means blue is in front
   */
  private int distanceRedFromBlue() {
    ArrayList<Tile> tiles = board.getVisibleTiles();
    int direction = tiles.get(tiles.size()-1).getDirection();
    return distanceBetweenPoints(direction, red.getX(), red.getY(), blue.getX(), blue.getY());
  }

  /**
   * Gibt die Distanz zwischen zwei gegebenen Punkten auf dem Feld zurück, gemessen an der angegebenen Richtung
   * @param direction die Richtung (0 - 5)
   * @param column1 x-Koordinate des ersten Feldes
   * @param row1 y-Koordinate des ersten Feldes
   * @param column2 x-Koordinate des zweiten Feldes
   * @param row2 y-Koordinate des zweiten Feldes
   * @return die Distanz
   */
  public static int distanceBetweenPoints(int direction, int column1, int row1, int column2, int row2) {
  //convert to cube coordinates, easier to use here
    int x1 = column1 - (row1 + (row1&1)) / 2;
    int z1 = row1;
    int y1 = -x1 - z1;

    int x2 = column2 - (row2 + (row2&1)) / 2;
    int z2 = row2;
    int y2 = -x2 - z2;
    switch(direction) {
    case 0:
      return (x1 - x2) - (y1 - y2); //dX - dY is the distance
    case 1:
      return (x1 - x2) - (z1 - z2);
    case 2:
      return (y1 - y2) - (z1 - z2);
    case 3:
      return (y1 - y2) - (x1 - x2);
    case 4:
      return (z1 - z2) - (x1 - x2);
    default: // case 5
      return (z1 - z2) - (y1 - y2);
    }
  }

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
   * <li>Welcher Spieler an der Reihe ist (currentPlayer)
   * <li>Was der letzte Zug war (lastMove)
   * <li>die Punkte der Spieler (points)
   * <li>die Bewegungspunkte der Spieler (movement)
   * <li>die freien Drehungen (freeTurns) sowohl im GameState als auch von den Spielern
   * <li>die freien Beschleunigungen (freeAccs)
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
    // get an extra free turn after getting pushed (except if pushed on a sandbank)
    if(lastMove.containsPushAction() && !(getOtherPlayer().getField(board).getType() == FieldType.SANDBANK)) {
      this.getOtherPlayer().setFreeTurns(2);
    }
    this.getOtherPlayer().setMovement(getOtherPlayer().getSpeed());
    this.getOtherPlayer().setFreeAcc(1);
    this.getOtherPlayer().setPoints(getPointsForPlayer(getOtherPlayerColor()));

    this.getCurrentPlayer().setFreeTurns(1);
    this.getCurrentPlayer().setMovement(getCurrentPlayer().getSpeed());
    this.getCurrentPlayer().setFreeAcc(1);
    this.getCurrentPlayer().setPoints(getPointsForPlayer(currentPlayer));
    switchCurrentPlayer();
    // free turns has to be set for the current player, because the next player might not be the opponent (overtake)
    if(getCurrentPlayer().getFreeTurns() == 2) {
      freeTurn = true;
    } else {
      freeTurn = false;
    }
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
   * @param movement Die Anzahl der Bewegungspunkte, die höchstens verwendet werden sollen (sollte kleiner oder gleich player.speed sein)
   * @param coal Anzahl der für die Aktion verbrauchten Kohleeinheiten.
   * @param acceleration Gibt an, ob Beschleunigungszüge möglich sein sollen
   * @param freeTurn ist eine freie Drehung verfügbar
   * @return Liste erlaubter Teilzuege
   */
  public List<Action> getPossibleActions(Player player, int movement, int coal, boolean acceleration, boolean freeTurn) { //TODO Test schreiben
    List<Action> actions = new ArrayList<Action>();
    Player otherPlayer = player.getPlayerColor().opponent() == PlayerColor.RED ? red : blue;
    if(player.getX() == otherPlayer.getX() && player.getY() == otherPlayer.getY()) {
      actions.addAll(getPossiblePushs(player, movement));
    } else {
      actions.addAll(getPossibleMovesInDirection(player, movement, player.getDirection(), coal));
      actions.addAll(getPossibleTurnsWithCoal(player, freeTurn, coal));
      if(acceleration) {
        actions.addAll(getPossibleAccelerations(player, coal));
      }
    }
    return actions;
  }

  /**
   * Liefert alle Beschleunigungsaktionen, die höchstens die übergebene Kohlezahl benötigen.
   * @param player Spieler
   * @param coal Kohle, die für die Beschleunigung benötigt werden darf.
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
   * Liefert alle sinnvollen Drehaktionen, die höchstens die angegebene Menge an Kohleeinheiten verbrauchen
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
    int freeTurns = freeTurn ? 2 : 1;
    int maxTurn = Math.min(3, coal + freeTurns);
    for(int i = 1; i <= maxTurn; i++) {
      turns.add(new Turn(i));
      turns.add(new Turn(-i));
    }
    return turns;
  }

  /**
   * Gibt alle Bewegungsaktionen des Spielers zurück, die in die gegebene Richtung
   * mit einer festen Anzahl von Bewegungspunkten möglich sind.
   * @param player Spieler
   * @param movement Geschwindigkeit
   * @param direction Richtung
   * @param coal Kohleeinheiten, die zur Verfügung stehen
   * @return Liste aller möglichen Züge des Spielers in entsprechende Richtung
   */
  public List<Advance> getPossibleMovesInDirection(Player player, int movement, Direction direction, int coal) {
    ArrayList<Advance> step = new ArrayList<Advance>();
    Field start = player.getField(board);
    int i = 0;
    Player enemy = player.getPlayerColor() == PlayerColor.RED ? blue : red;
    if(start.getType() == FieldType.SANDBANK && movement > 0) {

      Field fieldBehind = start.getFieldInDirection(direction.getOpposite(), this.board);
      if(fieldBehind != null && fieldBehind.isPassable()) {
        step.add(new Advance(-1));
      }
      Field fieldInFront = start.getFieldInDirection(direction, this.board);
      if(fieldInFront != null && fieldInFront.isPassable()) {
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

    stats[Constants.GAME_STATS_RED_INDEX][Constants.GAME_STATS_POINTS_INDEX] = this.red.getPoints();
    stats[Constants.GAME_STATS_RED_INDEX][Constants.GAME_STATS_PASSENGER_INDEX] = this.red.getPassenger();
    stats[Constants.GAME_STATS_BLUE_INDEX][Constants.GAME_STATS_POINTS_INDEX] = this.blue.getPoints();
    stats[Constants.GAME_STATS_BLUE_INDEX][Constants.GAME_STATS_PASSENGER_INDEX] = this.blue.getPassenger();
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
    for(int i = 0; i < board.getTiles().size(); i++) {
      Tile newTile = board.getTiles().get(i);
      if(newTile.isVisible()) {
        clone.board.getTiles().add(newTile);
      }
    }
    return clone;
  }


  /**
   * Berechent, wie viele Bewegungpunkte und Kohleeinheiten der Zug benötigt.
   * Es wird hier davon ausgegangen, dass der Zug möglich ist. Gibt {-1,-1} zurück, falls der
   * Zug ungültig ist.
   * @param player Spieler
   * @param freeTurn freie Drehung
   * @param move Zug
   * @return Array, welches benötigte Bewegungspunkte (Index 0) und Kohle (Index 1) enthält
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
    return "GameState: \n Spieler1: " + red + " \n" + "Spieler2: " + blue + "\n" + "freeTurn = " + freeTurn + " currentColor: " + currentPlayer + "\n" + board + "\n" + lastMove;
  }

  /**
   * Ueberschreibt den letzten Zug. Fuer eigene Implementierungen.
   */
  protected void setLastMove(Move move) {
    lastMove = move;
  }

  /**
   * Fuer eigene Implementierungen.
   *
   * @return true, falls der aktuelle Spieler eine freie Drehung machen darf,
   *         sonst false.
   */
  public boolean getFreeTurn() {
    return freeTurn;
  }

  /**
   * Ueberschreibt den Wert fuer freeTurn. Fuer eigene Implementierungen.
   */
  protected void setFreeTurn(boolean newValue) {
    freeTurn = newValue;
  }

  /**
   * Ueberschreibt das aktuelle Spielbrett. Fuer eigene Implementierungen.
   */
  protected void setBoard(Board newValue) {
    board = newValue;
  }
}
