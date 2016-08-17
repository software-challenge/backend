package sc.plugin2017;

import sc.plugin2017.util.InvalidMoveException;

public abstract class Action {
  
  /**
   * Zeigt an welche Nummer die Aktion hat
   */
  public int order;
  
  public abstract void perform(GameState state, Player player) throws InvalidMoveException;
}
