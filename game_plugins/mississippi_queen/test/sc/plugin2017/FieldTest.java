package sc.plugin2017;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class FieldTest {

  
  @Test
  public void testCompareFields() {
    Field fOne = new Field(FieldType.WATER, -2, -2);
    Field fTwo = new Field(FieldType.BLOCKED, 0, 0);
    Field fThree = new Field(FieldType.SANDBANK, -1, 0);
    Field fFour = new Field(FieldType.LOG, 3, 0);
    assertEquals(-1, fOne.compareTo(fTwo));
    assertEquals(1, fTwo.compareTo(fOne));
    assertEquals(1, fTwo.compareTo(fThree));
    assertEquals(-1, fTwo.compareTo(fFour));
    List<Field> list = new ArrayList<>(Arrays.asList(fThree, fOne, fFour, fTwo));
    Collections.sort(list);
    assertEquals(Arrays.asList(fOne, fThree, fTwo, fFour), list);
  }

}
