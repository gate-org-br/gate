package gate.lang.property;

import gate.error.ConversionException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class PropertyGraph<T>
{
	private final Class<T> type;
	private final Map<Attribute, Object> graph;

	private static final Map<Map.Entry<Class<?>, List<String>>, PropertyGraph<?>> CACHE = new ConcurrentHashMap<>();


	@SuppressWarnings("unchecked")
	public static <T> PropertyGraph<T> of(Class<T> type, List<String> properties)
	{
		return (PropertyGraph<T>) CACHE.computeIfAbsent(Map.entry(type, properties),
				entry ->
				{
					Map<Attribute, Object> result = new LinkedHashMap<>();

					for (var name : entry.getValue())
					{
						var property = Property.parse(entry.getKey(), name);
						if (property == null)
							continue;

						var attributes = property.getAttributes().stream().skip(1).toList();

						Map<Attribute, Object> value = result;
						for (var attribute : attributes.subList(0, attributes.size() - 1))
							value = (Map<Attribute, Object>) value.computeIfAbsent(attribute, e -> new LinkedHashMap<>());
						value.put(attributes.get(attributes.size() - 1), property);
					}

					return new PropertyGraph<>(entry.getKey(), result);
				});
	}

	public PropertyGraph(Class<T> type, Map<Attribute, Object> graph)
	{
		this.type = type;
		this.graph = Map.copyOf(graph);
	}

	public Object get(Object value, Function<Property, Object> getValue)
	{
		return get(type, value, graph, getValue);
	}

	Object get(Class<?> type, Object value, Object properties, Function<Property, Object> getValue)
	{
		try
		{
			if (properties instanceof Property property)
				return getValue.apply(property);

			if (!(properties instanceof Map<?, ?> map))
				return null;

			@SuppressWarnings("unchecked")
			var propertyMap = (Map<Attribute, Object>) map;

			return ConstructionStrategy.newInstance(type, value, propertyMap,
					(a, current, v) -> get(a.getRawType(), current, v, getValue));
		} catch (ReflectiveOperationException ex)
		{
			throw new ConversionException("Error trying to match", ex);
		}
	}
}