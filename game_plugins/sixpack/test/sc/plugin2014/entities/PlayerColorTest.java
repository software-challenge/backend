package sc.plugin2014.entities;

import static org.junit.Assert.*;
import org.junit.Test;

public class PlayerColorTest {

    @Test
    public void testGetOpponent() {
        PlayerColor red = PlayerColor.RED;
        assertEquals(PlayerColor.BLUE, red.getOpponent());

        PlayerColor blue = PlayerColor.BLUE;
        assertEquals(PlayerColor.RED, blue.getOpponent());
    }

}
