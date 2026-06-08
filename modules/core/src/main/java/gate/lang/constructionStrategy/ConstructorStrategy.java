package gate.lang.constructionStrategy;

import gate.error.ConstructionException;
import gate.lang.property.Attribute;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;

public record ConstructorStrategy(Constructor<?> constructor) implements ConstructionStrategy
{
	@Override
	public Object construct(Class<?> type, Map<Attribute, Object> attributes)
	{
		try
		{
			if (!attributes.isEmpty()
					&& attributes.values().stream().allMatch(Objects::isNull))
				return null;

			return constructor.newInstance(Arguments.of(type, constructor, attributes).values());
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