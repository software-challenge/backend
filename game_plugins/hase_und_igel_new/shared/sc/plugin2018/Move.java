package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.shared.DebugHint;
import sc.shared.InvalidMoveException;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Ein Spielzug. Ein Spielzug wird von dem derzeitgen Spieler eines GameStates ausgeführt. Er hat folgende Form:
 * - er besteht nur aus einer Skip, FallBack, EatSalad oder ExchangeCarrots Aktion
 * - er besteht aus einer Advance Aktion und eventuell darauf folgende Kartenaktionen
 */
@XStreamAlias(value = "move")
public class Move implements Cloneable {

  private static final Logger logger = LoggerFactory.getLogger(Move.class);
  /**
   * Liste von Aktionen aus denen der Zug besteht. Die Reihenfolge, in der die
   * Aktionen ausgeführt werden, wird NICHT durch die Reihenfolge in der Liste
   * bestimmt, sondern durch die Werte im order-Attribut jedes Action Objektes:
   * Die Aktionen werden nach dem order-Attribut aufsteigend sortiert
   * ausgeführt.
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
   * Liste von Debughints, die dem Zug beigefügt werden koennen. Siehe
   * {@link DebugHint}
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
   * Erzeugt einen neuen Zug aus Liste von Aktionen. Die Liste der Aktionen wird kopiert
   * (d.h. eine Änderung der Liste nach Erstellung des Zuges ändert den Zug nicht mehr).
   *
   * @param selectedActions
   *          Aktionen des Zuges
   */
  public Move(List<Action> selectedActions) {
    assert selectedActions != null;
    actions = new CopyOnWriteArrayList<>(selectedActions);
  }

  /**
   * erzeugt eine Deepcopy dieses Objektes
   *
   * @return ein neues Objekt mit gleichen Eigenschaften
   * @throws CloneNotSupportedException
   *           falls nicht geklont werden kann
   */
  @Override
  public Object clone() throws CloneNotSupportedException {
    List<Action> clonedActions = new CopyOnWriteArrayList<>();
    for (Action action : getActions()) {
      if (action instanceof Advance) {
        Advance clonedAction = ((Advance) action).clone();
        clonedActions.add(clonedAction);
      }
      if (action instanceof Skip) {
        Skip clonedAction = ((Skip) action).clone();
        clonedActions.add(clonedAction);
      }
      if (action instanceof EatSalad) {
        EatSalad clonedAction = ((EatSalad) action).clone();
        clonedActions.add(clonedAction);
      }
      if (action instanceof ExchangeCarrots) {
        ExchangeCarrots clonedAction = ((ExchangeCarrots) action).clone();
        clonedActions.add(clonedAction);
      }
      if (action instanceof FallBack) {
        FallBack clonedAction = ((FallBack) action).clone();
        clonedActions.add(clonedAction);
      }
      if (action instanceof Card) {
        Card clonedAction = ((Card) action).clone();
        clonedActions.add(clonedAction);
      }
    }
    Move clone = new Move(clonedActions);
    if (this.hints != null)
      clone.hints = new LinkedList<>(this.hints);
    return clone;
  }

  /**
   * Fuegt eine Debug-Hilfestellung hinzu. Diese kann waehrend des Spieles vom
   * Programmierer gelesen werden, wenn der Client einen Zug macht.
   *
   * @param hint
   *          hinzuzufuegende Debug-Hilfestellung
   */
  public void addHint(DebugHint hint) {
    if (hints == null) {
      hints = new LinkedList<>();
    }
    hints.add(hint);
  }

  /**
   *
   * Fuegt eine Debug-Hilfestellung hinzu. Diese kann waehrend des Spieles vom
   * Programmierer gelesen werden, wenn der Client einen Zug macht.
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
   * Fuegt eine Debug-Hilfestellung hinzu. Diese kann waehrend des Spieles vom
   * Programmierer gelesen werden, wenn der Client einen Zug macht.
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
   * Führt einen Zug aus, indem alle Aktionen aufsteigend anand des order Attributes ausgeführt werden.
   * Dabei werden zusätzlich folgende Informationen geupdated:
   * lastMove wird gesetzt, lastNonSkipAktion wird gesetzt, turn wird um eins erhöht.
   * der currentPlayer wird getauscht, falls sich der nächste Spieler auf entsprechendem
   * Positionsfeld befindet werden seine Karotten erhöht.
   * @param state
   *          Spielstatus
   * @throws InvalidMoveException
   *           wird geworfen, wenn der Zug nicht den Regeln entspricht
   */
  public void perform(GameState state) throws InvalidMoveException {
    // Sortiere Aktionen
    orderActions();
    if (actions.isEmpty()) {
      throw new InvalidMoveException("Keine Aktionen vorhanden.");
    }
    // führe Aktionen aus
    int index = 0;
    for (Action action : this.actions) {
      if (index != action.order) {
        throw new InvalidMoveException("Das order Attribut wurde nicht richtig gesetzt.");
      }
      if (action instanceof Advance || action instanceof Skip || action instanceof FallBack||
          action instanceof EatSalad || action instanceof ExchangeCarrots) {
        if (action.order != 0) {
          throw new InvalidMoveException("Nach der ersten Aktion können nur noch Karten folgen.");
        }

      }
      action.perform(state);
      index++;
    }
    if (state.getCurrentPlayer().mustPlayCard()) {
      throw new InvalidMoveException("Es muss eine Karte ausgespielt werden.");
    }
    // Bereite nächsten Zug vor:
    state.setLastMove(this);
    state.setTurn(state.getTurn() + 1);
    state.switchCurrentPlayer(); // depends on turn
    // Überprüfe ob nächster Spieler Karotten durch POSITION_X Felder bekommt
    FieldType fieldType = state.getBoard().getTypeAt(state.getCurrentPlayer().getFieldIndex());
    if (state.isFirst(state.getCurrentPlayer()) && fieldType == FieldType.POSITION_1) {
      state.getCurrentPlayer().changeCarrotsBy(10);
    } else if (state.isFirst(state.getOtherPlayer()) && fieldType == FieldType.POSITION_2) {
      state.getCurrentPlayer().changeCarrotsBy(30);
    }
  }

  /**
   * Vergleichsmethode fuer einen Zug. Zwei Züge sind gleich, wenn sie die
   * gleichen Teilaktionen beinhalten
   */
  @Override
  public boolean equals(Object o) {
    if (o instanceof Move) {
      Move move = (Move) o;
      for (Action action : move.getActions()) {
        if (!this.actions.contains(action)) {
          return false;
        }
      }
      for (Action action : this.actions) {
        if (!move.actions.contains(action)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    String toString = "Zug mit folgenden Aktionen \n";
    StringBuilder b = new StringBuilder(toString);
    for (Action action : getActions()) {
      if (action instanceof Advance) {
        b.append(action.toString());
        b.append("\n");
      } else if (action instanceof EatSalad) {
        b.append(action.toString());
        b.append("\n");
      } else if (action instanceof Card) {
        b.append(action.toString());
        b.append("\n");
      } else if (action instanceof ExchangeCarrots) {
        b.append(action.toString());
        b.append("\n");
      } else if (action instanceof FallBack) {
        b.append(action.toString());
        b.append("\n");
      } else if (action instanceof Skip) {
        b.append(action.toString());
        b.append("\n");
      }
    }
    return b.toString();
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

  /**
   * Sortiert die Aktionen aufsteigend anhand des order Attributs
   */
  public void orderActions() {
    if (actions != null) {
      actions.sort(Action::compareTo);
    }
  }

}
