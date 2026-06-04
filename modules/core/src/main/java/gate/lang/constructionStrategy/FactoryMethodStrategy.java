package gate.lang.constructionStrategy;

import gate.error.ConstructionException;
import gate.lang.property.Attribute;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

public record FactoryMethodStrategy(Method method) implements ConstructionStrategy
{
	@Override
	public Object construct(Class<?> type, Map<Attribute, Object> attributes)
	{
		try
		{
			if (!attributes.isEmpty()
					&& attributes.values().stream().allMatch(Objects::isNull))
				return null;

			return method.invoke(null, Arguments.of(type, method, attributes).values());
		} catch (ReflectiveOperationException | IllegalArgumentException ex)
		{
			throw new ConstructionException(type, method, attributes.keySet(), ex);
		}
	}
}