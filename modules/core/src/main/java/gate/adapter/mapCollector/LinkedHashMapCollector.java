package gate.adapter.mapCollector;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.LinkedHashMap;

public class LinkedHashMapCollector implements MapCollector
{
	@Override
	public Map<?, ?> ofMap(Type type, Map<?, ?> values)
	{
		var map = new LinkedHashMap<>();
		if (values != null)
			map.putAll(values);
		return map;
	}
}
