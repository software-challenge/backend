package sc.plugin2015.moves;

import static org.junit.Assert.*;
import org.junit.Test;

public class MoveTest {

    @Test
    public void testAddHint() {
        ExchangeMove move = new ExchangeMove();
        move.addHint("test");
        assertEquals(1, move.getHints().size());
        assertEquals("test", move.getHints().get(0).getContent());
    }
}
