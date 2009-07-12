package sc.helpers;

import java.util.Iterator;

public abstract class StringHelper
{
	public static <T> String join(final Iterable<T> objs, final String delimiter)
	{
		Iterator<T> iter = objs.iterator();

		if (!iter.hasNext())
			return "";

		StringBuffer buffer = new StringBuffer(String.valueOf(iter.next()));

		while (iter.hasNext())
			buffer.append(delimiter).append(String.valueOf(iter.next()));

		return buffer.toString();
	}

	public static String pad(String name, int length)
	{
		StringBuilder builder = new StringBuilder(name);
		for (int i = 0; i < length - name.length(); i++)
		{
			builder.append(" ");
		}
		return builder.toString();
	}
}
