package gate.lang.constructionStrategy;

import gate.error.ConstructionException;
import gate.function.TriFunction;
import gate.lang.property.Attribute;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record SealedConstructorStrategy(Map<Set<String>, Executable> candidates)
		implements ConstructionStrategy
{
	@Override
	public Object construct(Class<?> type, Map<Attribute, Object> attributes)
	{
		attributes = attributes.entrySet().stream()
				.filter(e -> e.getValue() != null)
				.collect(Collectors.toMap(Map.Entry::getKey,
						Map.Entry::getValue));

		var executable = candidates.get(attributes.keySet().stream()
				.map(Object::toString)
				.collect(Collectors.toSet()));
		if (executable == null)
			throw new ConstructionException(type, attributes.keySet());

		var values = Arguments.getArguments(executable, attributes);
		Arguments.checkPrimitiveArguments(type, executable, attributes.keySet(), values);

		try
		{
			if (executable instanceof Constructor<?> constructor)
				return constructor.newInstance(values);
			if (executable instanceof Method method)
				return method.invoke(null, values);
			throw new IllegalStateException();
		} catch (ReflectiveOperationException | IllegalArgumentException ex)
		{
			throw new ConstructionException(type, executable, attributes.keySet(), ex);
		}
	}

	@Override
	public Object construct(Class<?> type,
	                        Object value,
	                        Map<Attribute, Object> propertyMap,
	                        TriFunction<Attribute, Object, Object, Object> getValue)
			throws ReflectiveOperationException
	{
		var attributes = new LinkedHashMap<Attribute, Object>();
		for (var entry : propertyMap.entrySet())
		{
			var argument = getValue.apply(entry.getKey(), null, entry.getValue());
			if (argument != null)
				attributes.put(entry.getKey(), argument);
		}

		var executable = candidates.get(attributes.keySet().stream()
				.map(Object::toString)
				.collect(Collectors.toSet()));
		if (executable == null)
			throw new ConstructionException(type, attributes.keySet());

		var values = Arguments.getArguments(executable, attributes);
		Arguments.checkPrimitiveArguments(type, executable, attributes.keySet(), values);

		try
		{
			if (executable instanceof Constructor<?> constructor)
				return constructor.newInstance(values);
			if (executable instanceof Method method)
				return method.invoke(null, values);
			throw new IllegalStateException();
		} catch (ReflectiveOperationException | IllegalArgumentException ex)
		{
			throw new ConstructionException(type, executable, attributes.keySet(), ex);
		}
	}
}