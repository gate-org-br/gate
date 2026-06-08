package gate.lang.constructionStrategy;

import gate.error.ConstructionException;
import gate.lang.property.Attribute;
import gate.util.Reflection;

import java.lang.reflect.InvocationTargetException;
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
		} catch (InvocationTargetException ex)
		{
			throw new ConstructionException(
					"Failed to build '%s': %s"
							.formatted(type.getName(), ex.getCause().getMessage()), ex.getCause());
		} catch (ReflectiveOperationException ex)
		{
			throw new ConstructionException(
					"Failed to invoke builder for '%s': %s"
							.formatted(type.getName(), ex.getMessage()), ex);
		}
	}
}