package sc.plugin2017;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import sc.plugin2017.util.InvalidMoveException;

@XStreamAlias(value = "turn")
public class Turn extends Action {

  /**
   * Richtung in der die Drehung erfolgen soll und wie viel gedreht wird.
   */
  @XStreamAsAttribute
  public int direction;
  
  public Turn() {
    order = 0;
    direction = 0;
  }
  
  /**
   * Legt einen neuen Drehzug an
   * @param direction Richung der Drehung
   */
  public Turn(int direction) {
    this.direction = direction;
  }
  
  /**
   * Legt einen neuen Drehzug an
   * @param direction Richung der Drehung
   * @param order Reigenfolge
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
    int currentDirection = player.getDirection();
    currentDirection += direction;
    currentDirection = (currentDirection + 6/*Anzahl der Richtungen*/) % 6; // echtes Modulo nicht java modulo
    int i;
    for(i = Math.abs(direction); player.getFreeTurns() != 0; i--) {
      player.setFreeTurns(player.getFreeTurns() - 1);
    }
    if(player.getCoal() >= i) {
      player.setCoal(player.getCoal() - i);
    } else {
      throw new InvalidMoveException("Nicht gengug Kohle für Drehung");
    }
    player.setDirection(currentDirection);
    return;
  }
  
  public Turn clone() {
    return new Turn(this.direction, this.order);
  }
  
  public boolean equals(Object o) {
    if(o instanceof Turn) {
      return (this.direction == ((Turn) o).direction);
    }
    return false;
  }
  
  public String toString() {
    return "Drehe um " + direction;
  }

}
