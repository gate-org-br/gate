package gate.adapter.collector;

import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Collections;

public class DequeCollector implements Collector
{
	@Override
	public Object ofArray(Type type, Object[] array)
	{
		var collection = new ArrayDeque<>();
		if (array != null)
			Collections.addAll(collection, array);
		return collection;
	}
}
