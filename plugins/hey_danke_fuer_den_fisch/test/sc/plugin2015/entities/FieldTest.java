package sc.plugin2015.entities;

import static org.junit.Assert.*;

import org.junit.Test;

import sc.plugin2015.Field;
import sc.plugin2015.Penguin;
import sc.plugin2015.PlayerColor;

public class FieldTest {

    @Test
    public void testField() {
        Field field = new Field(1, new Penguin(PlayerColor.BLUE));
        assertEquals(1, field.getFish());
        assertEquals(PlayerColor.BLUE, field.getPenguin().getOwner());
    }

    @Test
    public void testSetPenguin() {
        Field field = new Field(1);
        Penguin penguin = new Penguin();
        assertNull(field.getPenguin());

        field.putPenguin(penguin);
        assertEquals(penguin, field.getPenguin());
    }
}
