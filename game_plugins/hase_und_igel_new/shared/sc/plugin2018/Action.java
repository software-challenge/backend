package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.shared.InvalidMoveException;

@XStreamAlias(value = "action")
public abstract class Action implements Comparable<Action> {

  public Action() {
    this.order = 0;
  }

  /**
   * Nummer der Aktion. Aktionen werden aufsteigend sortiert nach ihrer Nummer
   * ausgeführt.

   */
  @XStreamAsAttribute
  public int order;

  public abstract void perform(GameState state, Player player) throws InvalidMoveException;

  @Override
  public int compareTo(Action o) {
    return Integer.compare(this.order, o.order);
  }
}
