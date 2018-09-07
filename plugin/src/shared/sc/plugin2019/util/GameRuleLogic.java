package sc.plugin2019.util;

import sc.plugin2019.*;
import sc.shared.InvalidMoveException;
import sc.shared.PlayerColor;

import java.awt.*;
import java.util.*;
import java.util.List;

import static sc.plugin2019.util.Constants.BOARD_SIZE;
import static sc.plugin2019.util.Constants.MAX_FISH;

public class GameRuleLogic {

  private GameRuleLogic() {
    throw new IllegalStateException("Can't be instantiated.");
  }

  /**
   * Gibt eine Liste aller möglichen Züge zurück
   *
   * @return Liste von Move Objekten
   */
  public static ArrayList<Move> getPossibleMoves(GameState state) {
    ArrayList<Move> possibleMoves = new ArrayList<Move>();
    Collection<Field> fields = getOwnFields(state.getBoard(), state.getCurrentPlayerColor());
    for (Field field : fields) {
      for (Direction direction : Direction.values()) {
        int x = field.getX();
        int y = field.getY();
        int dist = calculateMoveDistance(state.getBoard(), x, y, direction);
        try {
          if (dist > 0 && isValidToMove(state, x, y, direction, dist)) {
            Move m = new Move(x, y, direction);
            possibleMoves.add(m);
          }
        } catch (InvalidMoveException ignore) {
        }

      }
    }
    return possibleMoves;
  }

  public static boolean isValidToMove(GameState state, int x, int y, Direction direction, int distance) throws InvalidMoveException {
    if (x >= Constants.BOARD_SIZE || y >= Constants.BOARD_SIZE || x < 0 || y < 0)
      throw new InvalidMoveException("x or y are not within the field range");
    Board board = state.getBoard();
    Field curField = board.getField(x, y);
    Optional<PlayerColor> curFieldPlayer = curField.getPiranha();
    if (!curFieldPlayer.isPresent() || curFieldPlayer.get() != state.getCurrentPlayerColor()) {
      throw new InvalidMoveException("Field does not belong to the current player");
    }


    if (calculateMoveDistance(board, x, y, direction) != distance)
      throw new InvalidMoveException("Move distance was incorrect");

    Field nextField;
    try {
      nextField = getFieldInDirection(board, x, y, direction, distance);
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new InvalidMoveException("Move in that direction would not be on the board");
    }

    List<Field> fieldsInDirection = getFieldsInDirection(board, x, y, direction);

    FieldState opponentFieldColor;
    if (state.getCurrentPlayerColor() == PlayerColor.RED) {
      opponentFieldColor = FieldState.BLUE;
    } else {
      opponentFieldColor = FieldState.RED;
    }

    for (Field f : fieldsInDirection) {
      if (f.getState() == opponentFieldColor) {
        throw new InvalidMoveException("Path to the new position is not clear");
      }
    }

    Optional<PlayerColor> nextFieldPlayer = nextField.getPiranha();
    if (nextFieldPlayer.isPresent() && nextFieldPlayer.get() == state.getCurrentPlayerColor()) {
      throw new InvalidMoveException("Field obstructed with own piranha");
    }
    if (nextField.isObstructed()) {
      throw new InvalidMoveException("Field is obstructed");
    }
    return true;
  }

  public static Set<Field> getOwnFields(Board board, PlayerColor player) {
    Set<Field> fields = new HashSet<>();
    int size = 0;
    for (int i = 0; i < BOARD_SIZE && MAX_FISH > size; i++) {
      for (int j = 0; j < BOARD_SIZE && MAX_FISH > size; j++) {
        Field curField = board.getField(i, j);
        if (curField.getPiranha().isPresent() && curField.getPiranha().get().equals(player)) {
          fields.add(curField);
          size++;
        }
      }
    }
    return fields;
  }

  private static Set<Field> getDirectNeighbour(Board board, Field f, Set<Field> parentSet) {
    Set<Field> returnSet = new HashSet<>();
    for (int i = -1; i <= 1; i++) {
      for (int j = -1; j <= 1; j++) {
        int x = f.getX() + i;
        int y = f.getY() + j;
        if (x < 0 || x >= Constants.BOARD_SIZE || y < 0 || y >= Constants.BOARD_SIZE || (i == 0 && j == 0)) continue;

        Field field = board.getField(x, y);
        if (parentSet.contains(field)) {
          returnSet.add(field);
        }
      }
    }
    return returnSet;
  }

  private static Set<Field> getSwarm(Board board, Set<Field> found, Set<Field> swarm) {
    if (swarm.isEmpty() && !found.isEmpty()) {
      Field field = found.iterator().next();
      swarm.add(field);
      found.remove(field);
    }

    Set<Field> tmpSwarm = new HashSet<>(swarm);
    // O(swarm.size()) time
    for (Field field : swarm) {
      // Constant time for both calls (max of 8 neighbors)
      Set<Field> neighbours = getDirectNeighbour(board, field, found);
      tmpSwarm.addAll(neighbours);
    }

    // O(found.size()*swarm.size()) time
    // FIXME: Might be improved O(swarm.size()) should be possible
    if (swarm.size() != tmpSwarm.size())
      tmpSwarm = getSwarm(board, found, tmpSwarm);


    swarm.addAll(tmpSwarm);

    found.removeAll(swarm);
    return swarm;
  }

  public static Set<Field> greatestSwarm(Board board, Set<Field> fieldsToCheck) {
    // Make a copy, so there will be no conflict with direct calls.
    Set<Field> occupiedFields = new HashSet<>(fieldsToCheck);
    Set<Field> greatestSwarm = new HashSet<>();
    int maxSize = -1;

    // this is a maximum of MAX_FISH iterations, so it is a linear iteration altogether
    while (!occupiedFields.isEmpty() && occupiedFields.size() > maxSize) {
      Set<Field> empty = new HashSet<>();
      Set<Field> swarm = getSwarm(board, occupiedFields, empty);
      if (maxSize < swarm.size()) {
        maxSize = swarm.size();
        greatestSwarm = swarm;
      }
    }
    return greatestSwarm;
  }

  public static Set<Field> greatestSwarm(Board board, PlayerColor player) {
    Set<Field> occupiedFields = getOwnFields(board, player);
    return greatestSwarm(board, occupiedFields);
  }

  public static int greatestSwarmSize(Board board, PlayerColor player) {
    return greatestSwarm(board, player).size();
  }

  public static int greatestSwarmSize(Board board, Set<Field> set) {
    return greatestSwarm(board, set).size();
  }

  public static boolean isSwarmConnected(Board board, PlayerColor player) {
    Set<Field> fieldsWithFish = getOwnFields(board, player);
    int numGreatestSwarm = greatestSwarmSize(board, fieldsWithFish);
    return numGreatestSwarm == fieldsWithFish.size();
  }

  /** Überprüft nicht, ob Feld innerhalb der Feldgrenzen */
  public static Field getFieldInDirection(Board board, int x, int y, Direction direction, int distance) {
    Point shift = direction.shift();
    return board.getField(x + shift.x * distance, y + shift.y * distance);
  }

  public static List<Field> getFieldsInDirection(Board board, int x, int y, Direction d) {
    int distance = calculateMoveDistance(board, x, y, d);
    List<Field> fields = new ArrayList<>();
    Point shift = d.shift();

    for (int i = 0; i < distance; i++) {
      fields.add(board.getField(x + shift.x * i, y + shift.y * i));
    }
    return fields;
  }

  /**
   * Calculate the minimum steps to take from given position in given direction
   *
   * @param x         coordinate to calculate from
   * @param y         coordinate to calculate from
   * @param direction of the calcualtion
   *
   * @return -1 if Invalid move, else the steps to take
   */
  public static int calculateMoveDistance(Board board, int x, int y, Direction direction) {
    switch (direction) {
      case LEFT:
      case RIGHT:
        return moveDistanceHorizontal(board, x, y);
      case UP:
      case DOWN:
        return moveDistanceVertical(board, x, y);
      case UP_RIGHT:
      case DOWN_LEFT:
        return moveDistanceDiagonalRising(board, x, y);
      case DOWN_RIGHT:
      case UP_LEFT:
        return moveDistanceDiagonalFalling(board, x, y);
    }
    return -1;
  }

  private static int moveDistanceHorizontal(Board board, int ignore, int y) {
    int count = 0;
    for (int i = 0; i < BOARD_SIZE; i++) {
      if (board.getField(i, y).getPiranha().isPresent()) {
        count++;
      }
    }
    return count;
  }

  private static int moveDistanceVertical(Board board, int x, int ignore) {
    int count = 0;
    for (int i = 0; i < BOARD_SIZE; i++) {
      if (board.getField(x, i).getPiranha().isPresent()) {
        count++;
      }
    }
    return count;
  }

  private static int moveDistanceDiagonalRising(Board board, int x, int y) {
    int count = 0;
    int cX = x;
    int cY = y;
    // Move down left
    while (cX >= 0 && cY >= 0) {
      if (board.getField(cX, cY).getPiranha().isPresent()) {
        count++;
      }
      cY--;
      cX--;
    }

    // Move up right
    cX = x + 1;
    cY = y + 1;
    while (cX < BOARD_SIZE && cY < BOARD_SIZE) {
      if (board.getField(cX, cY).getPiranha().isPresent()) {
        count++;
      }
      cY++;
      cX++;
    }
    return count;
  }

  private static int moveDistanceDiagonalFalling(Board board, int x, int y) {
    int count = 0;
    int cX = x;
    int cY = y;
    // Move down left
    while (cX < BOARD_SIZE && cY >= 0) {
      if (board.getField(cX, cY).getPiranha().isPresent()) {
        count++;
      }
      cY--;
      cX++;
    }

    // Move up right
    cX = x - 1;
    cY = y + 1;
    while (cX >= 0 && cY < BOARD_SIZE) {
      if (board.getField(cX, cY).getPiranha().isPresent()) {
        count++;
      }
      cY++;
      cX--;
    }
    return count;
  }

}
