package sc.plugin2019.util;

import sc.plugin2019.Direction;
import sc.plugin2019.Field;
import sc.plugin2019.GameState;
import sc.shared.InvalidMoveException;

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

      if (direction == INVALID) throw new InvalidMoveException("Invalid move direction");

      int newX = x;
      int newY = y;
      if (direction == UP_RIGHT || direction == RIGHT || direction == DOWN_RIGHT)
          newX+=distance;
      else if (direction == UP_LEFT || direction == DOWN_LEFT || direction == LEFT)
          newX-=distance;

      if (direction == UP_LEFT || direction == UP || direction == UP_RIGHT)
          newY += distance;
      else if (direction == DOWN_LEFT || direction == DOWN || direction == DOWN_RIGHT)
          newY -= distance;


     if (newX >= Constants.BOARD_SIZE || newY >= Constants.BOARD_SIZE || newX < 0 || newY < 0) return false;

     Field nextField = state.getField(newX, newY);
     if(nextField.getPiranha() == state.getCurrentPlayerColor()
           || nextField.isObstructed()) return false;





    // TODO throw Exception, if no piranha of current player, if move out of board or on own piranha
    return true;
  }
}
