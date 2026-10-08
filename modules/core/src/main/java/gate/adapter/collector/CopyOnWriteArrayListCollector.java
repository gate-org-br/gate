package gate.adapter.collector;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

public class CopyOnWriteArrayListCollector implements Collector
{
	@Override
	public Object ofArray(Type type, Object[] array)
	{
		var collection = new CopyOnWriteArrayList<>();
		if (array != null)
			Collections.addAll(collection, array);
		return collection;
	}
}
