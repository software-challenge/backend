package sc.plugin2017;

import java.util.ArrayList;
import java.util.List;

import sc.plugin2017.util.InvalidMoveException;

public class Step extends Action {

  /**
   * Anzahl der Felder, die zurückgelegt werden.
   */
  public int distance;
  /**
   * Zeigt an, um wie viel die Geschwindigkeit am Ende des Zuges verrinegert werden soll
   */
  protected int reduceSpeed;
  /**
   * Das fahren auf eine Sandbank beendet den Zug
   */
  protected boolean endsTurn;
  
  public Step(int distance) {
    this.distance = distance;
    reduceSpeed = 0;
    endsTurn = false;
  }
  
  @Override
  public int perform(GameState state, Player player) throws InvalidMoveException {
    int neededSpeed = 0;
    Field start = player.getField(state.getBoard());
    List<Field> nextFields = new ArrayList<Field>();
    int direction = player.getDirection();
    if(distance == 0 || distance > 6) {
      throw new InvalidMoveException("Zurückgelegte Distanz ist ungültig.");
    }
    if(distance == -1) { // Fall rückwärts von Sandbank
      if(start.getType() != FieldType.SANDBAR) {
        throw new InvalidMoveException("Rückwärtszug ist nur von Sandbank aus möglich.");
      }
      Field next = start.getFieldInDirection(state.getOppositeBoatDirection(direction));
      if(next == null || next.getType() == FieldType.LOG || !next.isPassable()) {
        throw new InvalidMoveException("Der Weg ist versperrt");
      }
      state.getBoard().put(next.getX(), next.getY(), player);
    } else {
      nextFields.add(start);
      // Kontrolliere für die Zurückgelegte Distanz, wie viele Bewegunsgpunkte verbraucht werden und ob es möglich ist, soweit zu ziehen
      for(int i = 0; i < distance; i++) {
        nextFields.add(nextFields.get(0).getFieldInDirection(player.getDirection()));
        Field checkField = nextFields.get(i);
        if(!checkField.isPassable() || state.getOtherPlayer().getField(state.getBoard()).equals(checkField)) {
          throw new InvalidMoveException("Feld ist blockiert. Ungültiger Zug.");
        }
        if(checkField.getType() == FieldType.SANDBAR) {
          reduceSpeed = player.getSpeed() - 1;
          endsTurn = true;
          if(i + 1 != distance) {
            // Zug endet hier, also darf nicht weitergelaufen werden
            throw new InvalidMoveException("Zug sollte bereits enden, da auf Sandbank gefahren wurde.");
          }
          return player.getSpeed();
        } else if(checkField.getType() == FieldType.LOG) {
          reduceSpeed++;
          neededSpeed += 2;
        } else {
          neededSpeed += 1;
        }
        
      }
    }
    return neededSpeed;
  }

}
