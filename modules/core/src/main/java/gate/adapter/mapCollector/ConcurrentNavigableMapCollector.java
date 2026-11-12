package gate.adapter.mapCollector;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class ConcurrentNavigableMapCollector implements MapCollector
{
	@Override
	public Map<?, ?> ofMap(Type type, Map<?, ?> values)
	{
		var map = new ConcurrentSkipListMap<>();
		if (values != null)
			map.putAll(values);
		return map;
	}
}
