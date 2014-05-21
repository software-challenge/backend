package sc.plugin2015.moves;

import static org.junit.Assert.*;

import org.junit.Test;

import sc.plugin2015.RunMove;

public class MoveTest {

    @Test
    public void testAddHint() {
        RunMove move = new RunMove();
        move.addHint("test");
        assertEquals(1, move.getHints().size());
        assertEquals("test", move.getHints().get(0).getContent());
    }
}
