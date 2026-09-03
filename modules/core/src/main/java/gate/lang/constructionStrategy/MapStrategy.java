package gate.lang.constructionStrategy;

import gate.lang.property.Attribute;
import gate.util.Instance;

import java.util.Map;

record MapStrategy() implements ConstructionStrategy
{
	@Override
	public Object construct(Class<?> type, Map<Attribute, Object> attributes)
	{
		var value = Instance.createMap(type);
		for (var attribute : attributes.entrySet())
			attribute.getKey().setValue(value, attribute.getValue());
		return value;
	}
}
