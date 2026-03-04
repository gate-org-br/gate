package gate.lang.property;

import gate.error.ConversionException;
import gate.util.Reflection;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

public class PropertyGraph<T>
{
	private final Class<T> type;
	private final Map<Attribute, Object> graph;

	@SuppressWarnings("unchecked")
	public static <T> PropertyGraph<T> of(Class<T> type, List<String> properties)
	{
		Map<Attribute, Object> result = new LinkedHashMap<>();

		for (var name : properties)
		{
			var property = Property.parse(type, name);
			if (property == null)
				continue;

			var attributes = property.getAttributes().stream().skip(1).toList();

			Map<Attribute, Object> value = result;
			for (int i = 0; i < attributes.size() - 1; i++)
			{
				var attribute = attributes.get(i);
				value = (Map<Attribute, Object>) value.computeIfAbsent(attribute, e -> new LinkedHashMap<>());
			}
			value.put(attributes.get(attributes.size() - 1), property);
		}

		return new PropertyGraph<>(type, result);
	}

	public PropertyGraph(Class<T> type, Map<Attribute, Object> graph)
	{
		this.type = type;
		this.graph = graph;
	}

	public Object get(Object value, Function<Property, Object> getValue) throws ConversionException
	{
		return get(type, value, graph, getValue);
	}

	private Object get(Class<?> type, Object value, Object properties, Function<Property, Object> getValue)
	{
		try
		{
			if (properties instanceof Property property)
				return getValue.apply(property);

			if (!(properties instanceof Map<?, ?> map))
				return null;

			@SuppressWarnings("unchecked")
			var propertyMap = (Map<Attribute, Object>) map;

			if (type.isRecord())
				return fromRecord(type, propertyMap, getValue);

			var builderFactory = Reflection.findMethod(type, "builder").orElse(null);
			if (builderFactory != null)
				return fromBuilder(builderFactory, value, propertyMap, getValue);

			if (type.isInterface()
					|| Arrays.stream(type.getDeclaredConstructors())
					.anyMatch(c -> c.getParameterCount() == 0))
				return fromAnemic(type, value, propertyMap, getValue);

			return fromCanonicalConstructor(type, propertyMap, getValue);
		} catch (ReflectiveOperationException ex)
		{
			throw new ConversionException("Error trying to match", ex);
		}
	}

	private Object fromRecord(Class<?> type, Map<Attribute, Object> propertyMap, Function<Property, Object> getValue)
			throws ReflectiveOperationException
	{
		var values = new ArrayList<>();
		var types = new ArrayList<Class<?>>();
		for (var component : type.getRecordComponents())
		{
			types.add(component.getType());
			var parameters = propertyMap.entrySet().stream()
					.filter(e -> e.getKey().toString().equals(component.getName()))
					.map(Map.Entry::getValue)
					.findAny()
					.orElse(null);
			values.add(get(component.getType(), null, parameters, getValue));
		}
		var constructor = type.getDeclaredConstructor(types.toArray(new Class[0]));
		constructor.setAccessible(true);
		return constructor.newInstance(values.toArray());
	}

	private Object fromBuilder(Method builderFactory, Object value,
							   Map<Attribute, Object> propertyMap, Function<Property, Object> getValue)
			throws ReflectiveOperationException
	{
		var builder = builderFactory.invoke(null);
		var build = Reflection.findMethod(builder.getClass(), "build").orElse(null);
		if (build == null)
			return null;
		for (var entry : propertyMap.entrySet())
		{
			var attribute = entry.getKey();
			var newValue = get(attribute.getRawType(), attribute.getValue(value), entry.getValue(), getValue);
			Reflection.findMethod(builder.getClass(), attribute.toString(), attribute.getRawType())
					.orElseThrow()
					.invoke(builder, newValue);
		}
		return build.invoke(builder);
	}

	private Object fromAnemic(Class<?> type, Object value, Map<Attribute, Object> propertyMap, Function<Property, Object> getValue)
			throws ReflectiveOperationException
	{
		if (value == null)
			value = ObjectFactory.create(type);
		for (var entry : propertyMap.entrySet())
		{
			var attribute = entry.getKey();
			var currentValue = attribute.getValue(value);
			var newValue = get(attribute.getRawType(), currentValue, entry.getValue(), getValue);
			if (newValue != currentValue)
				attribute.setValue(value, newValue);
		}
		return value;
	}

	private Object fromCanonicalConstructor(Class<?> type, Map<Attribute, Object> propertyMap, Function<Property, Object> getValue)
			throws ReflectiveOperationException
	{
		var attributes = propertyMap.keySet();
		var candidates = Arrays.stream(type.getDeclaredConstructors())
				.filter(c ->
				{
					var params = c.getParameters();
					return params.length == propertyMap.size()
							&& Arrays.stream(params).allMatch(p -> attributes.stream().anyMatch(a -> a.matches(p)));
				})
				.toList();

		if (candidates.size() != 1)
			throw new ConversionException("Could not find canonical constructor for " + type.getName());

		var constructor = candidates.get(0);
		Object[] args = Arrays.stream(constructor.getParameters())
				.map(p -> attributes.stream()
						.filter(a -> a.matches(p))
						.findFirst()
						.map(e -> get(e.getRawType(), null, propertyMap.get(e), getValue))
						.orElseThrow())
				.toArray();
		return constructor.newInstance(args);
	}
}
