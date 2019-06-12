package sc.plugin2020.util;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

/**
 * @author rra
 * @since Jul 4, 2009
 */
public class TestJUnitUtil {

  public interface Throwable {
    public void execute() throws Exception;
  }

  public static void assertThrows(Class<? extends Exception> clazz, Throwable run) {
    try {
      run.execute();
      fail();
    } catch (Exception e) {
      System.out.println(e.getMessage());
      assertTrue(clazz.isInstance(e));
    }
  }

  public static void assertThrows(Class<? extends Exception> clazz, Throwable run, String message) {
    try {
      run.execute();
      fail();
    } catch (Exception e) {
      assertTrue(clazz.isInstance(e) && e.getMessage().equals(message));
    }
  }

}
