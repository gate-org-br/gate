package gate.lang.constructionStrategy;

import gate.lang.property.Attribute;
import gate.adapter.mapCollector.MapCollector;

import java.util.Map;

record MapStrategy() implements ConstructionStrategy
{
	@Override
	public Object construct(Class<?> type, Map<Attribute, Object> attributes)
	{
		var value = new java.util.LinkedHashMap<Object, Object>();
		for (var attribute : attributes.entrySet())
			attribute.getKey().setValue(value, attribute.getValue());
		return MapCollector.fromMap(type, value);
	}
}
