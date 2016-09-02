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
    TextTileHelper.updatePlayerPosition(tileString, -2, -2, blue);
    TextTileHelper.updatePlayerPosition(tileString, -2, -2, red);

    Move move = new Move();
    Action action = new Advance(1);
    move.actions.add(action);

    move.perform(state, red);
  }

  @Test(expected = InvalidMoveException.class)
  public void moveOntoNotExistingField() throws InvalidMoveException {

    // Moving the red player one step in its direction will move him into a
    // blocked field.
    String tileString =
        ".W.W.W.W...\n" +
        "..b.r.W.W..\n" +
        "...W.W.W.W.\n" +
        "..W.W.W.W..\n" +
        ".W.W.W.W...\n";
    board.getTiles().set(0, TextTileHelper.parseTile(tileString, -2, -2));
    TextTileHelper.updatePlayerPosition(tileString, -2, -2, blue);
    TextTileHelper.updatePlayerPosition(tileString, -2, -2, red);
    red.setDirection(Direction.UP_RIGHT);
    red.setSpeed(2);

    Move move = new Move();
    Action action = new Advance(2);
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
    TextTileHelper.updatePlayerPosition(tileString, -2, -2, blue);
    TextTileHelper.updatePlayerPosition(tileString, -2, -2, red);
    blue.setDirection(Direction.UP_RIGHT);
    red.setDirection(Direction.DOWN_RIGHT);
    red.setMovement(1);

    Move move = new Move();
    move.actions.add(new Acceleration(1, 0));
    move.actions.add(new Advance(1, 1));
    move.actions.add(new Push(Direction.DOWN_LEFT, 2));

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
    TextTileHelper.updatePlayerPosition(tileString, -2, -2, blue);
    TextTileHelper.updatePlayerPosition(tileString, -2, -2, red);
    blue.setDirection(Direction.UP_RIGHT);
    red.setDirection(Direction.RIGHT);
    red.setSpeed(1);

    Move move = new Move();
    move.actions.add(new Acceleration(1, 0));
    move.actions.add(new Advance(1, 1));
    move.actions.add(new Push(Direction.DOWN_LEFT, 2));
    move.actions.add(new Turn(1, 3));

    assertEquals(Direction.UP_RIGHT, blue.getDirection());
    move.perform(state, red);
    // blue player should be pushed to 0,0
    assertEquals(board.getField(-1, 0), blue.getField(board));
  }

  @Test(expected = InvalidMoveException.class)
  public void invalidMoveOntoOpponent() throws InvalidMoveException {
    // Red player should not be able to move onto blue player field (without pushing)
    String tileString =
        ".W.W.W.W...\n" +
        "..r.W.b.W..\n" +
        "...W.W.W.W.\n" +
        "..W.W.W.W..\n" +
        ".W.W.W.W...\n";
    board.getTiles().set(0, TextTileHelper.parseTile(tileString, -2, -2));
    TextTileHelper.updatePlayerPosition(tileString, -2, -2, blue);
    TextTileHelper.updatePlayerPosition(tileString, -2, -2, red);
    blue.setDirection(Direction.UP_RIGHT);
    red.setDirection(Direction.RIGHT);
    red.setSpeed(2);

    Move move = new Move();
    move.actions.add(new Advance(2, 1));

    move.perform(state, red);
  }

  @Test(expected = InvalidMoveException.class)
  public void invalidPushNotEnoughMovement() throws InvalidMoveException {
    // Red player should not be able to push blue player because he only has speed 1.
    String tileString =
        ".W.W.W.W...\n" +
        "..r.W.b.W..\n" +
        "...W.W.W.W.\n" +
        "..W.W.W.W..\n" +
        ".W.W.W.W...\n";
    board.getTiles().set(0, TextTileHelper.parseTile(tileString, -2, -2));
    TextTileHelper.updatePlayerPosition(tileString, -2, -2, blue);
    TextTileHelper.updatePlayerPosition(tileString, -2, -2, red);
    blue.setDirection(Direction.UP_RIGHT);
    red.setDirection(Direction.RIGHT);
    red.setSpeed(2);

    Move move = new Move();
    move.actions.add(new Advance(2, 1));
    move.actions.add(new Push(Direction.DOWN_LEFT, 2));

    move.perform(state, red);
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
    TextTileHelper.updatePlayerPosition(tileString, -2, -2, blue);
    TextTileHelper.updatePlayerPosition(tileString, -2, -2, red);
    blue.setDirection(Direction.UP_RIGHT);
    blue.setMovement(3);
    red.setDirection(Direction.RIGHT);
    red.setMovement(1);

    Move move = new Move();
    move.actions.add(new Acceleration(1, 0));
    move.actions.add(new Advance(1, 1));
    move.actions.add(new Push(Direction.RIGHT, 2));

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
    TextTileHelper.updatePlayerPosition(tileString, -2, -2, blue);
    TextTileHelper.updatePlayerPosition(tileString, -2, -2, red);
    blue.setDirection(Direction.UP_RIGHT);
    blue.setMovement(3);
    red.setDirection(Direction.RIGHT);
    red.setMovement(1);

    Move move = new Move();
    move.actions.add(new Acceleration(1, 0));
    move.actions.add(new Advance(1, 1));
    move.actions.add(new Push(Direction.RIGHT, 2));

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
    TextTileHelper.updatePlayerPosition(tileString, -2, -2, blue);
    TextTileHelper.updatePlayerPosition(tileString, -2, -2, red);
    red.setDirection(Direction.RIGHT);
    red.setSpeed(2);
    red.setCoal(6);

    Move move = new Move();
    move.actions.add(new Advance(1, 0));
    move.actions.add(new Turn(-1, 1));
    move.actions.add(new Turn(-1, 2));
    move.actions.add(new Advance(1, 3));
    move.actions.add(new Turn(1, 4));

    move.perform(state, red);
    // red player should arrive at -1,0
    assertEquals(board.getField(-1, 0), red.getField(board));
    assertEquals(Direction.DOWN_RIGHT, red.getDirection());
    assertEquals(4, red.getCoal());
  }


}
