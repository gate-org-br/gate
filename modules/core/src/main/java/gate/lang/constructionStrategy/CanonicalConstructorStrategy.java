package gate.lang.constructionStrategy;

import gate.error.ConstructionException;
import gate.lang.property.Attribute;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;

public record CanonicalConstructorStrategy(Constructor<?> constructor) implements ConstructionStrategy
{
	@Override
	public Object construct(Class<?> type, Map<Attribute, Object> attributes)
	{
		if (!attributes.isEmpty()
				&& attributes.values().stream().allMatch(Objects::isNull))
			return null;
		try
		{
			var arguments = Arguments.of(type, constructor, attributes);
			var value = constructor.newInstance(arguments.values());

			for (var entry : attributes.entrySet())
				try
				{
					if (!arguments.contains(entry.getKey()))
						entry.getKey().setValue(value, entry.getValue());
				} catch (RuntimeException ex)
				{
					throw new ConstructionException(
							"Failed to set attribute '%s' on '%s' after construction via %s: %s"
									.formatted(entry.getKey(), type.getName(), constructor, ex.getMessage()), ex);
				}

			return value;
		} catch (InvocationTargetException ex)
		{
			throw new ConstructionException(
					"Failed to invoke %s with attributes %s: %s"
							.formatted(constructor, attributes.keySet(), ex.getCause()), ex.getCause());
		} catch (ReflectiveOperationException ex)
		{
			throw new ConstructionException(
					"Failed to invoke %s with attributes %s: %s"
							.formatted(constructor, attributes.keySet(), ex.getMessage()), ex);
		}
	}
}