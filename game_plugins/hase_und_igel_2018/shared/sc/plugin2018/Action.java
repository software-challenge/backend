package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import sc.shared.InvalidMoveException;

/**
 * Eine Aktion, die Bestandteil eines Zuges ist. Kann auch ohne den Zug mittels perform ausgeführt werden.
 */
@XStreamAlias(value = "action")
public abstract class Action implements Comparable<Action>, Cloneable {

  public Action() {
    this.order = 0;
  }

  /**
   * Nummer der Aktion. Aktionen werden aufsteigend sortiert nach ihrer Nummer
   * ausgeführt.

   */
  @XStreamAsAttribute
  public int order;

  /**
   * Führt eine Aktion aus. Es wird zusätzlich lastAktion geupdated und mustPlayerCard gesetzt, sollte
   * dies nötig sein.
   * @param state GameState
   * @throws InvalidMoveException
   */
  public abstract void perform(GameState state) throws InvalidMoveException;

  @Override
  public int compareTo(Action o) {
    return Integer.compare(this.order, o.order);
  }

  public abstract Action clone();

}
