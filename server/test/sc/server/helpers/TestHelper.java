package sc.server.helpers;

import org.junit.Assert;
import sc.helpers.Generator;

import java.util.concurrent.TimeUnit;

public class TestHelper {
  private static final long DEFAULT_DURATION = 100;
  private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS;

  public static <T> boolean waitUntilEqual(T expected, Generator<T> action) {
    return waitUntilEqual(expected, action, DEFAULT_DURATION,
            DEFAULT_TIME_UNIT);
  }

  public static <T> boolean waitUntilEqual(T expected, Generator<T> action,
                                           long maxDuration, TimeUnit unit) {
    long millis = unit.toMillis(maxDuration);
    long timeout = System.currentTimeMillis() + millis;

    while (System.currentTimeMillis() <= timeout) {
      if (isEqual(expected, action.operate())) {
        return true;
      }
      Thread.yield();
    }

    Assert.assertTrue("Did not receive " + expected + " within " + millis + "ms", isEqual(expected, action.operate()));
    return isEqual(expected, action.operate());
  }

  public static <T> boolean waitUntilEqual(T expected, Generator<T> action,
                                           long maxDuration) {
    return waitUntilEqual(expected, action, maxDuration, TimeUnit.MILLISECONDS);
  }

  public static <T> boolean waitUntilTrue(Generator<Boolean> action,
                                          long maxDuration, TimeUnit unit) {
    return waitUntilEqual(true, action, maxDuration, unit);
  }

  public static <T> boolean waitUntilTrue(Generator<Boolean> action, long maxDuration) {
    return waitUntilEqual(true, action, maxDuration, TimeUnit.MILLISECONDS);
  }

  public static <T> boolean waitUntilFalse(Generator<Boolean> action,
                                           long maxDuration, TimeUnit unit) {
    return waitUntilEqual(false, action, maxDuration, unit);
  }

  public static <T> boolean waitUntilFalse(Generator<Boolean> action, long maxDuration) {
    return waitUntilEqual(false, action, maxDuration, TimeUnit.MILLISECONDS);
  }

  public static <T> void assertEqualsWithTimeout(T expected,
                                                 Generator<T> action) {
    assertEqualsWithTimeout(expected, action, DEFAULT_DURATION,
            DEFAULT_TIME_UNIT);
  }


  public static <T> void assertEqualsWithTimeout(T expected,
                                                 Generator<T> action,
                                                 long maxMills) {
    assertEqualsWithTimeout(expected, action, maxMills,
            DEFAULT_TIME_UNIT);
  }

  public static <T> void assertEqualsWithTimeout(T expected,
                                                 Generator<T> action, long maxDuration, TimeUnit unit) {
    waitUntilEqual(expected, action, maxDuration, unit);
    Assert.assertEquals(expected, action.operate());
  }

  public static boolean isEqual(Object o1, Object o2) {
    if (o1 == null) {
      return o2 == null;
    } else {
      return o1.equals(o2);
    }
  }

  public static void waitMills(long mills) {
    try {
      Thread.sleep(mills);
    } catch (Exception e) {

    }
  }

  public static void waitForObject(Object o) {
    try {
      synchronized (o) {
        o.wait();
      }
    } catch (Exception e) {
    }
  }

  public static void waitForObject(Object o, long mills) {
    try {
      o.wait(mills);
    } catch (Exception e) {
    }
  }

}
