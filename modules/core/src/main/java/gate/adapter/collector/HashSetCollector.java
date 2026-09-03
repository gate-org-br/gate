package gate.adapter.collector;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;

public class HashSetCollector implements Collector
{
	@Override
	public Object ofArray(Type type, Object[] array)
	{
		var collection = new HashSet<>();
		if (array != null)
			Collections.addAll(collection, array);
		return collection;
	}
}
