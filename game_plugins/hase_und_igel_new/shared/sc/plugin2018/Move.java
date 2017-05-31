package sc.plugin2018;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.shared.DebugHint;
import sc.shared.InvalidMoveException;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 
 */
@XStreamAlias(value = "move")
public final class Move implements Cloneable
{
	private static final Logger logger = LoggerFactory.getLogger(Move.class);
	/**
	 * Liste von Aktionen aus denen der Zug besteht. Die Reihenfolge, in der die
	 * Aktionen ausgeführt werden, wird NICHT durch die Reihenfolge in der Liste
	 * bestimmt, sondern durch die Werte im order-Attribut jedes Action objektes:
	 * Die Aktionen werden nach dem order-Attribut aufsteigend sortiert
	 * ausgeführt.
	 */
	@XStreamImplicit
	private List<Action> actions;

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
	private List<DebugHint> hints = new LinkedList<>(this.hints);

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
			// TODO clone different Actions
		}
		Move clone = new Move(clonedActions);
		if (this.hints != null) {
      clone.hints = new LinkedList<>(this.hints);
    }
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
	 TODO comment
	 *
	 * @param state
	 *          Spielstatus
	 * @param player
	 *          ausfuehrender Spieler
	 * @throws InvalidMoveException
	 *           wird geworfen, wenn der Zug nicht den Regeln entspricht
	 */
	public void perform(GameState state, Player player) throws InvalidMoveException {
	  // sort actions according to order
    Collections.sort(this.actions, Action::compareTo); // this may do the right stuff
	  // TODO check actions for something wrong (may do this in perform of actions if possible)
    if (actions.isEmpty()) {
      throw new InvalidMoveException("Keine Aktionen vorhanden.");
      // TODO may use this as a giving up move to avoid unnecessary Exceptions
    }
    // TODO last action may be needed to test whether a actions is allowed (do this by adding this as parameter to perform)
//    if ((actions.get(0) instanceof Skip
//      || actions.get(0) instanceof EatSalad
//      || actions.get(0) instanceof ExchangeCarrots
//      || actions.get(0) instanceof FallBack)
//            && actions.size() > 1) {
//      throw new InvalidMoveException("Eine " + actions.get(0).getClass().getSimpleName() + "-Aktion muss letzte Aktion des Zuges sein.");
//    }
	  // perform actions
    int index = 0;
    for (Action action : this.actions) {
      if (index != action.order) {
        throw new InvalidMoveException("Das order Attribut wurde nicht richtig gesetzt.");
      }
//   TODO   action.perform(state, player);
      state.setLastAction(player, action);
      index++;
    }
    // prepare next turn
    state.setLastMove(player, this);
		state.updateCurrentPlayer();
		// check whether player in next turn gets carrots from Position_X fields
		FieldType fieldType = state.getBoard().getTypeAt(state.getCurrentPlayer().getFieldIndex());
		if (state.isFirst(state.getCurrentPlayer()) && fieldType == FieldType.POSITION_1) {
		  state.getCurrentPlayer().changeCarrotsAvailableBy(10);
    } else if (state.isFirst(state.getOtherPlayer()) && fieldType == FieldType.POSITION_2) {
      state.getCurrentPlayer().changeCarrotsAvailableBy(30);
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
}
