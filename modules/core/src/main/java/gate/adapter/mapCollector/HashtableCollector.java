package gate.adapter.mapCollector;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Hashtable;

public class HashtableCollector implements MapCollector
{
	@Override
	public Map<?, ?> ofMap(Type type, Map<?, ?> values)
	{
		var map = new Hashtable<>();
		if (values != null)
			map.putAll(values);
		return map;
	}
}
