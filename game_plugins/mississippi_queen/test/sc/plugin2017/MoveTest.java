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
    red.setSpeed(1);

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

  @Test
  public void pushIntoLog() throws InvalidMoveException {
    // Red player pushes blue player into log field, blue player should lose one speed.
    String tileString =
        ".W.W.W.W...\n" +
        "..r.b.L.W..\n" +
        "...W.W.W.W.\n" +
        "..W.W.W.W..\n" +
        ".W.W.W.W...\n";
    board.getTiles().set(0, TextTileHelper.parseTile(tileString, -2, -2));
    blue.setX(0);
    blue.setY(-1);
    blue.setDirection(1);
    blue.setMovement(3);
    red.setX(-1);
    red.setY(-1);
    red.setDirection(0);
    red.setMovement(1);

    Move move = new Move();
    move.actions.add(new Acceleration(1, 0));
    move.actions.add(new Step(1, 1));
    move.actions.add(new Push(0, 2));

    move.perform(state, red);
    // blue player should be pushed to 1,-1
    assertEquals(board.getField(1, -1), blue.getField(board));
    assertEquals(2, blue.getMovement());
  }

  @Test
  public void pushIntoSandbank() throws InvalidMoveException {
    // Red player pushes blue player into sandbank field, blue player should lose all speed.
    String tileString =
        ".W.W.W.W...\n" +
        "..r.b.S.W..\n" +
        "...W.W.W.W.\n" +
        "..W.W.W.W..\n" +
        ".W.W.W.W...\n";
    board.getTiles().set(0, TextTileHelper.parseTile(tileString, -2, -2));
    blue.setX(0);
    blue.setY(-1);
    blue.setDirection(1);
    blue.setMovement(3);
    red.setX(-1);
    red.setY(-1);
    red.setDirection(0);
    red.setMovement(1);

    Move move = new Move();
    move.actions.add(new Acceleration(1, 0));
    move.actions.add(new Step(1, 1));
    move.actions.add(new Push(0, 2));

    move.perform(state, red);
    // blue player should be pushed to 1,-1
    assertEquals(board.getField(1, -1), blue.getField(board));
    assertEquals(1, blue.getMovement());
  }

  @Test
  public void additionalTurnsCostCoal() throws InvalidMoveException {
    // Red player turns three times and should lose two coal.
    String tileString =
        ".W.W.W.W...\n" +
        "..r.W.W.W..\n" +
        "...W.W.W.W.\n" +
        "..b.W.W.W..\n" +
        ".W.W.W.W...\n";
    board.getTiles().set(0, TextTileHelper.parseTile(tileString, -2, -2));
    blue.setX(-1);
    blue.setY(1);
    red.setX(-1);
    red.setY(-1);
    red.setDirection(0);
    red.setSpeed(2);
    red.setCoal(6);

    Move move = new Move();
    move.actions.add(new Step(1, 0));
    move.actions.add(new Turn(-1, 1));
    move.actions.add(new Turn(-1, 2));
    move.actions.add(new Step(1, 3));
    move.actions.add(new Turn(1, 4));

    move.perform(state, red);
    // red player should arrive at -1,0
    assertEquals(board.getField(-1, 0), red.getField(board));
    assertEquals(5, red.getDirection());
    assertEquals(4, red.getCoal());
  }
}
