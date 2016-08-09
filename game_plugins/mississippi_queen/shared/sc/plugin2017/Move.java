package sc.plugin2017;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import sc.plugin2017.util.InvalidMoveException;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias(value = "move")
public class Move implements Cloneable {
  // TODO Xstream wahrscheinlich mit eigener Converterklasse

  /**
   * Liste von Aktionen aus denen der Zug besteht
   */
  public ArrayList<Action> actions;
  
  /**
   * Liste von Debughints, die dem Zug beigefügt werden koennen. Siehe {@link DebugHint}
   */
  @XStreamImplicit(itemFieldName = "hint")
  private List<DebugHint> hints;

  /**
   * Default Konstruktor, der einen ungueltigen Zug auf Position (-1, -1) erzeugt.
   */
  public Move() {
    actions = new ArrayList<Action>();
  }

  /**
   * Erzeugt einen neuen Zug auf gegebene Koordinaten
   * @param x X-Koordinate
   * @param y Y-Koordinate
   */
  public Move(ArrayList<Action> actions) {
    this.actions = new ArrayList<Action>(actions);
  }

  /**
   * erzeugt eine Deepcopy dieses Objektes TODO
   * 
   * @return ein neues Objekt mit gleichen Eigenschaften
   * @throws CloneNotSupportedException falls nicht geklont werden kann
   */
  @Override
  public Object clone() throws CloneNotSupportedException {
    Move clone = new Move(this.actions);
    if (this.hints != null)
      clone.hints = new LinkedList<DebugHint>(this.hints);
    return clone;
  }

  /**
   * Fuegt eine Debug-Hilfestellung hinzu.
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
   * Fuegt eine Debug-Hilfestellung hinzu.
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
   * Fuegt eine Debug-Hilfestellung hinzu.
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
    int freeTurns = state.isFreeTurn() ? 2 : 1;
    int totalMovement = 0;
    for(Action action : actions) {
      if(action.getClass() == Turn.class) {
        if(player.getField(state.getBoard()).getType() == FieldType.SANDBAR) {
          throw new InvalidMoveException("Du kannst nicht auf einer Sandbank drehen");
        }
        freeTurns -= action.perform(state, player); // count turns
      } else if(action.getClass() == Acceleration.class) {
        Acceleration acc = (Acceleration) action;
        if(acc.order != 0) {
          throw new InvalidMoveException("Du kannst nur in der ersten Aktion beschleunigen.");
        }
        action.perform(state, player); // coal is decreased in perform
      } else {
        totalMovement += action.perform(state, player); // count distance
      }
    }
    if(freeTurns < 0) { // check coal
      player.setCoal(player.getCoal() + freeTurns);
      if(player.getCoal() > 0) {
        throw new InvalidMoveException("Nicht genug Kohle für den Zug vorhanden.");
      }
    }
    if(totalMovement != player.getSpeed()) { // check speed
      throw new InvalidMoveException("Es sind noch Bewegungspunkte übrig oder es wurden zu viele verbraucht.");
    }
  }
  
  /**
   * Vergleichsmethode fuer einen Zuege
   * Zwei Züge sind gleich, wenn sie die gleichen Teilaktionen beinhalten
   */
  @Override
  public boolean equals(Object o) {
    if(o instanceof Move) {
      Move move = (Move) o;
      for(Action action : move.actions) {
        if(!this.actions.contains(action)) {
          return false;
        }
      }
      for(Action action : this.actions) {
        if(!move.actions.contains(action)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  public boolean containsPushAction() {
    for (Action action : actions) {
      if(action.getClass() == Push.class) {
        return true;
      }
    }
    return false;
  }

}
