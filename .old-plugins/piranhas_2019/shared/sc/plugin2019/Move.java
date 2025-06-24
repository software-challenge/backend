package sc.plugin2019;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sc.api.plugins.IMove;
import sc.shared.DebugHint;
import sc.shared.InvalidGameStateException;
import sc.shared.InvalidMoveException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static sc.plugin2019.util.GameRuleLogic.*;

/**
 * Ein Spielzug. Ein Spielzug wird von dem derzeitigen Spieler eines GameStates ausgeführt. Er hat folgende Form:
 * - Koordinate des Piranhas
 * - Direction in die sich bewegt wird
 */
@XStreamAlias(value = "move")
public class Move implements Cloneable, IMove {
  // TODO maybe add argument that piranha was removed for lastMove in Gamestate to use commando pattern

  @XStreamOmitField
  private static final Logger logger = LoggerFactory.getLogger(Move.class);

  @XStreamAsAttribute
  public final int x;

  @XStreamAsAttribute
  public final int y;

  @XStreamAsAttribute
  public Direction direction;

  /**
   * Liste von Debughints, die dem Zug beigefügt werden koennen.
   * Siehe {@link DebugHint}
   */
  @XStreamImplicit(itemFieldName = "hint")
  private List<DebugHint> hints;

  /** Erzeugt einen neuen Zug aus von der gegebenen Position in die gegebene Richtung. */
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
    return new Move(this.x, this.y, this.direction);
  }

  /**
   * Fuegt eine Debug-Hilfestellung hinzu. Diese kann waehrend des Spieles vom
   * Programmierer gelesen werden, wenn der Client einen Zug macht.
   *
   * @param hint hinzuzufuegende Debug-Hilfestellung
   */
  public void addHint(DebugHint hint) {
    if (hints == null) {
      hints = new ArrayList<>();
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
   * Führt einen Zug aus, indem alle Aktionen aufsteigend anhand des order Attributes ausgeführt werden.
   * Dabei werden zusätzlich folgende Informationen geupdated:
   * lastMove wird gesetzt, lastNonSkipAktion wird gesetzt, turn wird um eins erhöht.
   * der currentPlayer wird getauscht
   *
   * @throws InvalidMoveException wird geworfen, wenn der Zug nicht den Regeln entspricht
   */
  public void perform(GameState state) throws InvalidMoveException, InvalidGameStateException {
    int distance = calculateMoveDistance(state.getBoard(), x, y, direction);
    if (isValidToMove(state, x, y, direction, distance)) {
      Field start = state.getField(x, y);
      Field destination = getFieldInDirection(state.getBoard(), x, y, direction, distance);
      start.setState(FieldState.EMPTY);
      destination.setPiranha(state.getCurrentPlayerColor());
    }
    logger.debug("Distance: " + distance + " to Field " + state.getField(x, y));

    // Bereite nächsten Zug vor
    state.setLastMove(this);
    state.setTurn(state.getTurn() + 1);
    state.switchCurrentPlayer();
  }

  /**
   * Vergleichsmethode fuer einen Zug. Zwei Züge sind gleich, wenn sie die
   * gleichen Koordinaten und Richtung haben
   */
  @Override
  public boolean equals(Object o) {
    return o instanceof Move &&
            this.x == ((Move) o).x &&
            this.y == ((Move) o).y &&
            this.direction == ((Move) o).direction;
  }

  @Override
  public String toString() {
    return String.format("Zug von (%d|%d) in Richtung %s", x, y, direction);
  }

}
