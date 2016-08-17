package sc.plugin2017;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import sc.plugin2017.util.InvalidMoveException;

@XStreamAlias(value = "push")
public class Push extends Action {

  
  /**
   * Zeigt an welche Nummer die Aktion hat
   */
  @XStreamAsAttribute
  public int order;
  /**
   * Richtung in die abgedrängt werden soll
   */
  @XStreamAsAttribute
  public int direction;
 
  public Push(int direction) {
    this.direction = direction;
  }
  
  public Push(int direction, int order) {
    this.direction = direction;
    this.order = order;
  }
  
  @Override
  public void perform(GameState state, Player player) throws InvalidMoveException {
    if(player.getMovement() == 0) {
      throw new InvalidMoveException("Keine Bewegunspunkte mehr vorhanden");
    }
    Field pushFrom = player.getField(state.getBoard());
    Field pushTo = pushFrom.getFieldInDirection(direction, state.getBoard());
    if(pushTo == null || !pushTo.isPassable()
        || pushFrom.getType() == FieldType.SANDBANK) {
      throw new InvalidMoveException("Ungültiges Abdrängen");
    }
    // pushing costs 1 movement point
    player.setMovement(player.getMovement() - 1);
    if(pushTo.getType() == FieldType.LOG) {
      // driving through logs reduces speed and movement by +1
      player.setSpeed(player.getSpeed() - 1);
      player.setMovement(player.getMovement() - 1);
    }
    if(pushFrom.getFieldInDirection(GameState.getOppositeDirection(player.getDirection()), state.getBoard()).equals(pushTo)) { // es darf nicht nach hinten abgedrängt werden
      throw new InvalidMoveException("Ungültiges Abdrängen");
    }
    if(pushTo.getType() == FieldType.SANDBANK) {
      state.getOtherPlayer().setSpeed(1);
      state.getOtherPlayer().setMovement(1);
    }
   // change Position of opponent player
    state.put(pushTo.getX(), pushTo.getY(), state.getOtherPlayer());
    
    return;
  }
  
  public Push clone() {
    return new Push(this.direction);
  }
  
  public boolean equals(Object o) {
    if(o instanceof Push) {
      return (this.direction == ((Push) o).direction);
    }
    return false;
  }
  
  public String toString()  {
    return "abdrängen in Richtung " + direction;
  }

}
