package sc.plugin2017;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import sc.plugin2017.util.InvalidMoveException;

public class MoveTest {

  private GameState state;
  private Board board;
  private Player red;
  private Player blue;

  @Before
  public void setupGameState() {

    state = new GameState();
    red = new Player(PlayerColor.RED);
    blue = new Player(PlayerColor.BLUE);

    state.addPlayer(red);
    state.addPlayer(blue);

    board = state.getBoard();
  }

  @Test(expected = InvalidMoveException.class)
  public void testPerform() throws InvalidMoveException {

    red.getField(board).getFieldInDirection(red.getDirection(), board).setType(FieldType.BLOCKED);

    Move move = new Move();
    Action action = new Step(1);
    move.actions.add(action);

    move.perform(state, red);
  }

}
