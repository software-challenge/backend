package sc.plugin2017;

import static org.junit.Assert.assertEquals;

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
  public void moveOntoBlockedField() throws InvalidMoveException {

    // Moving the red player one step in its direction will move him into a
    // blocked field.
    String tileString =
        ".W.W.W.W...\n" +
        "..b.W.W.W..\n" +
        "...W.W.W.W.\n" +
        "..r.B.W.W..\n" +
        ".W.W.W.W...\n";
    board.getTiles().set(0, TextTileHelper.parseTile(tileString, -2, -2));

    Move move = new Move();
    Action action = new Step(1);
    move.actions.add(action);

    move.perform(state, red);
  }

  @Test(expected = InvalidMoveException.class)
  public void pushWhenNotOnOpponent() throws InvalidMoveException {
    // Red player moves to right upper field and should not be able to push blue
    // player.
    String tileString =
        ".W.W.W.W...\n" +
        "..r.b.W.W..\n" +
        "...W.W.W.W.\n" +
        "..W.W.W.W..\n" +
        ".W.W.W.W...\n";
    board.getTiles().set(0, TextTileHelper.parseTile(tileString, -2, -2));
    blue.setX(0);
    blue.setY(-1);
    blue.setDirection(1);
    red.setX(-1);
    red.setY(-1);
    red.setDirection(5);
    red.setMovement(1);

    Move move = new Move();
    move.actions.add(new Acceleration(1, 0));
    move.actions.add(new Step(1, 1));
    move.actions.add(new Push(4, 2));

    move.perform(state, red);
  }

  @Test
  public void validPush() throws InvalidMoveException {
    // Red player should be able to push blue player.
    String tileString =
        ".W.W.W.W...\n" +
        "..r.b.W.W..\n" +
        "...W.W.W.W.\n" +
        "..W.W.W.W..\n" +
        ".W.W.W.W...\n";
    board.getTiles().set(0, TextTileHelper.parseTile(tileString, -2, -2));
    blue.setX(0);
    blue.setY(-1);
    blue.setDirection(1);
    red.setX(-1);
    red.setY(-1);
    red.setDirection(0);
    red.setMovement(1);

    Move move = new Move();
    move.actions.add(new Acceleration(1, 0));
    move.actions.add(new Step(1, 1));
    move.actions.add(new Push(4, 2));
    move.actions.add(new Turn(1, 3));

    assertEquals(1, blue.getDirection());
    move.perform(state, red);
    // blue player should be pushed to 0,0
    assertEquals(board.getField(-1, 0), blue.getField(board));
  }

}
