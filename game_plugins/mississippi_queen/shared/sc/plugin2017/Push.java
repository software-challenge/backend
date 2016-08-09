package sc.plugin2017;

import sc.plugin2017.util.InvalidMoveException;

public class Push extends Action {

  
  public int x;
 
  public int y;
  
  protected int reduceSpeed;
 
  public Push(int x, int y) {
    this.x = x;
    this.y = y;
    reduceSpeed = 0;
  }
  @Override
  public int perform(GameState state, Player player) throws InvalidMoveException {
    Field enemy = state.getBoard().getField(x, y);
    Field start = player.getField(state.getBoard());
    Field next = start.getFieldInDirection(player.getDirection());
    if(enemy == null || enemy.getType() == FieldType.BLOCKED
        || enemy.getType() == FieldType.PASSENGER0 
        || enemy.getType() == FieldType.PASSENGER1
        || enemy.getType() == FieldType.PASSENGER2
        || enemy.getType() == FieldType.PASSENGER3
        || enemy.getType() == FieldType.PASSENGER4
        || enemy.getType() == FieldType.PASSENGER5
        || next.getType() == FieldType.SANDBAR) {
      throw new InvalidMoveException("Feld auf das abgedrängt werden soll ist nicht begehbar");
    }
    int neededSpeed = 2;
    if(enemy.getType() == FieldType.LOG) {
      neededSpeed++;
    }
    if(next.getType() == FieldType.LOG) {
      neededSpeed++;
      reduceSpeed = 1;
    }
    if(enemy.equals(next.getFieldInDirection(player.getDirection())) || !next.isNeighborOf(enemy) 
        || enemy.equals(start)) { // es darf nicht nach hinten abgedrängt werden
      throw new InvalidMoveException("Ungültiges Abdrängen");
    }
    
   // change Position of players
    state.getBoard().put(x, y, state.getOtherPlayer());
    state.getBoard().put(next.getX(), next.getY(), state.getCurrentPlayer());
    player.setX(next.getX()); // TODO soll das so?
    player.setY(next.getY());
    
    return neededSpeed;
  }

}
