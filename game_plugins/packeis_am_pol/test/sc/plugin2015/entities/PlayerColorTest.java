package sc.plugin2015.entities;

import static org.junit.Assert.*;

import org.junit.Test;

import sc.plugin2015.PlayerColor;

public class PlayerColorTest {

    @Test
    public void testGetOpponent() {
        PlayerColor red = PlayerColor.RED;
        assertEquals(PlayerColor.BLUE, red.opponent());

        PlayerColor blue = PlayerColor.BLUE;
        assertEquals(PlayerColor.RED, blue.opponent());
    }

}
