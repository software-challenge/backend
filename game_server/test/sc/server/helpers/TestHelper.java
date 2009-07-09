package sc.server.helpers;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;

import sc.helpers.Generator;

public class TestHelper
{
	public static <T> boolean waitUntilEqual(T expected, Generator<T> action)
	{
		return waitUntilEqual(expected, action, 1, TimeUnit.SECONDS);
	}

	public static <T> boolean waitUntilEqual(T expected, Generator<T> action,
			long maxDuration, TimeUnit unit)
	{
		long timeout = System.currentTimeMillis() + unit.toMillis(maxDuration);

		while (System.currentTimeMillis() <= timeout)
		{
			if (isEqual(expected, action.operate()))
			{
				return true;
			}

			Thread.yield();
		}

		return isEqual(expected, action.operate());
	}

	public static <T> void assertEqualsWithTimeout(T expected,
			Generator<T> action, long maxDuration, TimeUnit unit)
	{
		waitUntilEqual(expected, action, maxDuration, unit);
		Assert.assertEquals(expected, action.operate());
	}

	public static boolean isEqual(Object o1, Object o2)
	{
		if (o1 == null)
		{
			return o2 == null;
		}
		else
		{
			return o1.equals(o2);
		}
	}
}
