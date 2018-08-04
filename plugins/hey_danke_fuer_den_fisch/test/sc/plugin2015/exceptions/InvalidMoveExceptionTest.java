package sc.plugin2015.exceptions;

import static org.junit.Assert.*;
import org.junit.Test;
import sc.plugin2015.util.InvalidMoveException;

public class InvalidMoveExceptionTest {

    @Test
    public void testInvalidMoveException() {
        InvalidMoveException invalidMoveException = new InvalidMoveException(
                "test");
        assertEquals("test", invalidMoveException.getMessage());
    }
}
