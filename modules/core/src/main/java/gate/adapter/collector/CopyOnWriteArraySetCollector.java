package gate.adapter.collector;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArraySet;

public class CopyOnWriteArraySetCollector implements Collector
{
	@Override
	public Object ofArray(Type type, Object[] array)
	{
		var collection = new CopyOnWriteArraySet<>();
		if (array != null)
			Collections.addAll(collection, array);
		return collection;
	}
}
