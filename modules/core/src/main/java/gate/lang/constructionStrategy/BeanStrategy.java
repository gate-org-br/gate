package gate.lang.constructionStrategy;

import gate.error.ConstructionException;
import gate.function.TriFunction;
import gate.lang.property.Attribute;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

record BeanStrategy(Constructor<?> constructor) implements ConstructionStrategy
{
	@Override
	public Object construct(Class<?> type, Map<Attribute, Object> attributes)
	{
		try
		{
			if (attributes.values().stream().allMatch(Objects::isNull))
				return null;

			var value = constructor.newInstance();
			for (var attribute : attributes.entrySet())
				attribute.getKey().setValue(value, attribute.getValue());
			return value;
		} catch (ReflectiveOperationException ex)
		{
			throw new ConstructionException(type, constructor, attributes.keySet(), ex);
		}
	}

	@Override
	public Object construct(Class<?> type, Object value, Map<Attribute, Object> propertyMap,
	                        TriFunction<Attribute, Object, Object, Object> getValue)
	{
		try
		{
			var attributes = new LinkedHashMap<Attribute, Object>();
			for (var entry : propertyMap.entrySet())
				attributes.put(entry.getKey(), getValue.apply(entry.getKey(), null, entry.getValue()));
			if (value == null && attributes.values().stream().allMatch(Objects::isNull))
				return null;

			if (value == null)
				value = constructor.newInstance();

			for (var entry : propertyMap.entrySet())
			{
				var attribute = entry.getKey();
				var currentValue = attribute.getValue(value);
				var newValue = getValue.apply(attribute, currentValue, entry.getValue());
				if (newValue != currentValue)
					attribute.setValue(value, newValue);
			}
			return value;
		} catch (ReflectiveOperationException ex)
		{
			throw new ConstructionException(type, constructor, propertyMap.keySet(), ex);
		}
	}
}