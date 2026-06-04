package gate.lang.constructionStrategy;

import gate.error.ConstructionException;
import gate.lang.property.Attribute;

import java.lang.reflect.Constructor;
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
}