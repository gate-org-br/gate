package gate.adapter.collector;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashSet;

public class SetCollector implements Collector
{
	@Override
	public Object ofArray(Type type, Object[] array)
	{
		var collection = new LinkedHashSet<>();
		if (array != null)
			Collections.addAll(collection, array);
		return collection;
	}
}
