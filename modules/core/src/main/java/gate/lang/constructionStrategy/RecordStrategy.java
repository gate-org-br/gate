package gate.lang.constructionStrategy;

import gate.error.ConstructionException;
import gate.function.TriFunction;
import gate.lang.property.Attribute;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;

public record RecordStrategy(Constructor<?> constructor) implements ConstructionStrategy
{
	@Override
	public Object construct(Class<?> type, Map<Attribute, Object> attributes)
	{
		try
		{
			return constructor.newInstance(Arguments.of(type, constructor, attributes).values());
		} catch (ReflectiveOperationException | IllegalArgumentException ex)
		{
			throw new ConstructionException(type, constructor, attributes.keySet(), ex);
		}
	}

	@Override
	public Object construct(Class<?> type,
	                        Object value,
	                        Map<Attribute, Object> propertyMap,
	                        TriFunction<Attribute, Object, Object, Object> getValue)
			throws ReflectiveOperationException
	{
		try
		{
			var attributes = new LinkedHashMap<Attribute, Object>();
			for (var entry : propertyMap.entrySet())
				attributes.put(entry.getKey(), getValue.apply(entry.getKey(), null, entry.getValue()));
			return constructor.newInstance(Arguments.of(type, constructor, attributes).values());
		} catch (ReflectiveOperationException | IllegalArgumentException ex)
		{
			throw new ConstructionException(type, constructor, propertyMap.keySet(), ex);
		}
	}
}
