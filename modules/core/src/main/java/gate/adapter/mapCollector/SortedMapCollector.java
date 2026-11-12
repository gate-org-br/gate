package gate.adapter.mapCollector;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.TreeMap;

public class SortedMapCollector implements MapCollector
{
	@Override
	public Map<?, ?> ofMap(Type type, Map<?, ?> values)
	{
		var map = new TreeMap<>();
		if (values != null)
			map.putAll(values);
		return map;
	}
}
