package gate.lang.constructionStrategy;

import gate.error.ConstructionException;
import gate.function.TriFunction;
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
			var values = Arguments.getArguments(constructor, attributes);
			Arguments.checkPrimitiveArguments(type, constructor, attributes.keySet(), values);
			return constructor.newInstance(values);
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
			var args = Arguments.getArguments(constructor, propertyMap, getValue);
			Arguments.checkPrimitiveArguments(type, constructor, propertyMap.keySet(), args);
			return constructor.newInstance(args);
		} catch (ReflectiveOperationException | IllegalArgumentException ex)
		{
			throw new ConstructionException(type, constructor, propertyMap.keySet(), ex);
		}
	}
}