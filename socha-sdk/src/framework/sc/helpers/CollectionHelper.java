package sc.helpers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;

public abstract class CollectionHelper {
  public static <FROM, TO> Iterable<TO> map(final Iterable<FROM> source, final Function<FROM, TO> func) {
    return () -> {
      final Iterator<FROM> sourceIterator = source.iterator();
      return new Iterator<TO>() {
        
        @Override
        public boolean hasNext() {
          return sourceIterator.hasNext();
        }
        
        @Override
        public TO next() {
          return func.apply(sourceIterator.next());
        }
        
        @Override
        public void remove() {
          sourceIterator.remove();
        }
        
      };
    };
  }
  
  public static <T> Collection<T> iterableToCollection(Iterable<T> values) {
    Collection<T> result = new ArrayList<>();
    for (T value : values)
      result.add(value);
    return result;
  }
  
  public static Iterable<BigDecimal> intArrayToBigDecimalArray(Integer[] integers) {
    return map(Arrays.asList(integers), BigDecimal::valueOf);
  }
  
}
