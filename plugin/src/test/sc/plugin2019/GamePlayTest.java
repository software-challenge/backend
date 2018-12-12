package sc.plugin2019;

import org.junit.Before;
import org.junit.Test;
import sc.framework.plugins.Player;
import sc.plugin2019.util.Constants;
import sc.plugin2019.util.GameRuleLogic;
import sc.plugin2019.util.TestGameUtil;
import sc.shared.InvalidMoveException;
import sc.shared.PlayerColor;
import sc.shared.WinCondition;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static sc.plugin2019.Direction.*;
import static sc.plugin2019.FieldState.EMPTY;
import static sc.plugin2019.FieldState.RED;
import static sc.plugin2019.util.GameRuleLogic.*;
import static sc.plugin2019.util.TestJUnitUtil.assertThrows;

public class GamePlayTest {

  private Game game;
  private GameState state;
  private Player red;
  private Player blue;

  @Before
  public void beforeEveryTest() {
    game = new Game();
    state = game.getGameState();

    red = state.getPlayer(PlayerColor.RED);
    blue = state.getPlayer(PlayerColor.BLUE);
    state.setCurrentPlayerColor(PlayerColor.RED);
  }

  @Test
  public void onlyEndAfterRoundTest() throws InvalidMoveException {
    Board board = TestGameUtil.createCustomBoard("" +
            "-BBBBBBBB-" +
            "----B----R" +
            "----BBB--R" +
            "----B----R" +
            "----BO---R" +
            "----B--BRR" +
            "----R-O--R" +
            "------R--R" +
            "---------R" +
            "----------"
    );
    state.setBoard(board);
    {
      Move move = new Move(9, 3, UP_LEFT);
      move.perform(state);
      assertEquals(null, game.checkWinCondition());
    }
    {
      Move move = new Move(1, 0, RIGHT);
      move.perform(state);
      assertEquals(new WinCondition(PlayerColor.BLUE, Constants.WINNING_MESSAGE), game.checkWinCondition());
    }
  }

  @Test
  public void fieldTest() {
    Board board = TestGameUtil.createCustomBoard("" +
            "-BBBBBBB--" +
            "RB-------R" +
            "RO-------R" +
            "R-O------R" +
            "R--------R" +
            "R--------R" +
            "R--------R" +
            "R--------R" +
            "R--------R" +
            "-BBBBBBBB-"
    );
    state.setBoard(board);
    Field fieldRed = board.getField(0, 5);
    Field fieldEmpty = board.getField(5, 5);

    assertThrows(IllegalStateException.class, () -> fieldRed.setPiranha(null));
    fieldRed.setPiranha(PlayerColor.RED);
    assertTrue(fieldRed.getPiranha().isPresent());
    assertEquals(PlayerColor.RED, fieldRed.getPiranha().get());

    assertFalse(fieldEmpty.getPiranha().isPresent());
  }

  @Test
  public void testMoveDirection() {
    Board board = state.getBoard();
    assertEquals(2, calculateMoveDistance(board, 0, 1, RIGHT));
    assertEquals(2, calculateMoveDistance(board, 0, 8, Direction.UP_RIGHT));
    assertEquals(0, calculateMoveDistance(board, 9, 9, Direction.UP_RIGHT));
    assertEquals(2, calculateMoveDistance(board, 0, 1, Direction.DOWN_RIGHT));
    assertEquals(0, calculateMoveDistance(board, 0, 0, Direction.DOWN_RIGHT));
  }

  @Test
  public void testGreatestSwarm() {
    Board board = state.getBoard();
    assertEquals(8, greatestSwarmSize(board, PlayerColor.RED));
    assertEquals(8, greatestSwarmSize(board, PlayerColor.BLUE));

    board = TestGameUtil.createCustomBoard("" +
            "-BBBBBBB--" +
            "RB--------" +
            "RO--------" +
            "R-O-------" +
            "RRRRRRRRRR" +
            "R---------" +
            "R---------" +
            "R---------" +
            "----------" +
            "-BBBBBBBB-"
    );
    state.setBoard(board);

    assertEquals(16, getOwnFields(board, PlayerColor.RED).size());
    for (Field field : getOwnFields(board, PlayerColor.RED)) {
      assertEquals(RED, field.getState());
    }

    board = TestGameUtil.createCustomBoard("" +
            "BBBBBBBBBB" +
            "-B-B--B---" +
            "RO-B--B---" +
            "R-O---B---" +
            "RRRRRRRRRR" +
            "R---------" +
            "R---------" +
            "R---------" +
            "----------" +
            "----------"
    );
    state.setBoard(board);
    assertEquals(16, greatestSwarmSize(board, PlayerColor.BLUE));

    board = TestGameUtil.createCustomBoard("" +
            "BBBBBBBBBB" +
            "-B-B--B---" +
            "RO-B--B---" +
            "R-O---B---" +
            "R-RRRRRRRR" +
            "R---------" +
            "R---------" +
            "R---------" +
            "----------" +
            "----------"
    );
    state.setBoard(board);
    board.getField(1, 5).setState(EMPTY);
    assertEquals(8, greatestSwarmSize(board, PlayerColor.RED));

  }

  @Test
  public void testValidMove() throws InvalidMoveException {
    Board board = TestGameUtil.createCustomBoard("" +
            "-BBBBBBB--" +
            "RB-------R" +
            "RO-------R" +
            "R-O------R" +
            "R--------R" +
            "R--------R" +
            "R--------R" +
            "R--------R" +
            "R--------R" +
            "-BBBBBBBB-"
    );
    state.setBoard(board);

    assertTrue(isValidToMove(state, 0, 4, RIGHT, 2));
    assertThrows(InvalidMoveException.class, () -> isValidToMove(state, 0, 0, RIGHT, 2), "Field does not belong to the current player");
    assertThrows(InvalidMoveException.class, () -> isValidToMove(state, 0, 4, RIGHT, 3), "Move distance was incorrect");
    assertThrows(InvalidMoveException.class, () -> isValidToMove(state, 0, 4, LEFT, 2), "Move in that direction would not be on the board");
    assertThrows(InvalidMoveException.class, () -> isValidToMove(state, 0, 1, RIGHT, 3), "Path to the new position is not clear");
    assertTrue(isValidToMove(state, 0, 2, RIGHT, 2));
    assertThrows(InvalidMoveException.class, () -> isValidToMove(state, 0, 3, RIGHT, 2), "Field is obstructed");

  }

  @Test
  public void testObstructedFieldGeneration() {
    int count = 0;
    List<Field> obstructedFields = new ArrayList<>();
    for (int x = 0; x < Constants.BOARD_SIZE; x++) {
      for (int y = 0; y < Constants.BOARD_SIZE; y++) {
        if (state.getBoard().getField(x, y).isObstructed()) {
          if (count < Constants.NUM_OBSTACLES) {
            count++;
          } else {
            fail("More than two obstructed fields found");
          }
          assertTrue(x > 1);
          assertTrue(y > 1);
          assertTrue(x < Constants.BOARD_SIZE - 2);
          assertTrue(y < Constants.BOARD_SIZE - 2);
          for (Field field : obstructedFields) {
            // check whether second field is in same line (diagonal, vertical or horizontal as first one)
            assertNotEquals(field.getX(), x); // horizontal
            assertNotEquals(field.getY(), y); // vertical
            assertNotEquals(field.getX() - field.getY(), x - y); // downleft to topright diagonal
            assertNotEquals(field.getX() + field.getY(), x + y); // downright to topleft diagonal
          }
          obstructedFields.add(state.getBoard().getField(x, y));
        }
      }
    }
  }

  @Test
  public void testNoWinAfterPiranhaEaten() throws InvalidMoveException {
    Board board = TestGameUtil.createCustomBoard("" +
            "-BBBBBBB--" +
            "R---------" +
            "R-R-------" +
            "R-O-------" +
            "R---------" +
            "R---------" +
            "R---------" +
            "R---------" +
            "-R--------" +
            "-B--------"
    );
    state.setBoard(board);
    assertEquals(7, GameRuleLogic.greatestSwarmSize(board, PlayerColor.BLUE));
    assertEquals(8, GameRuleLogic.greatestSwarmSize(board, PlayerColor.RED));

    {
      Move move = new Move(1, 8, Direction.LEFT);
      move.perform(state);
      assertEquals(null,  game.checkWinCondition());
    }

    {
      Move move = new Move(2, 0, Direction.UP);
      move.perform(state);
      assertNotEquals(null, game.checkWinCondition());
    }
    assertEquals(5, GameRuleLogic.greatestSwarmSize(board, PlayerColor.BLUE));
    assertEquals(8, GameRuleLogic.greatestSwarmSize(board, PlayerColor.RED));
    assertEquals(PlayerColor.BLUE, board.getField(2,2).getPiranha().get());
  }
}
