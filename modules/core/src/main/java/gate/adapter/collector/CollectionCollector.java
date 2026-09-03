package gate.adapter.collector;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class CollectionCollector implements Collector
{
	@Override
	public Object ofArray(Type type, Object[] array)
	{
		var collection = new ArrayList<>();
		if (array != null)
			Collections.addAll(collection, array);
		return collection;
	}
}
