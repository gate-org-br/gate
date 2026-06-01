package gate.lang.constructionStrategy;

import gate.error.ConstructionException;
import gate.function.TriFunction;
import gate.lang.property.Attribute;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public record CanonicalConstructorStrategy(Constructor<?> constructor) implements ConstructionStrategy
{
	@Override
	public Object construct(Class<?> type, Map<Attribute, Object> attributes)
	{
		try
		{
			if (!attributes.isEmpty()
			    && attributes.values().stream().allMatch(Objects::isNull))
				return null;

			var constructorAttributes = Arguments.getConstructorAttributes(constructor, attributes.keySet());

			var values = Arguments.getArguments(constructor, attributes);

			Arguments.checkPrimitiveArguments(type, constructor, attributes.keySet(), values);

			var value = constructor.newInstance(values);
			for (var entry : attributes.entrySet())
				if (!constructorAttributes.contains(entry.getKey()))
					entry.getKey().setValue(value, entry.getValue());
			return value;
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

			if (attributes.values().stream().allMatch(Objects::isNull))
				return null;

			var constructorAttributes = Arguments.getConstructorAttributes(constructor, attributes.keySet());
			var values = Arguments.getArguments(constructor, attributes);
			Arguments.checkPrimitiveArguments(type, constructor, attributes.keySet(), values);

			value = constructor.newInstance(values);
			for (var entry : attributes.entrySet())
			{
				var attribute = entry.getKey();
				if (!constructorAttributes.contains(attribute))
					attribute.setValue(value, entry.getValue());
			}

			return value;
		} catch (ReflectiveOperationException | IllegalArgumentException ex)
		{
			throw new ConstructionException(type, constructor, propertyMap.keySet(), ex);
		}
	}
}