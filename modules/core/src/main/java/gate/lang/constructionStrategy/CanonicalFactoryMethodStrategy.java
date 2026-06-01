package gate.lang.constructionStrategy;

import gate.error.ConstructionException;
import gate.function.TriFunction;
import gate.lang.property.Attribute;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public record CanonicalFactoryMethodStrategy(Method method) implements ConstructionStrategy
{
	@Override
	public Object construct(Class<?> type, Map<Attribute, Object> attributes)
	{
		try
		{
			if (!attributes.isEmpty()
			    && attributes.values().stream().allMatch(Objects::isNull))
				return null;

			var constructorAttributes = Arguments.getConstructorAttributes(method, attributes.keySet());

			var values = Arguments.getArguments(method, attributes);

			Arguments.checkPrimitiveArguments(type, method, attributes.keySet(), values);

			var value = method.invoke(null, values);
			for (var entry : attributes.entrySet())
				if (!constructorAttributes.contains(entry.getKey()))
					entry.getKey().setValue(value, entry.getValue());
			return value;
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
			throws ReflectiveOperationException
	{
		try
		{
			var attributes = new LinkedHashMap<Attribute, Object>();
			for (var entry : propertyMap.entrySet())
				attributes.put(entry.getKey(), getValue.apply(entry.getKey(), null, entry.getValue()));

			if (attributes.values().stream().allMatch(Objects::isNull))
				return null;

			var constructorAttributes = Arguments.getConstructorAttributes(method, attributes.keySet());
			var args = Arguments.getArguments(method, attributes);
			Arguments.checkPrimitiveArguments(type, method, attributes.keySet(), args);

			value = method.invoke(null, args);

			for (var entry : attributes.entrySet())
			{
				var attribute = entry.getKey();
				if (!constructorAttributes.contains(attribute))
					attribute.setValue(value, entry.getValue());
			}

			return value;
		} catch (ReflectiveOperationException | IllegalArgumentException ex)
		{
			throw new ConstructionException(type, method, propertyMap.keySet(), ex);
		}
	}
}