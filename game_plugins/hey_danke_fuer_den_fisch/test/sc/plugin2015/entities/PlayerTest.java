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
        assertEquals(0, bluePlayer.getFields());
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
    public void testAddPoints() {
        Player redPlayer = new Player(PlayerColor.RED);
        assertEquals(0, redPlayer.getPoints());

        redPlayer.addPoints(3);
        assertEquals(3, redPlayer.getPoints());

        redPlayer.addPoints(1);
        assertEquals(4, redPlayer.getPoints());
    }
}
