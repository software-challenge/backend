package sc.plugin2014.entities;

import static org.junit.Assert.*;
import org.junit.Test;

public class StoneShapeTest {
    @Test
    public void testGetShapeFromIndex() {
        assertEquals(StoneShape.CIRCLE, StoneShape.getShapeFromIndex(0));
        assertEquals(StoneShape.FLOWER, StoneShape.getShapeFromIndex(1));
        assertEquals(StoneShape.FOUR_SPIKE, StoneShape.getShapeFromIndex(2));
        assertEquals(StoneShape.RHOMBUS, StoneShape.getShapeFromIndex(3));
        assertEquals(StoneShape.SQUARE, StoneShape.getShapeFromIndex(4));
        assertEquals(StoneShape.STAR, StoneShape.getShapeFromIndex(5));
    }
}
