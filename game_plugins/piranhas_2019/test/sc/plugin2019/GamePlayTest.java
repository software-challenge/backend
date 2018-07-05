package sc.plugin2019;

import org.junit.Before;
import org.junit.Test;
import sc.plugin2019.util.Constants;
import sc.plugin2019.util.TestGameUtil;
import sc.shared.InvalidMoveException;
import sc.shared.PlayerColor;

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

		Board board = state.getBoard();
		for(int i = 0; i < Constants.BOARD_SIZE; i++){
			board.getField(i,5).setPiranha(PlayerColor.RED);
			board.getField(9,i).setState(EMPTY);
		}


		assertEquals(16, state.getOwnFields(PlayerColor.RED).size());
		for (Field field : state.getOwnFields(PlayerColor.RED)){
			assertEquals(RED, field.getState());
		}
		assertEquals(16,state.greatestSwarmSize(PlayerColor.RED));

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
}
