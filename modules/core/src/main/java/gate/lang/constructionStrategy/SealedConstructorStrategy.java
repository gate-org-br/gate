package gate.lang.constructionStrategy;

import gate.annotation.Canonical;
import gate.error.ConstructionException;
import gate.function.TriFunction;
import gate.lang.property.Attribute;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
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

		var selected = compatible.size() == 1 ? compatible.getFirst() : null;

		if (selected == null)
			selected = select(type, attributes.keySet(), compatible.stream()
					.filter(e -> e.getParameterCount() == attributes.size())
					.toList());
		if (selected == null)
			selected = select(type, attributes.keySet(), compatible.stream()
					.filter(e -> e.isAnnotationPresent(Canonical.class))
					.toList());
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

	private Executable select(Class<?> type,
	                          Set<Attribute> attributes,
	                          List<Executable> executables)
	{
		if (executables.isEmpty())
			return null;

		return executables.stream()
				.reduce((a, b) ->
				{
					boolean aIsMoreSpecificSomewhere = false;
					boolean bIsMoreSpecificSomewhere = false;

					for (var attribute : attributes)
					{
						var aType = Arrays.stream(a.getParameters())
								.filter(attribute::matches)
								.map(Parameter::getType)
								.findFirst()
								.orElseThrow();
						var bType = Arrays.stream(b.getParameters())
								.filter(attribute::matches)
								.map(Parameter::getType)
								.findFirst()
								.orElseThrow();

						if (aType == bType)
							continue;
						if (bType.isAssignableFrom(aType))
							aIsMoreSpecificSomewhere = true;
						else if (aType.isAssignableFrom(bType))
							bIsMoreSpecificSomewhere = true;
						else
							throw new ConstructionException(type, attributes, List.of(a, b));
					}

					if (aIsMoreSpecificSomewhere && !bIsMoreSpecificSomewhere)
						return a;

					if (bIsMoreSpecificSomewhere && !aIsMoreSpecificSomewhere)
						return b;

					throw new ConstructionException(type, attributes, List.of(a, b));
				})
				.orElseThrow();
	}
}