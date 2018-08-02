package sc.plugin2019;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.IMove;
import sc.plugin2019.util.GameRuleLogic;
import sc.shared.DebugHint;
import sc.shared.InvalidGameStateException;
import sc.shared.InvalidMoveException;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Ein Spielzug. Ein Spielzug wird von dem derzeitgen Spieler eines GameStates ausgeführt. Er hat folgende Form:
 * - er besteht aus einer Koordinate (für einen Piranha)
 * - er besteht aus einer Advance Aktion und eventuell darauf folgende Kartenaktionen
 */
@XStreamAlias(value = "move")
public class Move implements Cloneable, IMove {
  // TODO maybe add argument that piranha was removed for lastMove in Gamestate to use commando pattern

  @XStreamOmitField
  private static final Logger logger = LoggerFactory.getLogger(Move.class);

  @XStreamAsAttribute
  public int x = -1;

  @XStreamAsAttribute
  public int y = -1;

  @XStreamAsAttribute
  public Direction direction;

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
  }

  /**
   * Erzeugt einen neuen Zug aus Liste von Aktionen. Die Liste der Aktionen wird kopiert
   * (d.h. eine Änderung der Liste nach Erstellung des Zuges ändert den Zug nicht mehr).
   *
   * @param x Aktionen des Zuges
   */
  public Move(int x, int y, Direction direction) {
    this.x = x;
    this.y = y;
    this.direction = direction;
  }

  /**
   * erzeugt eine Deepcopy dieses Objektes
   *
   * @return ein neues Objekt mit gleichen Eigenschaften
   */
  @Override
  public Move clone() {
    Move clone = new Move(this.x, this.y, this.direction);
    return clone;
  }

  /**
   * Fuegt eine Debug-Hilfestellung hinzu. Diese kann waehrend des Spieles vom
   * Programmierer gelesen werden, wenn der Client einen Zug macht.
   *
   * @param hint hinzuzufuegende Debug-Hilfestellung
   */
  public void addHint(DebugHint hint) {
    if (hints == null) {
      hints = new LinkedList<>();
    }
    hints.add(hint);
  }

  /**
   * Fuegt eine Debug-Hilfestellung hinzu. Diese kann waehrend des Spieles vom
   * Programmierer gelesen werden, wenn der Client einen Zug macht.
   *
   * @param key   Schluessel
   * @param value zugehöriger Wert
   */
  public void addHint(String key, String value) {
    addHint(new DebugHint(key, value));
  }

  /**
   * Fuegt eine Debug-Hilfestellung hinzu. Diese kann waehrend des Spieles vom
   * Programmierer gelesen werden, wenn der Client einen Zug macht.
   *
   * @param string Debug-Hilfestellung
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
    return hints == null ? Collections.emptyList() : hints;
  }

  /**
   * Führt einen Zug aus, indem alle Aktionen aufsteigend anand des order Attributes ausgeführt werden.
   * Dabei werden zusätzlich folgende Informationen geupdated:
   * lastMove wird gesetzt, lastNonSkipAktion wird gesetzt, turn wird um eins erhöht.
   * der currentPlayer wird getauscht, falls sich der nächste Spieler auf entsprechendem
   * Positionsfeld befindet werden seine Karotten erhöht.
   *
   * @param state Spielstatus
   *
   * @throws InvalidMoveException wird geworfen, wenn der Zug nicht den Regeln entspricht
   */
  public void perform(GameState state) throws InvalidMoveException, InvalidGameStateException {
    // TODO perform move
    int distance = state.calculateMoveDistance(x, y, direction);
    System.out.println("distance: " + distance);
    if (GameRuleLogic.isValidToMove(x, y, direction, distance, state)) {
      Field start = state.getField(x, y);
      Field destination = state.getFieldInDirection(x, y, direction, distance);
      start.setState(FieldState.EMPTY);

      destination.setPiranha(state.getCurrentPlayerColor());

    }

    System.out.println(state.getField(x, y));

    // Bereite nächsten Zug vor:
    state.setLastMove(this);
    state.setTurn(state.getTurn() + 1);
    state.switchCurrentPlayer(); // depends on turn
  }

  /**
   * Vergleichsmethode fuer einen Zug. Zwei Züge sind gleich, wenn sie die
   * gleichen Koordinaten und Richtung haben
   */
  @Override
  public boolean equals(Object o) {
    if (o instanceof Move) {
      if (this.x == ((Move) o).x &&
              this.y == ((Move) o).y &&
              this.direction == ((Move) o).direction) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    String toString = "Zug: \nx = " + x + ", y = " + y + ", direction = " + direction;
    return toString;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public Direction getDirection() {
    return direction;
  }
}
