package sc.plugin2014.entities;

import static org.junit.Assert.*;
import org.junit.Test;

public class FieldTest {

    @Test
    public void testField() {
        Field field = new Field(1, 2);
        assertEquals(1, field.getPosX());
        assertEquals(2, field.getPosY());
    }

    @Test
    public void testSetStone() {
        Field field = new Field(1, 2);
        Stone stone = new Stone();
        assertNull(field.getStone());

        field.setStone(stone);
        assertEquals(stone, field.getStone());
    }
}
