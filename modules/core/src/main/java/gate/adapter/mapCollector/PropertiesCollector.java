package gate.adapter.mapCollector;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Properties;

public class PropertiesCollector implements MapCollector
{
	@Override
	public Map<?, ?> ofMap(Type type, Map<?, ?> values)
	{
		var map = new Properties();
		if (values != null)
			map.putAll(values);
		return map;
	}
}
