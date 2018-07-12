package sc.plugin2019;

import org.junit.Before;
import org.junit.Test;
import sc.plugin2019.util.Constants;
import sc.plugin2019.util.TestGameUtil;
import sc.shared.InvalidMoveException;
import sc.shared.PlayerColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static sc.plugin2019.Direction.LEFT;
import static sc.plugin2019.Direction.RIGHT;
import static sc.plugin2019.FieldState.*;
import static org.junit.Assert.*;
import static sc.plugin2019.util.GameRuleLogic.isValidToMove;
import static sc.plugin2019.util.TestJUnitUtil.assertThrows;

public class GamePlayTest
{
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
		state.setCurrentPlayer(PlayerColor.RED);
	}

	@Test
  public void fieldTest(){
    Board board = TestGameUtil.createCustomBoard(""+
        "-BBBBBBB--"+
        "RB-------R"+
        "RO-------R"+
        "R-O------R"+
        "R--------R"+
        "R--------R"+
        "R--------R"+
        "R--------R"+
        "R--------R"+
        "-BBBBBBBB-"
    );
    state.setBoard(board);
    Field fieldRed = board.getField(0,5);
    Field fieldEmpty = board.getField(5,5);

    assertThrows(IllegalStateException.class, ()->fieldRed.setPiranha(null));
    fieldRed.setPiranha(PlayerColor.RED);
    assertTrue(fieldRed.getPiranha().isPresent());
    assertEquals(PlayerColor.RED,fieldRed.getPiranha().get());

    assertFalse(fieldEmpty.getPiranha().isPresent());
  }

	@Test
	public void testMoveDirection(){
		assertEquals(2, state.calculateMoveDistance(0,1, RIGHT));
		assertEquals(2, state.calculateMoveDistance(0,8, Direction.UP_RIGHT));
		assertEquals(0, state.calculateMoveDistance(9,9, Direction.UP_RIGHT));
		assertEquals(2, state.calculateMoveDistance(0,1, Direction.DOWN_RIGHT));
		assertEquals(0, state.calculateMoveDistance(0,0, Direction.DOWN_RIGHT));
	}

	@Test
	public void testGreatestSwarm(){

		assertEquals(8,state.greatestSwarmSize(PlayerColor.RED));
		assertEquals(8,state.greatestSwarmSize(PlayerColor.BLUE));

    Board board = TestGameUtil.createCustomBoard(""+
        "-BBBBBBB--"+
        "RB--------"+
        "RO--------"+
        "R-O-------"+
        "RRRRRRRRRR"+
        "R---------"+
        "R---------"+
        "R---------"+
        "----------"+
        "-BBBBBBBB-"
    );
    state.setBoard(board);

		assertEquals(16, state.getOwnFields(PlayerColor.RED).size());
		for (Field field : state.getOwnFields(PlayerColor.RED)){
			assertEquals(RED, field.getState());
		}

    board = TestGameUtil.createCustomBoard(""+
        "BBBBBBBBBB"+
        "-B-B--B---"+
        "RO-B--B---"+
        "R-O---B---"+
        "RRRRRRRRRR"+
        "R---------"+
        "R---------"+
        "R---------"+
        "----------"+
        "----------"
    );
    state.setBoard(board);
		assertEquals(16,state.greatestSwarmSize(PlayerColor.BLUE));

    board = TestGameUtil.createCustomBoard(""+
        "BBBBBBBBBB"+
        "-B-B--B---"+
        "RO-B--B---"+
        "R-O---B---"+
        "R-RRRRRRRR"+
        "R---------"+
        "R---------"+
        "R---------"+
        "----------"+
        "----------"
    );
    state.setBoard(board);
		board.getField(1,5).setState(EMPTY);
		assertEquals(8, state.greatestSwarmSize(PlayerColor.RED));

	}

	@Test
	public void testValidMove() throws InvalidMoveException {
		Board board = TestGameUtil.createCustomBoard(""+
						"-BBBBBBB--"+
						"RB-------R"+
						"RO-------R"+
						"R-O------R"+
						"R--------R"+
						"R--------R"+
						"R--------R"+
						"R--------R"+
						"R--------R"+
						"-BBBBBBBB-"
		);
		state.setBoard(board);

		assertTrue(isValidToMove(0,4,RIGHT, 2, state));
		assertThrows(InvalidMoveException.class, ()->isValidToMove(0,0,RIGHT, 2, state), "Field does not belong to the current player");
		assertThrows(InvalidMoveException.class, ()->isValidToMove(0,4,RIGHT, 3, state), "Move distance was incorrect");
		assertThrows(InvalidMoveException.class, ()->isValidToMove(0,4,LEFT, 2, state), "Move in that direction would not be on the board");
		assertThrows(InvalidMoveException.class, ()->isValidToMove(0,1,RIGHT, 3, state), "Path to the new position is not clear");
		assertTrue(isValidToMove(0,2,RIGHT, 2, state));
		assertThrows(InvalidMoveException.class, ()->isValidToMove(0,3,RIGHT, 2, state), "Field is obstructed");

	}

	@Test
  public void testObstructedFieldGeneration() {
	  int count = 0;
	  List<Field> obstructedFields = new ArrayList<>();
	  for (int x = 0; x < Constants.BOARD_SIZE; x++) {
	    for (int y = 0; y < Constants.BOARD_SIZE; y++) {
	      if (state.getBoard().getField(x,y).isObstructed()) {
	        if (count < Constants.NUM_OBSTACLES) {
	          count++;
          } else {
	          fail("More than two obstructed fields found");
          }
          assertTrue(x > 1);
          assertTrue(y > 1);
          assertTrue(x < Constants.BOARD_SIZE - 2);
          assertTrue(y < Constants.BOARD_SIZE - 2);
          for (Field field: obstructedFields) {
            // check whether second field is in same line (diagonal, vertical or horizontal as first one)
            assertNotEquals(field.getX(), x); // horizontal
            assertNotEquals(field.getY(), y); // vertical
            assertNotEquals(field.getX() - field.getY(), x - y); // downleft to topright diagonal
            assertNotEquals(field.getX() + field.getY(), x + y); // downright to topleft diagonal
          }
          obstructedFields.add(state.getBoard().getField(x,y));
        }
      }
    }
  }
}
