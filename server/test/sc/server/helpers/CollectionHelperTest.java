package sc.server.helpers;

import org.junit.Assert;
import org.junit.Test;
import sc.helpers.CollectionHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectionHelperTest {

  @Test
  public void mapTest() {
    Integer[] ints = new Integer[]{0, 1, 2, 3};
    List<Integer> list = Arrays.asList(ints);

    Iterable<String> converted = CollectionHelper.map(list,
            val -> val > 0 ? "+" + val.toString() : val.toString());

    List<String> convertedList = new ArrayList<>();
    for (String aConverted : converted) {
      convertedList.add(aConverted);
    }

    String[] result = new String[]{"0", "+1", "+2", "+3"};

    Assert.assertArrayEquals(result, convertedList.toArray(new String[0]));
  }

}
