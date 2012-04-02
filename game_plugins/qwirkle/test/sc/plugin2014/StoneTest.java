package sc.plugin2014;

import static org.junit.Assert.*;
import org.junit.Test;

public class StoneTest {

    @Test
    public void testStone() {
        Stone stone = new Stone();
        assertEquals(StoneColor.BLUE, stone.getColor());
        assertEquals(StoneShape.CIRCLE, stone.getShape());

        Stone stoneGreenFlower = new Stone(StoneColor.GREEN, StoneShape.FLOWER);
        assertEquals(StoneColor.GREEN, stoneGreenFlower.getColor());
        assertEquals(StoneShape.FLOWER, stoneGreenFlower.getShape());
    }

}
