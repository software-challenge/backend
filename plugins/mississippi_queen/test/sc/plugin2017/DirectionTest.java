package sc.plugin2017;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DirectionTest {

  @Test
  public void testToString() {
    assertEquals("oben links", Direction.UP_LEFT.toString());
  }

}
