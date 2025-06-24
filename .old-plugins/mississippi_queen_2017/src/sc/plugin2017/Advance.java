package sc.plugin2017;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import sc.plugin2017.util.InvalidMoveException;

@XStreamAlias(value = "advance")
public class Advance extends Action {

  private static final Logger logger = LoggerFactory.getLogger(Advance.class);
  /**
   * Anzahl der Felder, die zurückgelegt werden.
   */
  @XStreamAsAttribute
  public int distance;
  /**
   * Das Fahren auf eine Sandbank beendet den Zug
   */
  @XStreamOmitField
  protected boolean endsTurn;

  public Advance() {
    distance = 0;
    order = 0;
  }

  /**
   * Legt eine neue Laufaktion an
   * @param distance Felder, die überwunden werden
   */
  public Advance(int distance) {
    this.distance = distance;
    endsTurn = false;
  }

  /**
   * Legt eine neue Laufaktion an
   * @param distance Felder, die überwunden werden
   * @param order Nummer der Aktion. Aktionen werden aufsteigend sortiert nach
   *              ihrer Nummer ausgeführt.
   */
  public Advance(int distance, int order) {
    this.distance = distance;
    this.order = order;
    endsTurn = false;
  }

  @Override
  public void perform(GameState state, Ship ship) throws InvalidMoveException {
    if(ship.getMovement() == 0) {
      throw new InvalidMoveException("Keine Bewegunspunkte mehr vorhanden");
    }
    Field start = ship.getField(state.getBoard());
    LinkedList<Field> nextFields = new LinkedList<Field>();
    Direction direction = ship.getDirection();
    if(distance == 0 || distance > 6 || distance < -1) {
      throw new InvalidMoveException("Zurückgelegte Distanz ist ungültig.");
    }
    if(distance == -1) { // Fall rückwärts von Sandbank
      if(start.getType() != FieldType.SANDBANK) {
        throw new InvalidMoveException("Rückwärtszug ist nur von Sandbank aus möglich.");
      }
      Field next = start.getFieldInDirection(direction.getOpposite(), state.getBoard());
      if(next == null || next.getType() == FieldType.LOG || !next.isPassable()) {
        throw new InvalidMoveException("Der Weg ist versperrt");
      }
      state.put(next.getX(), next.getY(), ship);
      ship.setMovement(0);
      ship.setCoal(ship.getCoal() - 1);
      return;
    } else {
      if(start.getType() == FieldType.SANDBANK) {
        if(this.distance != 1) {
          throw new InvalidMoveException("Nur eine Bewegung nach vorne auf einer Sandbank möglich");
        }
        ship.setMovement(0);
        Field next = start.getFieldInDirection(direction, state.getBoard());
        if (next == null) {
          throw new InvalidMoveException("Feld ist nicht vorhanden. Ungültiger Zug.");
        }
        if(!next.isPassable()) {
          throw new InvalidMoveException("Feld ist blockiert. Ungültiger Zug.");
        }
        state.put(next.getX(), next.getY(), ship);
        return;
      }
      nextFields.add(start);
      // Kontrolliere für die Zurückgelegte Distanz, wie viele Bewegunsgpunkte verbraucht werden und ob es möglich ist, soweit zu ziehen
      for(int i = 0; i < distance; i++) {
        Field next = nextFields.get(i).getFieldInDirection(ship.getDirection(), state.getBoard());
        if(next != null) {
          nextFields.add(next);
        } else {
          throw new InvalidMoveException("Feld ist nicht vorhanden. Ungültiger Zug.");
        }
        Field checkField = nextFields.get(i + 1); // get next field
        if(!checkField.isPassable() ||
            (state.getOtherPlayer().getField(state.getBoard()).equals(checkField) && i != distance -1)) {
          throw new InvalidMoveException("Feld ist blockiert. Ungültiger Zug.");
        }
        if(checkField.getType() == FieldType.SANDBANK) {
          // case sandbar
          ship.setSpeed(1);
          ship.setMovement(0);
          endsTurn = true;
          if(i != distance - 1) {
            // Zug endet hier, also darf nicht weitergelaufen werden
            throw new InvalidMoveException("Zug sollte bereits enden, da auf Sandbank gefahren wurde.");
          }

          state.put(checkField.getX(), checkField.getY(), ship);
          return;
        } else if(checkField.getType() == FieldType.LOG) {
          if(ship.getMovement() <= 1) {
            throw new InvalidMoveException("Nicht genug Bewegunspunkte vorhanden, um Baumstamm zu überqueren");
          }
          ship.setMovement(ship.getMovement() - 2);
          ship.setSpeed(Math.max(1, ship.getSpeed() - 1));
        } else {
          ship.setMovement(ship.getMovement() - 1);
        }

      }
      Field target = nextFields.get(distance);
      state.put(target.getX(), target.getY(), ship);
    }
    return;
  }

  @Override
  public Advance clone() {
    return new Advance(this.distance, this.order);
  }

  @Override
  public boolean equals(Object o) {
    if(o instanceof Advance) {
      return (this.distance == ((Advance) o).distance);
    }
    return false;
  }

  @Override
  public String toString() {
    return "Gehe " + distance + " vor";
  }

}
