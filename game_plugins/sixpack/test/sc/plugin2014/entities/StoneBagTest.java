package sc.plugin2014.entities;

import static org.junit.Assert.*;
import org.junit.Test;
import sc.plugin2014.util.Constants;

public class StoneBagTest {

    @Test
    public void testStoneBag() {
        StoneBag stoneBag = new StoneBag();
        assertEquals(
                (Constants.STONES_COLOR_COUNT * Constants.STONES_SHAPE_COUNT * Constants.STONES_SAME_KIND_COUNT),
                stoneBag.getStoneCountInBag());
    }

    @Test
    public void testDrawStone() {
        StoneBag stoneBag = new StoneBag();
        int sizeBefore = stoneBag.getStoneCountInBag();
        assertNotNull(stoneBag.drawStone());
        assertEquals(sizeBefore - 1, stoneBag.getStoneCountInBag());
    }

    public void testDrawStoneOverEnd() {
        StoneBag stoneBag = new StoneBag();
        assertNotNull(stoneBag.drawStone());

        int stoneCountInBag = stoneBag.getStoneCountInBag();

        for (int i = 0; i < stoneCountInBag; i++) {
            assertNotNull(stoneBag.drawStone());
        }

        assertNull(stoneBag.drawStone());
    }

    @Test
    public void testPutBackStone() {
        StoneBag stoneBag = new StoneBag();
        int sizeBefore = stoneBag.getStoneCountInBag();

        Stone drawnStone = stoneBag.drawStone();
        assertNotNull(drawnStone);
        assertEquals(sizeBefore - 1, stoneBag.getStoneCountInBag());

        stoneBag.putBackStone(drawnStone);
        assertEquals(sizeBefore, stoneBag.getStoneCountInBag());
    }

    @Test
    public void testClone() {
        // TODO
    }

}
