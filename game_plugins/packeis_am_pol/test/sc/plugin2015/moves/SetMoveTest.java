package sc.plugin2015.moves;

import org.junit.Test;

import sc.plugin2015.Board;
import sc.plugin2015.Penguin;
import sc.plugin2015.PlayerColor;
import sc.plugin2015.util.Constants;

public class SetMoveTest {

	@Test(expected = IllegalArgumentException.class)
	public void testSetMoveManyFish() {
		Board board = new Board(false);
		board.getField(0, 0).fish = 2;
		Penguin penguin = new Penguin(PlayerColor.RED);
		board.putPenguin(0, 0, penguin);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetMoveEmptyField() {

		Board board = new Board(false);
		board.getField(0, 0).fish = 0;
		Penguin penguin = new Penguin(PlayerColor.RED);
		board.putPenguin(0, 0, penguin);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetMoveOutOfBounds() {

		Board board = new Board(false);
		Penguin penguin = new Penguin(PlayerColor.RED);
		board.putPenguin(Constants.ROWS, 0, penguin);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetMoveAlreadyTaken() {

		Board board = new Board(false);
		board.getField(0, 0).fish = 1;
		Penguin penguin = new Penguin(PlayerColor.RED);
		Penguin otherPenguin = new Penguin(PlayerColor.BLUE);
		board.putPenguin(0, 0, penguin);
		board.putPenguin(0, 0, otherPenguin);
	}

}
