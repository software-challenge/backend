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
  public Direction direction;

  public Push() {
    order = 0;
    direction = Direction.RIGHT;
  }

  /**
   * Erzeugt eine Abdrängaktion in angegebene Richtug
   * @param direction Richtung des Abdrängens
   */
  public Push(Direction direction) {
    this.direction = direction;
  }

  /**
   * Erzeugt eine Abdrängaktion in angegebene Richtug
   * @param direction Richtung des Abdrängens
   * @param order Reihenfolge
   */
  public Push(Direction direction, int order) {
    this.direction = direction;
    this.order = order;
  }

  @Override
  public void perform(GameState state, Player pushingPlayer) throws InvalidMoveException {
    Player nudgedPlayer = state.getOtherPlayer(); // The player who is being pushed (using different verb to make distinction easier).
    if(pushingPlayer.getMovement() == 0) {
      throw new InvalidMoveException("Keine Bewegunspunkte mehr vorhanden");
    }
    Field pushFrom = pushingPlayer.getField(state.getBoard());
    Field pushTo = pushFrom.alwaysGetFieldInDirection(direction, state.getBoard());
    if (!pushFrom.equals(nudgedPlayer.getField(state.getBoard()))) {
      throw new InvalidMoveException("Um einen Spieler abzudrängen muss man sich auf demselben Feld wie der Spieler befinden.");
    }
    if(pushTo == null) {
      throw new InvalidMoveException("Ein Spieler darf nicht auf ein nicht vorhandenes (oder nicht sichtbares) Feld abgedrängt werden.");
    }
    if (pushTo.isBlocked()) {
      throw new InvalidMoveException("Ein Spieler darf nicht auf ein blockiertes Feld abgedrängt werden.");
    }
    if (pushFrom.getType() == FieldType.SANDBANK) {
      throw new InvalidMoveException("Von einer Sandbank ist abdrängen nicht möglich.");
    }
    // pushing costs 1 movement point
    pushingPlayer.setMovement(pushingPlayer.getMovement() - 1);
    if(pushTo.getType() == FieldType.LOG) {
      // driving through logs reduces speed and movement by +1
      nudgedPlayer.setSpeed(nudgedPlayer.getSpeed() - 1);
      nudgedPlayer.setMovement(nudgedPlayer.getMovement() - 1);
    }
    Field fieldBehindPushingPlayer = pushFrom.getFieldInDirection(pushingPlayer.getDirection().getOpposite(), state.getBoard());
    // If fieldBehindPushedPlayer is null, the following check is not needed
    // because pushTo cannot be that field (and pushTo cannot be null as already
    // checked above).
    if(fieldBehindPushingPlayer != null && fieldBehindPushingPlayer.equals(pushTo)) {
      throw new InvalidMoveException("Ein Spieler darf nicht hinter sich, also auf das zu ihm benachbarte Feld entgegen seiner Bewegungsrichtung, abdrängen.");
    }
    if(pushTo.getType() == FieldType.SANDBANK) {
      nudgedPlayer.setSpeed(1);
      nudgedPlayer.setMovement(1);
    }
    // change Position of opponent player
    state.put(pushTo.getX(), pushTo.getY(), nudgedPlayer);

    return;
  }

  @Override
  public Push clone() {
    return new Push(this.direction, this.order);
  }

  @Override
  public boolean equals(Object o) {
    if(o instanceof Push) {
      return (this.direction == ((Push) o).direction);
    }
    return false;
  }

  @Override
  public String toString()  {
    return "Dränge nach " + direction + " ab";
  }

}
