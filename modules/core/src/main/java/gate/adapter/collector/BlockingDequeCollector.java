package gate.adapter.collector;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingDeque;

public class BlockingDequeCollector implements Collector
{
	@Override
	public Object ofArray(Type type, Object[] array)
	{
		var collection = new LinkedBlockingDeque<>();
		if (array != null)
			Collections.addAll(collection, array);
		return collection;
	}
}
