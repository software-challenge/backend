package sc.server.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import sc.helpers.CollectionHelper;
import sc.helpers.Function;

public class CollectionHelperTest
{
	@Test
	public void MapTest()
	{
		Integer[] ints = new Integer[] { 0, 1, 2, 3 };
		List<Integer> list = Arrays.asList(ints);

		Iterable<String> converted = CollectionHelper.map(list,
				new Function<Integer, String>() {

					@Override
					public String operate(Integer val)
					{
						if (val > 0)
						{
							return "+" + val.toString();
						}
						else
						{
							return val.toString();
						}
					}

				});

		List<String> convertedList = new ArrayList<String>();
		Iterator<String> iterator = converted.iterator();
		while (iterator.hasNext())
		{
			convertedList.add(iterator.next());
		}

		String[] result = new String[] { "0", "+1", "+2", "+3" };

		Assert.assertArrayEquals(result, convertedList.toArray(new String[0]));
	}
}
