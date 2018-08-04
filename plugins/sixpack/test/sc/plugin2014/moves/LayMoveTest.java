package sc.plugin2014.moves;

import static org.junit.Assert.*;
import org.junit.Test;

import sc.plugin2014.entities.Field;
import sc.plugin2014.entities.Stone;

public class LayMoveTest {

    @Test
    public void testLayMove() {
        LayMove layMove = new LayMove();
        assertEquals(0, layMove.getStoneToFieldMapping().size());
    }

    @Test
    public void testLayStoneOntoField() {

    }

    @Test
    public void testClearStoneToFieldMapping() {

    }

    @Test
    public void testPerform() {

    }

    @Test
    public void testClone() {
    	LayMove lm = new LayMove();
    	lm.layStoneOntoField(new Stone(), new Field(0,0));
    	LayMove clone = new LayMove();
    	LayMove clone2 = new LayMove();
		try {
			clone = (LayMove) lm.clone();
			assertTrue(lm.equals(clone));
			assertFalse(lm.equals(clone2));
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
    }
}
