package sc.helpers;

import java.util.Iterator;

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
}
