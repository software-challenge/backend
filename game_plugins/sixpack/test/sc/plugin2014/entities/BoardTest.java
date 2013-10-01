package sc.plugin2014.entities;

import static org.junit.Assert.*;
import org.junit.Test;

public class BoardTest {

    @Test(expected = IllegalArgumentException.class)
    public void testLayStoneNullStone() {
        Board board = new Board();
        board.layStone(null, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLayStoneWrongPosX() {
        Board board = new Board();
        Stone stone = new Stone();
        board.layStone(stone, -1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLayStoneWrongPosY() {
        Board board = new Board();
        Stone stone = new Stone();
        board.layStone(stone, 0, -1);
    }

    @Test
    public void testLayStone() {
        Board board = new Board();
        Stone stone = new Stone();
        assertNull(board.getField(0, 0).getStone());

        board.layStone(stone, 0, 0);
        assertEquals(stone, board.getField(0, 0).getStone());
    }

    @Test
    public void testClone() {
    	Board board = new Board();
    	Stone stone = new Stone();
    	board.layStone(stone, 0, 0);
    	Board clone;
		try {
			clone = (Board) board.clone();
			assertTrue(board.equals(clone));
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}    	
    }

}
