package sc.plugin2017;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import sc.plugin2017.util.InvalidMoveException;

@XStreamAlias("move")
public class Move implements Cloneable {

  private static final Logger logger = LoggerFactory.getLogger(Move.class);
  /**
   * Liste von Aktionen aus denen der Zug besteht
   */
  @XStreamImplicit
  public List<Action> actions;

  public List<Action> getActions() {
    if (actions == null) {
      return Collections.emptyList();
    } else {
      return actions;
    }
  }

  /**
   * Liste von Debughints, die dem Zug beigefügt werden koennen. Siehe {@link DebugHint}
   */
  @XStreamImplicit(itemFieldName = "hint")
  private List<DebugHint> hints;

  /**
   * Default Konstruktor, der einen leeren Zug erzeugt.
   */
  public Move() {
    // This list needs to be thread safe because the side bar may be iterating
    // over it while a new turn is started, resulting in a
    // ConcurrentModificationException.
    actions = new CopyOnWriteArrayList<>();
  }

  /**
   * Erzeugt einen neuen Zug aus Liste von Aktionen
   * @param selectedActions Aktionen des Zuges
   */
  public Move(List<Action> selectedActions) {
    assert selectedActions != null;
    actions = new CopyOnWriteArrayList<>(selectedActions);
  }

  /**
   * erzeugt eine Deepcopy dieses Objektes
   *
   * @return ein neues Objekt mit gleichen Eigenschaften
   * @throws CloneNotSupportedException falls nicht geklont werden kann
   */
  @Override
  public Object clone() throws CloneNotSupportedException {
    List<Action> clonedActions = new CopyOnWriteArrayList<>();
    for (Action action : getActions()) {
      if(action.getClass() == Acceleration.class) {
        Acceleration clonedAction = ((Acceleration) action).clone();
        clonedActions.add(clonedAction);
      }
      if(action.getClass() == Push.class) {
        Push clonedAction = ((Push) action).clone();
        clonedActions.add(clonedAction);
      }
      if(action.getClass() == Advance.class) {
        Advance clonedAction = ((Advance) action).clone();
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
    return hints == null ? Collections.<DebugHint>emptyList() : hints;
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
    orderActions();
    int order = 0;
    boolean onEnemy;
    // make sure movement is set right:
    player.setMovement(player.getSpeed());


    if(getActions().isEmpty()) {
      throw new InvalidMoveException("Der Zug enthält keine Aktionen");
    }
    for(Action action : actions) {
      onEnemy = player.getX() == state.getOtherPlayer().getX() &&
          player.getY() == state.getOtherPlayer().getY();
      if(onEnemy && action.getClass() != Push.class) {
        throw new InvalidMoveException("Wenn du auf einem gegnerischen Schiff landest,"
            + " muss darauf eine Abdrängaktion folgen.");
      }
      Action lastAction = null;
      if(order > 0) {
        lastAction = actions.get(action.order - 1);

      }
      if(lastAction != null && lastAction.getClass() == Advance.class) {
        if(((Advance) lastAction).endsTurn) {
          throw new InvalidMoveException("Zug auf eine Sandbank muss letzte Aktion sein.");
        }
      }
      if(action.getClass() == Turn.class) {
        if(player.getField(state.getBoard()).getType() == FieldType.SANDBANK) {
          throw new InvalidMoveException("Du kannst nicht auf einer Sandbank drehen");
        }
        ((Turn)action).perform(state, player); // count turns decreases freeTurns and reduces coal if nessessary
      } else if(action.getClass() == Acceleration.class) {
        Acceleration acc = (Acceleration) action;
        if(acc.order != 0) {
          throw new InvalidMoveException("Du kannst nur in der ersten Aktion beschleunigen.");
        }
        acc.perform(state, player); // coal is decreased in perform
      } else {
        action.perform(state, player); // Speed and movement is decreased here
      }
      ++order;
    }
    // when stepping onto the opponents field, the opponent has to be pushed away
    if (player.getX() == state.getOtherPlayer().getX() && player.getY() == state.getOtherPlayer().getY()) {
      throw new InvalidMoveException("Der Zug darf nicht auf dem Gegner enden.");
    }
    // pick up passenger
    if(player.getSpeed() == 1 && player.canPickupPassenger(state.getBoard())) {
      state.removePassenger(player);
    }
    // otherplayer could possible pick up Passenger in enemy turn
    if(state.getOtherPlayer().getSpeed() == 1 && state.getOtherPlayer().canPickupPassenger(state.getBoard())) {
      state.removePassenger(state.getOtherPlayer());
    }
    if(player.getCoal() < 0) {
      throw new InvalidMoveException("Nicht genug Kohle für den Zug vorhanden.");
    }
    if(player.getMovement() > 0) { // check whether movement points are left
      throw new InvalidMoveException("Es sind noch " + player.getMovement() + " Bewegungspunkte übrig.");
    }
    if(player.getMovement() < 0) { // check whether movement points are left
      throw new InvalidMoveException("Es sind " + Math.abs(player.getMovement()) + " Bewegungspunkte zuviel verbraucht worden.");
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
      for(Action action : move.getActions()) {
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
    for (Action action : getActions()) {
      if(action.getClass() == Push.class) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    String toString = "Zug mit folgenden Aktionen \n";
    for (Action action : getActions()) {
      if(action.getClass() == Turn.class) {
        toString.concat(((Turn) action).toString() + "\n");
      } else if(action.getClass() == Acceleration.class){
        toString.concat(((Acceleration) action).toString() + "\n");
      } else if(action.getClass() == Advance.class){
        toString.concat(((Advance) action).toString() + "\n");
      } else if(action.getClass() == Push.class){
        toString.concat(((Push) action).toString() + "\n");
      }
    }
    return toString;
  }

  /**
   * Setzt das order Attribut der Züge anhand ihrer Reihenfolge in actions
   */
  public void setOrderInActions() {
    int order = 0;
    for (Action action : getActions()) {
      action.order = order++;
    }
  }

  public void orderActions() {
    if (actions != null) {
      Collections.sort(actions);
    }
  }

}
