package sc.plugin2016;

import java.util.LinkedList;
import java.util.List;

import sc.plugin2016.util.Constants;
import sc.plugin2016.util.InvalidMoveException;
import sc.plugin2016.DebugHint;
import sc.plugin2016.Move;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias(value = "move")
public class Move implements Cloneable {

  @XStreamAsAttribute
  private final int x;

  @XStreamAsAttribute
  private final int y;

  @XStreamImplicit(itemFieldName = "hint")
  private List<DebugHint> hints;

  public Move() {
    this.x = -1;
    this.y = -1;
  }

  public Move(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /**
   * klont dieses Objekt
   * 
   * @return ein neues Objekt mit gleichen Eigenschaften
   * @throws CloneNotSupportedException
   */
  @Override
  public Object clone() throws CloneNotSupportedException {
    Move clone = (Move) super.clone();
    if (this.hints != null)
      clone.hints = new LinkedList<DebugHint>(this.hints);
    return clone;
  }

  /**
   * Fuegt eine Debug-Hilfestellung hinzu.<br/>
   * Diese kann waehrend des Spieles vom Programmierer gelesen werden, wenn der
   * Client einen Zug macht.
   * 
   * @param hint
   *          hinzuzufuegende Debug-Hilfestellung
   */
  public void addHint(DebugHint hint) {
    if (hints == null) {
      hints = new LinkedList<DebugHint>();
    }
    hints.add(hint);
  }

  /**
   * 
   * Fuegt eine Debug-Hilfestellung hinzu.<br/>
   * Diese kann waehrend des Spieles vom Programmierer gelesen werden, wenn der
   * Client einen Zug macht.
   * 
   * @param key
   *          Schluessel
   * @param value
   *          zugehöriger Wert
   */
  public void addHint(String key, String value) {
    addHint(new DebugHint(key, value));
  }

  /**
   * Fuegt eine Debug-Hilfestellung hinzu.<br/>
   * Diese kann waehrend des Spieles vom Programmierer gelesen werden, wenn der
   * Client einen Zug macht.
   * 
   * @param string
   *          Debug-Hilfestellung
   */
  public void addHint(String string) {
    addHint(new DebugHint(string));
  }

  /**
   * Gibt die Liste der hinzugefuegten Debug-Hilfestellungen zurueck
   * 
   * @return Liste der hinzugefuegten Debug-Hilfestellungen
   */
  public List<DebugHint> getHints() {
    return hints == null ? new LinkedList<DebugHint>() : hints;
  }

  /**
   * @return x
   */
  public int getX() {
    return x;
  }

  /**
   * @return y
   */
  public int getY() {
    return y;
  }

  /**
   * Fuehrt diesen Zug auf den uebergebenen Spielstatus aus, mit uebergebenem
   * Spieler.
   * 
   * @param state
   *          Spielstatus
   * @param player
   *          ausfuehrender Spieler
   * @throws InvalidMoveException
   *           geworfen, wenn der Zug ungueltig ist, also nicht ausfuehrbar
   */
  public void perform(GameState state, Player player) throws InvalidMoveException {
    if (this != null) {
      if (getX() < Constants.SIZE && getY() < Constants.SIZE && getX() >= 0
          && getX() >= 0) {
        if (state.getPossibleMoves().contains(this)) {
          state.getBoard().put(getX(), getY(), player);
          player.setPoints(state.getPointsForPlayer(player.getPlayerColor()));
          //System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nset points for player " + player.getPlayerColor() + " to " + state.getPointsForPlayer(player.getPlayerColor()));
        } else {
          throw new InvalidMoveException(
              "Der Zug ist nicht möglich, denn der Platz ist bereits besetzt oder nicht besetzbar.");
        }
      } else {
        throw new InvalidMoveException(
            "Startkoordinaten sind nicht innerhalb des Spielfeldes.");
      }
    }

  }
  @Override
  public boolean equals(Object o) {
    if(o instanceof Move) {
      Move move = (Move) o;
      if(this.getX() == move.getX() && this.getY() == move.getY()) {
        return true;
      }
    }
    return false;
  }

}
