package sc.plugin2015.entities;

import static org.junit.Assert.*;

import org.junit.Test;

import sc.plugin2015.Board;
import sc.plugin2015.Penguin;
import sc.plugin2015.PlayerColor;

public class BoardTest {
	

	@Test(expected = IllegalArgumentException.class)
	public void testPutPenguinOutOfBoundsLowY() {
		Board board = new Board();
		board.putPenguin(0, -1, new Penguin(PlayerColor.RED));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPutPenguinOutOfBoundsHighY() {
		Board board = new Board();
		board.putPenguin(0, 8, new Penguin(PlayerColor.RED));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPutPenguinOutOfBoundsLowX() {
		Board board = new Board();
		board.putPenguin(-1, 0, new Penguin(PlayerColor.RED));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPutPenguinOutOfBoundsHighX() {
		Board board = new Board();
		board.putPenguin(8, 0, new Penguin(PlayerColor.RED));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPutPenguinOnEmptyField() {
		Board board = new Board(false);
		board.putPenguin(1, 1, new Penguin(PlayerColor.RED));
	}

	@Test
	public void testPutPenguin() {
		Board board = new Board();
		board.getField(0, 0).fish = 1;
		Penguin penguin = new Penguin(PlayerColor.RED);
		assertNull(board.getField(0, 0).getPenguin());

		board.putPenguin(0, 0, penguin);
		assertEquals(penguin, board.getField(0, 0).getPenguin());
	}

	@Test
	public void testClone() {
		Board board = new Board();
		board.getField(0, 0).fish = 1;
		Penguin penguin = new Penguin();
		board.putPenguin(0, 0, penguin);
		Board clone;
		try {
			clone = (Board) board.clone();
			assertTrue(board.equals(clone));
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
	}

}
