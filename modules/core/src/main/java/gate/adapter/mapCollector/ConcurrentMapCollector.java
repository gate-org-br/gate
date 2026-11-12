package gate.adapter.mapCollector;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentMapCollector implements MapCollector
{
	@Override
	public Map<?, ?> ofMap(Type type, Map<?, ?> values)
	{
		var map = new ConcurrentHashMap<>();
		if (values != null)
			map.putAll(values);
		return map;
	}
}
