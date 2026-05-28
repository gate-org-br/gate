package gate.lang.constructionStrategy;

import gate.function.TriFunction;
import gate.lang.property.Attribute;

import java.util.Map;
import java.util.function.Supplier;

record CollectionStrategy(Supplier<Object> supplier) implements ConstructionStrategy
{
	@Override
	public Object construct(Class<?> type, Map<Attribute, Object> attributes)
	{
		var value = supplier.get();
		for (var attribute : attributes.entrySet())
			attribute.getKey().setValue(value, attribute.getValue());
		return value;
	}

	@Override
	public Object construct(Class<?> type, Object value, Map<Attribute, Object> propertyMap,
	                        TriFunction<Attribute, Object, Object, Object> getValue)
	{
		if (value == null)
			value = supplier.get();
		for (var entry : propertyMap.entrySet())
		{
			var attribute = entry.getKey();
			var currentValue = attribute.getValue(value);
			var newValue = getValue.apply(attribute, currentValue, entry.getValue());
			if (newValue != currentValue)
				attribute.setValue(value, newValue);
		}
		return value;
	}
}