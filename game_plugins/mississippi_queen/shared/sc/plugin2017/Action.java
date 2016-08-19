package sc.plugin2017;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import sc.plugin2017.util.InvalidMoveException;

@XStreamAlias(value = "action")
public abstract class Action {
  
  /**
   * Zeigt an welche Nummer die Aktion hat
   */
  @XStreamAsAttribute
  public int order;
  
  public abstract void perform(GameState state, Player player) throws InvalidMoveException;
}
