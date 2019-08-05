package sc.plugin2020.util;

import sc.plugin2020.Board;
import sc.plugin2020.Field;
import sc.plugin2020.GameState;
import sc.plugin2020.Move;
import sc.shared.PlayerColor;

public class GameRuleLogic {

  private GameRuleLogic() {
    throw new IllegalStateException("Can't be instantiated.");
  }

  public static Field getNeighbourInDirection(Board b, Coord c, Direction d){
    return b.getField(new Coord(c.x + d.shift(1).x,c.y + d.shift(1).y,c.z + d.shift(1).z));
  }

  public static PlayerColor getCurrentPlayerColor(GameState gs){
    return ((gs.getTurn() % 2 == 0) ? PlayerColor.RED : PlayerColor.BLUE);
  }

  public static GameState performMove(GameState gs, Move m){
    return null;
  }

  public static boolean isQueenBlocked(Board b, PlayerColor pc){
    return false;
  }
}
