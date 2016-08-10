package sc.plugin2017;

import sc.plugin2017.util.InvalidMoveException;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public abstract class Action {

  
  /**
   * Zeigt an welche Nummer die Aktion hat
   */
  @XStreamAsAttribute
  public int order;
  
  public abstract int perform(GameState state, Player player) throws InvalidMoveException;
}
