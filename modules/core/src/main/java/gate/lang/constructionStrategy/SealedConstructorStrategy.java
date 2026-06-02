package gate.lang.constructionStrategy;

import gate.annotation.Canonical;
import gate.error.ConstructionException;
import gate.function.TriFunction;
import gate.lang.property.Attribute;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public record SealedConstructorStrategy(Map<Set<ConstructionStrategy.ParameterKey>, Executable> candidates)
		implements ConstructionStrategy
{
	@Override
	public Object construct(Class<?> type, Map<Attribute, Object> attributes)
	{
		attributes = attributes.entrySet().stream()
				.filter(e -> e.getValue() != null)
				.collect(Collectors.toMap(Map.Entry::getKey,
						Map.Entry::getValue));

		return instantiate(type, attributes);
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

		return instantiate(type, attributes);
	}

	private Object instantiate(Class<?> type, Map<Attribute, Object> attributes)
	{
		var compatible = candidates.values().stream()
				.filter(e -> attributes.keySet().stream()
						.allMatch(a -> Arrays.stream(e.getParameters()).anyMatch(a::matches)))
				.toList();
		if (compatible.isEmpty())
			throw new ConstructionException(type, attributes.keySet());
		
		Executable selected = compatible.size() == 1 ? compatible.getFirst() : null;

		if (selected == null)
		{
			var exact = compatible.stream()
					.filter(e -> e.getParameterCount() == attributes.size())
					.toList();
			selected = exact.size() == 1 ? exact.getFirst() : null;
		}

		if (selected == null)
		{
			var canonical = compatible.stream()
					.filter(e -> e.isAnnotationPresent(Canonical.class))
					.toList();
			selected = canonical.size() == 1 ? canonical.getFirst() : null;
		}

		if (selected == null)
			throw new ConstructionException(type, attributes.keySet());

		var arguments = Arguments.of(type, selected, attributes);
		try
		{
			if (selected instanceof Constructor<?> constructor)
				return constructor.newInstance(arguments.values());
			if (selected instanceof Method method)
				return method.invoke(null, arguments.values());
			throw new IllegalStateException();
		} catch (ReflectiveOperationException | IllegalArgumentException ex)
		{
			throw new ConstructionException(type, selected, attributes.keySet(), ex);
		}
	}
}