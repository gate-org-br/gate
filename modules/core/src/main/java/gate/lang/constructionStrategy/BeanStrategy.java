package gate.lang.constructionStrategy;

import gate.error.ConstructionException;
import gate.lang.property.Attribute;

import java.lang.reflect.Constructor;
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
}