package sc.plugin2017;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import sc.plugin2017.util.InvalidMoveException;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias(value = "move")
public class Move implements Cloneable {

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
   * Erzeugt einen neuen Zug aus Liste von Aktionen
   * @param selectedActions Aktionen des Zuges
   */
  public Move(List<Action> selectedActions) {
    this.actions = new ArrayList<Action>(selectedActions);
  }

  /**
   * erzeugt eine Deepcopy dieses Objektes
   * 
   * @return ein neues Objekt mit gleichen Eigenschaften
   * @throws CloneNotSupportedException falls nicht geklont werden kann
   */
  @Override
  public Object clone() throws CloneNotSupportedException {
    ArrayList<Action> clonedActions = new ArrayList<Action>();
    for (Action action : actions) {
      if(action.getClass() == Acceleration.class) {
        Acceleration clonedAction = ((Acceleration) action).clone();
        clonedActions.add(clonedAction);
      }
      if(action.getClass() == Push.class) {
        Push clonedAction = ((Push) action).clone();
        clonedActions.add(clonedAction);
      }
      if(action.getClass() == Step.class) {
        Step clonedAction = ((Step) action).clone();
        clonedActions.add(clonedAction);
      }
      if(action.getClass() == Turn.class) {
        Turn clonedAction = ((Turn) action).clone();
        clonedActions.add(clonedAction);
      }
    }
    Move clone = new Move(clonedActions); 
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
   *           oder wenn der Zug unsinnig ist (Drehung um mehr als die Hälfte, Drehung oder Laufen um 0)
   *           oder wenn die Aktionen im Zug nicht nach der Reihenfolge sortiert sind
   */
  public void perform(GameState state, Player player) throws InvalidMoveException {
    int freeTurns = state.isFreeTurn() ? 2 : 1;
    int beginningSpeed = player.getSpeed();
    int totalMovement = 0;
    int order = 0;
    int reduceSpeed = 0;
    boolean onEnemy;
    if(actions.isEmpty()) {
      throw new InvalidMoveException("Der Zug enthält keine Aktionen");
    }
    for(Action action : actions) {
      onEnemy = player.getX() == state.getOtherPlayer().getX() && 
          player.getY() == state.getOtherPlayer().getY();
      if(onEnemy && action.getClass() != Push.class) {
        throw new InvalidMoveException("Wenn du auf einem gegnerischen Schiff landest,"
            + " muss darauf eine Abdrängaktion folgen.");
      }
      Action lastAction = actions.get(action.order - 1);
      if(lastAction != null && lastAction.getClass() == Step.class) {
        if(((Step) lastAction).endsTurn) {
          throw new InvalidMoveException("Zug auf eine Sandbank muss letzte Aktion sein.");
        }
      }
      if(order != action.order) {
        throw new InvalidMoveException("Aktionen sind nicht nach Reihenfolge sortiert.");
      }
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
        acc.perform(state, player); // coal is decreased in perform
      } else {
        totalMovement += action.perform(state, player); // count distance
        if(action.getClass() == Step.class) {
          reduceSpeed += ((Step) action).reduceSpeed; // add speed to reduce on end of turn
        }
      }
      ++order;
    }
    if(beginningSpeed == 1 && player.canPickupPassenger(state.getBoard())) {
      state.removePassenger(player);
    }
    if(freeTurns < 0) { // check coal
      player.setCoal(player.getCoal() + freeTurns);
    }
    if(player.getCoal() < 0) {
      throw new InvalidMoveException("Nicht genug Kohle für den Zug vorhanden.");
    }
    if(totalMovement > player.getSpeed() || 
        (totalMovement < player.getSpeed() && player.getField(state.getBoard()).getType() != FieldType.SANDBAR)) { // check speed
      throw new InvalidMoveException("Es sind noch Bewegungspunkte übrig oder es wurden zu viele verbraucht.");
    }
    if(player.getSpeed() - reduceSpeed > 0) {
      player.setSpeed(player.getSpeed() - reduceSpeed);
    } else {
      player.setSpeed(1);
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
  
  public String toString() {
    String toString = "Zug mit folgenden Aktionen \n";
    for (Action action : actions) {
      toString.concat(action.toString() + " ");
    }
    return toString;
  }

}
