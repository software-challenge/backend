package sc.plugin2019;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.plugin2019.util.Constants;
import sc.shared.InvalidGameStateException;
import sc.shared.InvalidMoveException;
import sc.shared.PlayerColor;

import java.util.*;

import static sc.plugin2019.util.Constants.BOARD_SIZE;
import static sc.plugin2019.util.Constants.MAX_FISH;
import static sc.plugin2019.util.GameRuleLogic.isValidToMove;

/**
 * Ein {@code GameState} beinhaltet alle Informationen, die den Spielstand zu
 * einem gegebenen Zeitpunkt, das heisst zwischen zwei Spielzuegen, beschreiben.
 * Dies umfasst eine fortlaufende Zugnummer ({@link #getTurn() getRound()}), die
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
  private static final Logger logger = LoggerFactory.getLogger(GameState.class);
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
   * stateToClone. Fuer eigene Implementierungen.<br>
   * Hinweis: Es ist effizienter, direkt das Ergebnis von {@link #clone()} zu verwenden
   */
  protected GameState(GameState stateToClone) {
    GameState clone = stateToClone.clone();
    setRedPlayer(clone.getPlayer(PlayerColor.RED));
    setBluePlayer(clone.getPlayer(PlayerColor.BLUE));
    setLastMove(clone.getLastMove());
    setBoard(clone.getBoard());
    setCurrentPlayer(clone.getCurrentPlayerColor());
  }

  /**
   * erzeugt eine Deepcopy dieses Objekts
   *
   * @return ein neues Objekt mit gleichen Eigenschaften
   */
  @Override
  public GameState clone()  {
    GameState clone;
    try {
      clone = (GameState) super.clone();
    } catch (CloneNotSupportedException e) {
      // impossible
      throw new RuntimeException("Cloning of GameState failed!", e);
    }
    if (red != null)
      clone.red = this.red.clone();
    if (blue != null)
      clone.blue = this.blue.clone();
    if (lastMove != null)
      clone.lastMove = this.lastMove.clone();
    if (board != null)
      clone.board = this.board.clone();
    if (currentPlayer != null)
      clone.currentPlayer = this.currentPlayer;
    clone.startPlayer = this.startPlayer;
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
   * Liefert den Spieler als {@code Player}-Objekt, der als die entsprechende Farbe spielt
   * @param color die Farbe des gefragten Spielers
   */
  public Player getPlayer(PlayerColor color) {
    return color == PlayerColor.RED ? red : blue;
  }

  /**
   * Liefert den Spieler, also ein {@code Player}-Objekt, der momentan am Zug
   * ist.
   *
   * @return Der Spieler, der momentan am Zug ist.
   */
  public Player getCurrentPlayer() {
    return getPlayer(currentPlayer);
  }

  /**
   * Liefert die {@code PlayerColor}-Farbe des Spielers, der momentan am Zug
   * ist. Dies ist aequivalent zum Aufruf
   * {@code getCurrentPlayer().getPlayerColor()}, aber schneller.
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
    return getPlayer(getOtherPlayerColor());
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
   * Nur für den Server relevant
   * @param red roter Spieler
   */
  protected void setRedPlayer(Player red) {
    this.red = red;
  }

  /**
   * Nur für den Server relevant
   * @param blue blauer Spieler
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
   * wechselt den Spieler, der aktuell an der Reihe ist anhand der Anzahl der Züge <code>turn</code>
   */
  public void switchCurrentPlayer() {
    if (turn % 2 == 0) {
      currentPlayer = PlayerColor.RED;
    } else {
      currentPlayer = PlayerColor.BLUE;
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
   * Setzt die aktuelle Zugzahl. Nur für den Server relevant
   * @param turn neue Zugzahl
   */
  public void setTurn(int turn) throws InvalidGameStateException {
    int turnLimit = Constants.ROUND_LIMIT * 2;
    if (turn > turnLimit) {
      throw new InvalidGameStateException("Turn " + turn + " exceeded maxTurn " + turnLimit);
    }
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
   * <li>[0] - Punktekonto des Spielers (Größe des Schwarms)
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

    int[][] stats = new int[2][1];

    stats[Constants.GAME_STATS_RED_INDEX][Constants.GAME_STATS_SWARM_SIZE] = this.getPointsForPlayer(PlayerColor.RED);
    stats[Constants.GAME_STATS_BLUE_INDEX][Constants.GAME_STATS_SWARM_SIZE] = this.getPointsForPlayer(PlayerColor.BLUE);
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
    return greatestSwarmSize(playerColor);
  }

  /**
   * Ueberschreibt das aktuelle Spielbrett. Fuer eigene Implementierungen.
   */
  protected void setBoard(Board newValue) {
    board = newValue;
  }

  public Player getOpponent(Player player) {
    return getPlayer(player.getPlayerColor().opponent());
  }

  /**
   * Setzt letzten Zug. Nur für den Server relevant.
   * @param lastMove letzter Zug
   */
  protected void setLastMove(Move lastMove) {
    this.lastMove = lastMove;
  }

  /**
   * Gibt den letzten Zugzurück
   * @return letzter Zug
   */
  public Move getLastMove() {
    return this.lastMove;
  }

  /**
   * Gibt eine Liste aller möglichen Züge zurück
   * @return Liste von Move Objekten
   */
  public ArrayList<Move> getPossibleMoves() {
    ArrayList<Move> possibleMoves = new ArrayList<>();
    Set<Field> fields = getOwnFields(getCurrentPlayer());

    for(Field field : fields){
      for(Direction direction : Direction.values()){
        int x = field.getX();
        int y = field.getY();
        int dist = calculateMoveDistance(x, y, direction);
        try {
          if (dist > 0 && isValidToMove(x,y,direction,dist,this)){
            Move m = new Move(x,y,direction);
            possibleMoves.add(m);
          }
        } catch (InvalidMoveException ignore) {}
      }
    }

    return possibleMoves;
  }

  @Override
  public String toString() {
    return "GameState:\n"
        + "turn=" + this.getTurn() + this.getCurrentPlayer()
        + this.red + this.blue
        + this.board
        + this.getLastMove();

  }

  public Set<Field> getOwnFields(PlayerColor player){
    Set<Field> fields = new HashSet<>();
    int size = 0;
    for(int i = 0; i < BOARD_SIZE && MAX_FISH > size; i++){
      for(int j = 0; j < BOARD_SIZE && MAX_FISH > size; j++){
        Field curField = getBoard().getField(i,j);
        if (curField.getPiranha().isPresent() && curField.getPiranha().get().equals(player)){
          fields.add(curField);
          size++;
        }
      }
    }
    return fields;
  }

  private Set<Field> getDirectNeighbour(Field f, Set<Field> parentSet){
    Set<Field> returnSet = new HashSet<>();
    Board b = getBoard();
    for(int i = -1; i <= 1; i++){
      for(int j = -1; j <= 1; j++){
        int x = f.getX()+i;
        int y = f.getY()+j;
        if (x < 0 || x >= Constants.BOARD_SIZE || y < 0 || y >= Constants.BOARD_SIZE || (i==0 && j==0)) continue;

        Field field = b.getField(x,y);
        if (parentSet.contains(field)){
          returnSet.add(field);
        }
      }
    }
    return returnSet;
  }

  public Set<Field> getOwnFields(Player player){
    return getOwnFields(player.getPlayerColor());
  }

  private Set<Field> getSwarm(Set<Field> found, Set<Field> swarm){
    if (swarm.isEmpty() && !found.isEmpty()) {
      Field field = found.iterator().next();
      swarm.add(field);
      found.remove(field);
    }

    Set<Field> tmpSwarm = new HashSet<>(swarm);
    // O(swarm.size()) time
    for(Field field : swarm){
      // Constant time for both calls (max of 8 neighbors)
      Set<Field> neighbours = getDirectNeighbour(field, found);
      tmpSwarm.addAll(neighbours);
    }

    // O(found.size()*swarm.size()) time
    // FIXME: Might be improved O(swarm.size()) should be possible
    if (swarm.size() != tmpSwarm.size())
      tmpSwarm = getSwarm(found, tmpSwarm);


    swarm.addAll(tmpSwarm);

    found.removeAll(swarm);
    return swarm;
  }

  public Set<Field> greatestSwarm(Set<Field> fieldsToCheck){
    // Make a copy, so there will be no conflict with direct calls.
    Set<Field> occupiedFields = new HashSet<>(fieldsToCheck);
    Set<Field> greatestSwarm = new HashSet<>();
    int maxSize = -1;

    // this is a maximum of MAX_FISH iterations, so it is a linear iteration altogether
    while(!occupiedFields.isEmpty() && occupiedFields.size() > maxSize) {
      Set<Field> empty = new HashSet<>();
      Set<Field> swarm = getSwarm(occupiedFields, empty);
      if (maxSize < swarm.size()){
        maxSize = swarm.size();
        greatestSwarm = swarm;
      }
    }
    return greatestSwarm;
  }

  public Set<Field> greatestSwarm(PlayerColor player){
    Set<Field> occupiedFields = getOwnFields(player);

    return greatestSwarm(occupiedFields);
  }

  public int greatestSwarmSize(PlayerColor player){
    return greatestSwarm(player).size();
  }
  public int greatestSwarmSize(Set<Field> set){
    return greatestSwarm(set).size();
  }

  public boolean isSwarmConnected(Player player) {
    Set<Field> fieldsWithFish = getOwnFields(player);
    int numGreatestSwarm = greatestSwarmSize(fieldsWithFish);
    return numGreatestSwarm == fieldsWithFish.size();
  }

  private int moveDistanceHorizontal(int ignore, int y){
    int count = 0;
    for(int i = 0; i < BOARD_SIZE; i++){
      if (board.getField(i,y).getPiranha().isPresent()){
        count++;
      }
    }
    return count;
  }

  private int moveDistanceVertical(int x, int ignore){
    int count = 0;
    for(int i = 0; i < BOARD_SIZE; i++){
      if (board.getField(x,i).getPiranha().isPresent()){
        count++;
      }
    }
    return count;
  }

  private int moveDistanceDiagonalRising(int x, int y){
    int count = 0;
    int cX = x;
    int cY = y;
    // Move down left
    while(cX >= 0 && cY >= 0) {
      if (board.getField(cX,cY).getPiranha().isPresent()){
        count++;
      }
      cY--;
      cX--;
    }


    // Move up right
    cX = x+1;
    cY = y+1;
    while(cX < BOARD_SIZE && cY < BOARD_SIZE) {
      if (board.getField(cX,cY).getPiranha().isPresent()){
        count++;
      }
      cY++;
      cX++;
    }
    return count;
  }

  private int moveDistanceDiagonalFalling(int x, int y){
    int count = 0;
    int cX = x;
    int cY = y;
    // Move down left
    while(cX < BOARD_SIZE && cY >= 0) {
      if (board.getField(cX,cY).getPiranha().isPresent()){
        count++;
      }
      cY--;
      cX++;
    }


    // Move up right
    cX = x-1;
    cY = y+1;
    while(cX >= 0  && cY < BOARD_SIZE) {
      if (board.getField(cX,cY).getPiranha().isPresent()){
        count++;
      }
      cY++;
      cX--;
    }
    return count;
  }

  /**
   * Calculate the minimum steps to take from given position in given direction
   * @param x coordinate to calculate from
   * @param y coordinate to calculate from
   * @param direction of the calcualtion
   * @return -1 if Invalid move, else the steps to take
   */
  public int calculateMoveDistance(int x, int y, Direction direction) {
    switch(direction){
      case LEFT:
      case RIGHT:
        return moveDistanceHorizontal(x,y);
      case UP:
      case DOWN:
        return moveDistanceVertical(x,y);
      case UP_RIGHT:
      case DOWN_LEFT:
        return moveDistanceDiagonalRising(x,y);
      case DOWN_RIGHT:
      case UP_LEFT:
        return moveDistanceDiagonalFalling(x,y);
    }
    return -1;
  }

  public Field getField(int x, int y) {
    return this.getBoard().getField(x,y);
  }

  /**
   * Überprüft nicht, ob Feld innerhalb der Feldgrenzen
   * @param x
   * @param y
   * @param direction
   * @param distance
   * @return
   */
  public Field getFieldInDirection(int x, int y, Direction direction, int distance) {
    Map.Entry<Integer, Integer> shift = directionShift(direction);
    return this.getBoard().getField(x + shift.getKey() * distance, y + shift.getValue() * distance);
  }

  private Map.Entry<Integer, Integer> directionShift(Direction d){
    int shiftX = 0;
    int shiftY = 0;
    switch (d){
      case UP_RIGHT:
        shiftX = 1;
      case UP:
        shiftY = 1;
        break;
      case DOWN_RIGHT:
        shiftY = -1;
      case RIGHT:
        shiftX = 1;
        break;
      case DOWN_LEFT:
        shiftX = -1;
      case DOWN:
        shiftY = -1;
        break;
      case UP_LEFT:
        shiftY = 1;
      case LEFT:
        shiftX = -1;
        break;
    }
    return new AbstractMap.SimpleEntry<>(shiftX, shiftY);
  }

  public List<Field> getFieldsInDirection(int x, int y, Direction d) {
    int distance = calculateMoveDistance(x, y, d);
    List<Field> fields = new LinkedList<>();
    Board b = getBoard();
    Map.Entry<Integer, Integer> shift = directionShift(d);

    for (int i = 0; i < distance; i++){
      fields.add(b.getField(x+shift.getKey()*i, y+shift.getValue()*i));
    }
    return fields;
  }
}
