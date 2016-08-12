package sc.plugin2017;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import sc.plugin2017.util.InvalidMoveException;

@XStreamAlias(value = "turn")
public class Turn extends Action {

  
  /**
   * Zeigt an welche Nummer die Aktion hat
   */
  @XStreamAsAttribute
  public int order;
  /**
   * Richtung in der die Drehung erfolgen soll und wie viel gedreht wird.
   */
  @XStreamAsAttribute
  public int direction;
  
  /**
   * Legt einen neuen Drehzug an
   * @param direction Richung der Drehung
   */
  public Turn(int direction) {
    this.direction = direction;
  }
  
  public Turn(int direction, int order) {
    this.direction = direction;
    this.order = order;
  }
  
  /**
   * @param state Gamestate
   * @param player Spieler der die Aktion ausführt
   * @return Anzahl der Drehungen
   */
  @Override
  public int perform(GameState state, Player player) throws InvalidMoveException {
    if(direction == 0 || direction < -3 || direction > 3) {
      throw new InvalidMoveException("Drehung ist ungültig.");
    }
    int currentDirection = player.getDirection();
    currentDirection += direction;
    currentDirection = (currentDirection + 6/*Anzahl der Richtungen*/) % 6; // echtes Modulo nicht java modulo
    return Math.abs(direction);
  }
  
  public Turn clone() {
    return new Turn(this.direction);
  }
  
  public boolean equals(Object o) {
    if(o instanceof Turn) {
      return (this.direction == ((Turn) o).direction);
    }
    return false;
  }
  
  public String toString() {
    return "Drehung um " + direction;
  }

}
