package sc.plugin2014.exceptions;

import static org.junit.Assert.*;
import org.junit.Test;

public class InvalidMoveExceptionTest {

    @Test
    public void testInvalidMoveException() {
        InvalidMoveException invalidMoveException = new InvalidMoveException(
                "test");
        assertEquals("test", invalidMoveException.getMessage());
    }
}
