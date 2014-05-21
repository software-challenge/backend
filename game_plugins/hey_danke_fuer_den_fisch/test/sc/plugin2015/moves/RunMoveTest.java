package sc.plugin2015.moves;

import org.junit.Test;

import sc.plugin2015.Board;
import sc.plugin2015.Penguin;
import sc.plugin2015.PlayerColor;

public class RunMoveTest {

	@Test(expected = NullPointerException.class)
	public void testRunMoveNoPenguin() {
		Board board = new Board(false);
		board.getField(0, 0).fish = 2;
		board.movePenguin(0, 0, 0, 1, PlayerColor.RED);
	}
	
	@Test
	public void testRunMove() {
		Board board = new Board(false);
		board.getField(0, 0).fish = 1;
		Penguin penguin = new Penguin(PlayerColor.RED);
		board.getField(0, 1).fish = 3;
		board.putPenguin(0, 0, penguin);
		board.movePenguin(0, 0, 0, 1, PlayerColor.RED);
	}
}
