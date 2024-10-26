package gate.lang.property;

import gate.error.ConversionException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

			var attributes = property.getAttributes()
					.stream().skip(1).toList();

			Map<Attribute, Object> value = result;
			for (int i = 0; i < attributes.size() - 1; i++)
			{
				var attribute = attributes.get(i);
				value = (Map<Attribute, Object>) value
						.computeIfAbsent(attribute, e -> new LinkedHashMap<>());
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

	@SuppressWarnings("unchecked")
	public Object get(Object value, Function<Property, Object> getValue)
			throws ConversionException
	{
		try
		{
			return get(type, value, graph, getValue);
		} catch (ReflectiveOperationException ex)
		{
			throw new ConversionException("Error trying to match", ex);
		}
	}

	private Object get(Class<?> type,
					   Object value,
					   Object properties,
					   Function<Property, Object> getValue) throws
			ReflectiveOperationException
	{
		if (properties instanceof Property property)
			return getValue.apply(property);

		if (properties instanceof Map<?, ?> map)
		{
			if (type.isRecord())
			{
				var types = new ArrayList<Class<?>>();
				List<Object> values = new ArrayList<>();
				for (var component : type.getRecordComponents())
				{
					types.add(component.getType());
					var parameters = map.entrySet().stream()
							.filter(e -> e.getKey().toString().equals(component.getName()))
							.map(Map.Entry::getValue)
							.findAny()
							.orElse(null);
					values.add(get(component.getType(),
							null,
							parameters,
							getValue));
				}
				var constructor = type.getDeclaredConstructor(types.toArray(new Class[0]));
				constructor.setAccessible(true);
				return constructor.newInstance(values.toArray());
			} else
			{
				if (value == null)
					value = ObjectFactory.create(type);
				for (var entry : map.entrySet())
				{
					var attribute = ((Attribute) entry.getKey());
					var currentValue = attribute.getValue(value);
					var newValue = get(attribute.getRawType(),
							currentValue,
							entry.getValue(),
							getValue);
					if (newValue != currentValue)
						attribute.setValue(value, newValue);
				}
				return value;
			}
		}
		return null;
	}
}
