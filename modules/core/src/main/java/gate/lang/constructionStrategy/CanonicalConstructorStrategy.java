package gate.lang.constructionStrategy;

import gate.error.ConstructionException;
import gate.lang.property.Attribute;

import java.lang.reflect.Constructor;
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

			var arguments = Arguments.of(type, constructor, attributes);

			var value = constructor.newInstance(arguments.values());
			for (var entry : attributes.entrySet())
				if (!arguments.contains(entry.getKey()))
					entry.getKey().setValue(value, entry.getValue());
			return value;
		} catch (ReflectiveOperationException | IllegalArgumentException ex)
		{
			throw new ConstructionException(type, constructor, attributes.keySet(), ex);
		}
	}
}