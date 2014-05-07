package sc.plugin2015.entities;

import static org.junit.Assert.*;
import org.junit.Test;

public class StoneColorTest {

    @Test
    public void testGetColorFromIndex() {
        assertEquals(StoneColor.BLUE, StoneColor.getColorFromIndex(0));
        assertEquals(StoneColor.GREEN, StoneColor.getColorFromIndex(1));
        assertEquals(StoneColor.MAGENTA, StoneColor.getColorFromIndex(2));
        assertEquals(StoneColor.ORANGE, StoneColor.getColorFromIndex(3));
        assertEquals(StoneColor.VIOLET, StoneColor.getColorFromIndex(4));
        assertEquals(StoneColor.YELLOW, StoneColor.getColorFromIndex(5));
    }

}
