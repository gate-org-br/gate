package gate.lang.property;

import gate.annotation.Discriminator;
import gate.annotation.Subtype;
import gate.error.PropertyError;
import gate.lang.constructionStrategy.ConstructionStrategy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Precompiled graph of nested {@link Property} paths used to materialize object trees from a
 * flat property source.
 * <p>
 * A graph is built from property names such as {@code "role.name"} or {@code "items[0].id"} and
 * then resolved recursively. Leaf properties obtain their value from the supplied
 * {@code Function<Property, Object>}, while intermediate nodes are materialized through
 * {@link ConstructionStrategy}.
 * <p>
 * This means partial updates follow the construction model of each target type:
 * <ul>
 *     <li>beans reuse or create an instance and apply only the provided attributes;</li>
 *     <li>builders receive only the provided attributes;</li>
 *     <li>records, constructors and static factories receive {@code null} for missing attributes.</li>
 * </ul>
 * As a result, immutable nested objects may be rebuilt from the provided subset, while mutable
 * anemic objects preserve attributes that were not present in the graph.
 */
public class PropertyGraph<T>
{
	private static final Map<Class<?>, Map<List<String>, PropertyGraph<?>>> CACHE = new ConcurrentHashMap<>();
	private final Class<T> type;
	private final Map<Attribute, Object> graph;

	public PropertyGraph(Class<T> type, Map<Attribute, Object> graph)
	{
		this.type = type;
		this.graph = Map.copyOf(graph);
	}

	@SuppressWarnings("unchecked")
	public static <T> PropertyGraph<T> of(Class<T> type, List<String> properties)
	{
		return (PropertyGraph<T>) CACHE
				.computeIfAbsent(type, e -> new ConcurrentHashMap<>())
				.computeIfAbsent(properties, key ->
				{
					Map<Attribute, Object> result = new LinkedHashMap<>();

					for (var name : key)
					{
						var property = findProperty(type, name);
						if (property == null)
							continue;

						var attributes = property.getAttributes().stream().skip(1).toList();

						Map<Attribute, Object> value = result;
						for (var attribute : attributes.subList(0, attributes.size() - 1))
							value = (Map<Attribute, Object>) value.computeIfAbsent(attribute,
									e -> new LinkedHashMap<>());
						value.put(attributes.get(attributes.size() - 1), property);
					}

					return new PropertyGraph<>(type, result);
				});
	}
	private static Property findProperty(Class<?> type, String name)
	{
		var property = Property.parse(type, name);
		if (property != null)
			return property;

		var discriminator = Discriminator.Extractor.extract(type);
		if (discriminator != null)
		{
			var properties = Subtype.Extractor.extract(discriminator)
					.stream()
					.map(e -> findProperty(e, name))
					.filter(Objects::nonNull)
					.toList();

			if (properties.isEmpty())
				return null;

			if (properties.size() == 1)
				return properties.get(0);

			throw new PropertyError("Ambiguous discriminator property %s on %s: found in %s"
					.formatted(name, type.getName(), properties.stream()
							.map(Property::getOwner)
							.map(Class::getName)
							.toList()));
		}

		if (type.isSealed())
		{
			var properties = Arrays.stream(type.getPermittedSubclasses())
					.map(e -> findProperty(e, name))
					.filter(Objects::nonNull)
					.toList();

			if (properties.isEmpty())
				return null;

			if (properties.size() == 1)
				return properties.get(0);

			throw new PropertyError("Ambiguous sealed property %s on %s: found in %s"
					.formatted(name, type.getName(), properties.stream()
							.map(Property::getOwner)
							.map(Class::getName)
							.toList()));
		}

		return null;
	}

	public void populate(Object value, Function<Property, Object> getValue)
	{
		populate(value, graph, getValue);
	}

	public Object populate(Function<Property, Object> getValue)
	{
		return populate(type, graph, getValue);
	}

	void populate(Object object, Object properties, Function<Property, Object> getValue)
	{
		if (properties instanceof Map<?, ?> map)
		{
			var propertyMap = (Map<Attribute, Object>) map;
			for (var entry : propertyMap.entrySet())
			{
				var attribute = entry.getKey();
				var property = entry.getValue();
				attribute.setValue(object, populate(attribute.getRawType(), property, getValue));
			}
		} else if (properties instanceof Property property)
			property.setValue(object, getValue.apply(property));
	}

	Object populate(Class<?> type, Object properties, Function<Property, Object> getValue)
	{
		if (properties instanceof Property property)
			return getValue.apply(property);

		if (!(properties instanceof Map<?, ?> map))
			return null;

		Map<Attribute, Object> attributes = new HashMap<>();
		var propertyMap = (Map<Attribute, Object>) map;
		for (var entry : propertyMap.entrySet())
		{
			var attribute = entry.getKey();
			var property = entry.getValue();
			var value = populate(attribute.getRawType(), property, getValue);
			attributes.put(attribute, value);
		}
		return ConstructionStrategy.newInstance(type, attributes);
	}
}