package sc.plugin2014.entities;

import static org.junit.Assert.*;
import org.junit.Test;

public class StoneShapeTest {
    @Test
    public void testGetShapeFromIndex() {
        assertEquals(StoneShape.ACORN, StoneShape.getShapeFromIndex(0));
        assertEquals(StoneShape.BELL, StoneShape.getShapeFromIndex(1));
        assertEquals(StoneShape.CLUBS, StoneShape.getShapeFromIndex(2));
        assertEquals(StoneShape.DIAMONT, StoneShape.getShapeFromIndex(3));
        assertEquals(StoneShape.HEART, StoneShape.getShapeFromIndex(4));
        assertEquals(StoneShape.SPADES, StoneShape.getShapeFromIndex(5));
    }
}
