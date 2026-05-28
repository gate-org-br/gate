package gate.lang.constructionStrategy;

import gate.error.ConstructionException;
import gate.function.TriFunction;
import gate.lang.property.Attribute;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public record FactoryMethodStrategy(Method method) implements ConstructionStrategy
{
	@Override
	public Object construct(Class<?> type, Map<Attribute, Object> attributes)
	{
		try
		{
			if (attributes.values().stream().allMatch(Objects::isNull))
				return null;

			var values = Arguments.getArguments(method, attributes);
			Arguments.checkPrimitiveArguments(type, method, attributes.keySet(), values);
			return method.invoke(null, values);
		} catch (ReflectiveOperationException | IllegalArgumentException ex)
		{
			throw new ConstructionException(type, method, attributes.keySet(), ex);
		}
	}

	@Override
	public Object construct(Class<?> type,
	                        Object value,
	                        Map<Attribute, Object> propertyMap,
	                        TriFunction<Attribute, Object, Object, Object> getValue)
	{
		try
		{
			var attributes = new LinkedHashMap<Attribute, Object>();
			for (var entry : propertyMap.entrySet())
				attributes.put(entry.getKey(), getValue.apply(entry.getKey(), null, entry.getValue()));

			if (attributes.values().stream().allMatch(Objects::isNull))
				return null;

			var args = Arguments.getArguments(method, attributes);
			Arguments.checkPrimitiveArguments(type, method, attributes.keySet(), args);
			return method.invoke(null, args);
		} catch (ReflectiveOperationException | IllegalArgumentException ex)
		{
			throw new ConstructionException(type, method, propertyMap.keySet(), ex);
		}
	}
}
