package sc.plugin2014.entities;

import static org.junit.Assert.*;
import org.junit.Test;

public class StoneTest {

    @Test
    public void testStone() {
        Stone stone = new Stone();
        assertEquals(StoneColor.BLUE, stone.getColor());
        assertEquals(StoneShape.ACORN, stone.getShape());

        Stone stoneGreenFlower = new Stone(StoneColor.GREEN, StoneShape.BELL);
        assertEquals(StoneColor.GREEN, stoneGreenFlower.getColor());
        assertEquals(StoneShape.BELL, stoneGreenFlower.getShape());
    }

}
