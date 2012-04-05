package sc.plugin2014.entities;

import static org.junit.Assert.*;
import org.junit.Test;

public class StoneColorTest {

    @Test
    public void testGetColorFromIndex() {
        assertEquals(StoneColor.BLUE, StoneColor.getColorFromIndex(0));
        assertEquals(StoneColor.GREEN, StoneColor.getColorFromIndex(1));
        assertEquals(StoneColor.ORANGE, StoneColor.getColorFromIndex(2));
        assertEquals(StoneColor.PURPLE, StoneColor.getColorFromIndex(3));
        assertEquals(StoneColor.RED, StoneColor.getColorFromIndex(4));
        assertEquals(StoneColor.YELLOW, StoneColor.getColorFromIndex(5));
    }

}
