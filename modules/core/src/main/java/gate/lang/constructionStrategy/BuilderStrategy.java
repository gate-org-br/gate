package gate.lang.constructionStrategy;

import gate.error.ConstructionException;
import gate.function.TriFunction;
import gate.lang.property.Attribute;
import gate.util.Reflection;

import java.lang.reflect.Method;
import java.util.Map;

record BuilderStrategy(Method builderFactory, Method build) implements ConstructionStrategy
{
	@Override
	public Object construct(Class<?> type, Map<Attribute, Object> attributes)
	{
		try
		{
			var builder = builderFactory.invoke(null);
			for (var entry : attributes.entrySet())
			{
				var attribute = entry.getKey();
				var method = Reflection.findMethod(builder.getClass(),
								attribute.toString(), attribute.getRawType())
						.orElseThrow(() -> new ConstructionException(builder.getClass(), attribute));
				method.invoke(builder, entry.getValue());
			}
			return build.invoke(builder);
		} catch (ReflectiveOperationException ex)
		{
			throw new ConstructionException(type, builderFactory.getReturnType(), attributes.keySet(), ex);
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
			var builder = builderFactory.invoke(null);
			for (var entry : propertyMap.entrySet())
			{
				var attribute = entry.getKey();
				var newValue = getValue.apply(attribute, null, entry.getValue());
				var method = Reflection.findMethod(builder.getClass(), attribute.toString(), attribute.getRawType())
						.orElseThrow(() -> new ConstructionException(builder.getClass(), attribute));
				method.invoke(builder, newValue);
			}
			return build.invoke(builder);
		} catch (ReflectiveOperationException ex)
		{
			throw new ConstructionException(type, builderFactory.getReturnType(), propertyMap.keySet(), ex);
		}
	}
}