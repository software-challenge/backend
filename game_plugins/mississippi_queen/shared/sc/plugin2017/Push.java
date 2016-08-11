package sc.plugin2017;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import sc.plugin2017.util.InvalidMoveException;

@XStreamAlias(value = "push")
public class Push extends Action {

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
  public int perform(GameState state, Player player) throws InvalidMoveException {
    Field pushFrom = player.getField(state.getBoard());
    Field pushTo = pushFrom.getFieldInDirection(direction, state.getBoard());
    if(pushTo == null || !pushTo.isPassable()
        || pushFrom.getType() == FieldType.SANDBAR) {
      throw new InvalidMoveException("Ungültiges Abdrängen");
    }
    int neededSpeed = 1;
    if(pushTo.getType() == FieldType.LOG) {
      neededSpeed++;
    }
    if(pushFrom.getFieldInDirection(GameState.getOppositeDirection(player.getDirection()), state.getBoard()).equals(pushTo)) { // es darf nicht nach hinten abgedrängt werden
      throw new InvalidMoveException("Ungültiges Abdrängen");
    }
    if(pushTo.getType() == FieldType.SANDBAR) {
      state.getOtherPlayer().setSpeed(1);
    }
   // change Position of opponent player
    state.put(pushTo.getX(), pushTo.getY(), state.getOtherPlayer());
    
    return neededSpeed;
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
