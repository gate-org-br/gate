package gate.adapter.collector;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedList;

public class LinkedListCollector implements Collector
{
	@Override
	public Object ofArray(Type type, Object[] array)
	{
		var collection = new LinkedList<>();
		if (array != null)
			Collections.addAll(collection, array);
		return collection;
	}
}
