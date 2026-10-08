package gate.adapter.collector;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.TreeSet;

public class SortedSetCollector implements Collector
{
	@Override
	public Object ofArray(Type type, Object[] array)
	{
		var collection = new TreeSet<>();
		if (array != null)
			Collections.addAll(collection, array);
		return collection;
	}
}
