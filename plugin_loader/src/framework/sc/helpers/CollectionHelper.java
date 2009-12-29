package sc.helpers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public abstract class CollectionHelper
{
	public static <FROM, TO> Iterable<TO> map(final Iterable<FROM> source,
			final Function<FROM, TO> func)
	{
		return new Iterable<TO>() {
			@Override
			public Iterator<TO> iterator()
			{
				final Iterator<FROM> sourceIterator = source.iterator();
				return new Iterator<TO>() {

					@Override
					public boolean hasNext()
					{
						return sourceIterator.hasNext();
					}

					@Override
					public TO next()
					{
						return func.operate((sourceIterator.next()));
					}

					@Override
					public void remove()
					{
						sourceIterator.remove();
					}

				};
			}
		};
	}

	public static <T> Collection<T> iterableToColleciton(Iterable<T> values)
	{
		Collection<T> result = new LinkedList<T>();
		for(T value : values)
		{
			result.add(value);
		}
		return result;
	}
	
	public static Iterable<BigDecimal> intArrayToBigDecimalArray(Integer[] integers)
	{
		return CollectionHelper.map(Arrays.asList(integers),
				new Function<Integer, BigDecimal>() {
					@Override
					public BigDecimal operate(Integer val)
					{
						return BigDecimal.valueOf(val);
					}
				});
	}
}
