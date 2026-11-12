package gate.adapter.mapCollector;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.HashMap;

public class HashMapCollector implements MapCollector
{
	@Override
	public Map<?, ?> ofMap(Type type, Map<?, ?> values)
	{
		var map = new HashMap<>();
		if (values != null)
			map.putAll(values);
		return map;
	}
}
