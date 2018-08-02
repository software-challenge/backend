package sc.plugin2017;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import sc.plugin2017.util.InvalidMoveException;

@XStreamAlias(value = "turn")
public class Turn extends Action {

  /**
   * Wie viel gedreht wird.
   */
  @XStreamAsAttribute
  public int direction;

  public Turn() {
    order = 0;
    direction = 0;
  }

  /**
   * Legt einen neuen Drehzug an
   * @param direction Wert, um wie viel gedreht wird
   */
  public Turn(int direction) {
    this.direction = direction;
  }

  /**
   * Legt einen neuen Drehzug an
   * @param direction Wert, um wie viel gedreht wird
   * @param order Nummer der Aktion. Aktionen werden aufsteigend sortiert nach
   *              ihrer Nummer ausgeführt.
   */
  public Turn(int direction, int order) {
    this.direction = direction;
    this.order = order;
  }

  /**
   * @param state Gamestate
   * @param player Spieler der die Aktion ausführt
   */
  @Override
  public void perform(GameState state, Player player) throws InvalidMoveException {
    if(direction == 0 || direction < -3 || direction > 3) {
      throw new InvalidMoveException("Drehung ist ungültig.");
    }
    if(player.getField(state.getBoard()).getType() == FieldType.SANDBANK) {
      throw new InvalidMoveException("Drehung auf Sandbank nicht erlaubt.");
    }
    Direction newDirection = player.getDirection().getTurnedDirection(direction);
    int usedCoal = Math.abs(direction) - player.getFreeTurns();
    int test = player.getFreeTurns() - Math.abs(direction);
    if(test <= 0) {
      player.setFreeTurns(0);
    } else {
      player.setFreeTurns(1); // only possible, wenn freeTurn was 2 und player turns by 1
    }
    if(usedCoal > 0) {
      if(player.getCoal() >= usedCoal) {
        player.setCoal(player.getCoal() - usedCoal);
      } else {
        throw new InvalidMoveException("Nicht genug Kohle für Drehung.");
      }
    }
    player.setDirection(newDirection);
    return;
  }

  @Override
  public Turn clone() {
    return new Turn(this.direction, this.order);
  }

  @Override
  public boolean equals(Object o) {
    if(o instanceof Turn) {
      return (this.direction == ((Turn) o).direction);
    }
    return false;
  }

  @Override
  public String toString() {
    return "Drehe um " + direction;
  }

}
