package sc.plugin2015.entities;

import static org.junit.Assert.*;

import org.junit.Test;

import sc.plugin2015.Player;
import sc.plugin2015.PlayerColor;

public class PlayerTest {

    @Test
    public void testPlayer() {
        Player bluePlayer = new Player(PlayerColor.BLUE);
        assertEquals(PlayerColor.BLUE, bluePlayer.getPlayerColor());
        assertEquals(0, bluePlayer.getPoints());
        assertEquals(0, bluePlayer.getStones().size());
    }

    @Test
    public void testEquals() {
        Player redPlayer = new Player(PlayerColor.RED);
        Player redPlayer2 = new Player(PlayerColor.RED);
        Player bluePlayer = new Player(PlayerColor.BLUE);

        assertTrue(redPlayer.equals(redPlayer));
        assertTrue(redPlayer.equals(redPlayer2));
        assertTrue(bluePlayer.equals(bluePlayer));

        assertFalse(redPlayer.equals(bluePlayer));
        assertFalse(bluePlayer.equals(redPlayer));
    }

    @Test
    public void testAddStone() {
        Player redPlayer = new Player(PlayerColor.RED);
        Stone stone = new Stone();
        Stone stone2 = new Stone();

        redPlayer.addStone(stone);
        assertEquals(1, redPlayer.getStones().size());
        assertEquals(stone, redPlayer.getStones().get(0));

        redPlayer.addStone(stone2, 0);
        assertEquals(1, redPlayer.getStones().size());
        assertEquals(stone2, redPlayer.getStones().get(0));
    }

    @Test
    public void testRemoveStone() {
        Player redPlayer = new Player(PlayerColor.RED);
        Stone stone = new Stone();
        Stone stone2 = new Stone();

        redPlayer.addStone(stone);

        redPlayer.addStone(stone2);
        assertEquals(2, redPlayer.getStones().size());
        assertEquals(stone2, redPlayer.getStones().get(1));

        redPlayer.removeStone(stone);
        assertEquals(2, redPlayer.getStones().size());
        assertNull(redPlayer.getStones().get(0));
        assertEquals(stone2, redPlayer.getStones().get(1));

        redPlayer.removeStone(stone2);
        assertNull(redPlayer.getStones().get(0));
        assertNull(redPlayer.getStones().get(1));
    }

    @Test
    public void testHasStone() {
        Player redPlayer = new Player(PlayerColor.RED);
        Stone stone = new Stone();
        Stone stone2 = new Stone(StoneColor.MAGENTA, StoneShape.BELL);

        assertFalse(redPlayer.hasStone(stone));
        assertFalse(redPlayer.hasStone(stone2));

        redPlayer.addStone(stone);

        assertTrue(redPlayer.hasStone(stone));
        assertFalse(redPlayer.hasStone(stone2));

        redPlayer.addStone(stone2);

        assertTrue(redPlayer.hasStone(stone));
        assertTrue(redPlayer.hasStone(stone2));

        redPlayer.removeStone(stone);

        assertFalse(redPlayer.hasStone(stone));
        assertTrue(redPlayer.hasStone(stone2));

        redPlayer.removeStone(stone2);

        assertFalse(redPlayer.hasStone(stone));
        assertFalse(redPlayer.hasStone(stone2));
    }

    @Test
    public void testAddPoints() {
        Player redPlayer = new Player(PlayerColor.RED);
        assertEquals(0, redPlayer.getPoints());

        redPlayer.addPoints(3);
        assertEquals(3, redPlayer.getPoints());

        redPlayer.addPoints(1);
        assertEquals(4, redPlayer.getPoints());
    }

    @Test
    public void testGetStonePosition() {
        Player redPlayer = new Player(PlayerColor.RED);
        Stone stone = new Stone();

        redPlayer.addStone(stone);
        assertEquals(0, redPlayer.getStonePosition(stone));
    }

}
