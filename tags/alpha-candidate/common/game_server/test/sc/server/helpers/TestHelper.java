package sc.server.helpers;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;

import sc.helpers.Generator;

public class TestHelper
{
	private static final long		DEFAULT_DURATION	= 1;
	private static final TimeUnit	DEFAULT_TIME_UNIT	= TimeUnit.SECONDS;

	public static <T> boolean waitUntilEqual(T expected, Generator<T> action)
	{
		return waitUntilEqual(expected, action, DEFAULT_DURATION,
				DEFAULT_TIME_UNIT);
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
			Generator<T> action)
	{
		assertEqualsWithTimeout(expected, action, DEFAULT_DURATION,
				DEFAULT_TIME_UNIT);
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
