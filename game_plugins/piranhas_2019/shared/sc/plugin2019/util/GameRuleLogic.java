package sc.plugin2019.util;

import sc.plugin2019.Direction;
import sc.plugin2019.Field;
import sc.plugin2019.FieldState;
import sc.plugin2019.GameState;
import sc.shared.InvalidMoveException;
import sc.shared.PlayerColor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static sc.plugin2019.Direction.*;

public class GameRuleLogic
{

  private GameRuleLogic()
  {
    throw new IllegalStateException("Can't be instantiated.");
  }

  /**
   * Berechnet, ob der Schwarm des currentPlayers zusammenhängend ist
   * @param state GameState
   * @return true, falls zusammenhängend
   */
  public static boolean isSwarmConnected(GameState state) {
    return state.isSwarmConnected(state.getCurrentPlayer());
  }



  public static boolean isValidToMove(int x, int y, Direction direction, int distance, GameState state) throws InvalidMoveException {
    if (x >= Constants.BOARD_SIZE || y >= Constants.BOARD_SIZE || x < 0 || y < 0) throw new InvalidMoveException("x or y are not within the field range");
    Field curField = state.getField(x,y);
    if (curField.getPiranha() != state.getCurrentPlayerColor()){
      throw new InvalidMoveException("Field does not belong to the current player");
    }


    if (state.calculateMoveDistance(x,y,direction) != distance){
      throw new InvalidMoveException("Move distance was incorrect");
    }
    Field nextField;
    try{
      nextField = state.getFieldInDirection(x,y,direction, distance);
    } catch(ArrayIndexOutOfBoundsException e){
      throw new InvalidMoveException("Move in that direction would not be on the board");
    }

    List<Field> fieldsInDirection = state.getFieldsInDirection(x,y,direction);

    FieldState oponentFieldColor;
    if (state.getCurrentPlayerColor() == PlayerColor.RED) {
      oponentFieldColor = FieldState.PLAYER_BLUE;
    } else {
      oponentFieldColor = FieldState.PLAYER_RED;
    }

    for (Field f : fieldsInDirection){
      if (f.getState() == oponentFieldColor || f.getState() == FieldState.OBSTRUCTED ){
        throw new InvalidMoveException("Path to the new position is not clear");
      }
    }

    if (nextField.getPiranha() == state.getCurrentPlayerColor()){
      throw new InvalidMoveException("Field obstructed with own piranha");
    }
    if (nextField.isObstructed()){
      throw new InvalidMoveException("Field is obstructed");
    }
    return true;
  }
}
