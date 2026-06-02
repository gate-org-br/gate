package gate.lang.property;

import gate.annotation.Discriminator;
import gate.annotation.Subtype;
import gate.error.ConversionException;
import gate.error.PropertyError;
import gate.lang.constructionStrategy.ConstructionStrategy;

import java.lang.reflect.Field;
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
	private final Class<T> type;
	private final Map<Attribute, Object> graph;

	private static final Map<Class<?>, Map<List<String>, PropertyGraph<?>>> CACHE = new ConcurrentHashMap<>();


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

	public PropertyGraph(Class<T> type, Map<Attribute, Object> graph)
	{
		this.type = type;
		this.graph = Map.copyOf(graph);
	}

	public Object populate(Object value, Function<Property, Object> getValue)
	{
		return populate(type, value, graph, getValue);
	}

	Object populate(Class<?> type, Object value, Object properties, Function<Property, Object> getValue)
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
					(a, current, v) -> populate(a.getRawType(), current, v, getValue));
		} catch (ReflectiveOperationException ex)
		{
			throw new ConversionException("Error trying to create a %s with properties %s: %s"
					.formatted(type.getName(),
							properties.toString()
							, ex.getMessage()));
		}
	}

	private static Property findProperty(Class<?> type, String name)
	{
		var property = Property.parse(type, name);
		if (property != null)
			return property;

		var discriminator = getDiscriminator(type);
		if (discriminator != null)
		{
			var properties = Arrays.stream(discriminator.getRawType().getEnumConstants())
					.map(Enum.class::cast)
					.map(constant ->
					{
						try
						{
							var field = discriminator.getRawType().getField(constant.name());
							return field.getAnnotation(Subtype.class).value();
						} catch (NoSuchFieldException ex)
						{
							throw new PropertyError("Invalid discriminator constant %s.%s"
									.formatted(discriminator.toString(), constant.name()));
						}
					})
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

	public static FieldAttribute getDiscriminator(Class<?> type)
	{
		var discriminators = Arrays.stream(type.getDeclaredFields())
				.filter(e -> e.isAnnotationPresent(Discriminator.class))
				.toList();

		if (discriminators.isEmpty())
			return null;

		if (discriminators.size() > 1)
			throw new PropertyError("Ambiguous discriminator properties found on %s: %s"
					.formatted(type.getName(), discriminators.stream()
							.map(Field::getName)
							.toList()));

		var discriminator = discriminators.getFirst();

		if (!discriminator.getType().isEnum())
			throw new PropertyError("Invalid discriminator property %s on %s"
					.formatted(discriminator.getName(), type.getName()));

		for (var constant : discriminator.getType().getEnumConstants())
		{
			var value = (Enum<?>) constant;
			try
			{
				var field = discriminator.getType().getField(value.name());
				if (!field.isAnnotationPresent(Subtype.class))
					throw new PropertyError("Missing subtype on discriminator constant %s.%s"
							.formatted(discriminator.getName(), value.name()));

				var subtype = field.getAnnotation(Subtype.class).value();
				if (!type.isAssignableFrom(subtype))
					throw new PropertyError("Invalid subtype %s on discriminator constant %s.%s"
							.formatted(subtype.getName(), discriminator.getName(), value.name()));
			} catch (NoSuchFieldException ex)
			{
				throw new PropertyError("Invalid discriminator constant %s.%s"
						.formatted(discriminator.getName(), value.name()));
			}
		}

		return FieldAttribute.of(discriminator);
	}

}
