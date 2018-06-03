package sc.plugin2019.util;

import sc.plugin2019.Direction;
import sc.plugin2019.GameState;
import sc.shared.InvalidMoveException;

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


  public static boolean isValidToMove(int x, int y, Direction direction, int distance) throws InvalidMoveException {
    // TODO throw Exception, if no piranha of current player, if move out of board or on own piranha
    return false;
  }
}
