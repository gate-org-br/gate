package gate.adapter.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.function.TryFunction;
import gate.lang.json.JsonScanner;
import gate.lang.json.JsonToken;
import gate.lang.json.JsonWriter;
import gate.util.Reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class RecordConverter implements Converter
{
	private static final Map<Class<?>, RecordComponent[]> COMPONENTS = new ConcurrentHashMap<>();

	private static final Map<Class<?>, Constructor<?>> CONSTRUCTORS = new ConcurrentHashMap<>();

	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		if (string == null || string.isBlank())
			return null;
		return Converter.fromJson(type,
				new String(Base64.getDecoder().decode(string), StandardCharsets.UTF_8));
	}

	@Override
	public String toString(Class<?> type, Object object)
	{
		return Base64.getEncoder().encodeToString(Converter.toJson(object).getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public String render(Class<?> type, Object object)
	{
		return object != null ? object.toString().trim() : "";
	}

	@Override
	public String render(Class<?> type, Object object, String format)
	{
		return String.format(format, render(type, object));
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object ofJson(JsonScanner scanner, Type type, Type elementType) throws ConversionException
	{
		if (scanner.getCurrent().getType() == JsonToken.Type.NULL)
			return null;

		try
		{
			if (scanner.getCurrent().getType() != JsonToken.Type.OPEN_OBJECT)
				throw new ConversionException(scanner.getCurrent() + " is not a valid JSON object");

			int i = -1;
			var clazz = (Class<Object>) type;
			var components = COMPONENTS.computeIfAbsent(clazz, Class::getRecordComponents);

			Map<RecordComponent, Object> map = new HashMap<>();

			do
			{
				scanner.scan();
				if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_OBJECT)
				{
					i++;
					if (scanner.getCurrent().getType() != JsonToken.Type.STRING)
						throw new ConversionException(scanner.getCurrent() + " is not a valid JSON object key");

					var name = scanner.getCurrent().toString();

					var component = Stream.of(components).filter(e -> e.getName().equals(name))
							.findFirst().orElseThrow(() -> new NoSuchFieldException(name));

					scanner.scan();
					if (scanner.getCurrent().getType() != JsonToken.Type.DOUBLE_DOT)
						throw new ConversionException(scanner.getCurrent() + " is not a valid JSON object");

					scanner.scan();
					Type genericType = component.getGenericType();
					Converter converter = Converter.getConverter(component.getType());
					map.put(component, converter.ofJson(scanner, genericType, Reflection.getElementType(genericType)));
				} else if (i >= 0)
					throw new ConversionException("the specified JsonElement is not a JsonObject");
			} while (scanner.getCurrent().getType() == JsonToken.Type.COMMA);

			if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_OBJECT)
				throw new ConversionException(scanner.getCurrent() + " is not a valid JSON object");
			scanner.scan();

			for (var component : components)
				if (component.getType().isPrimitive() && map.get(component) == null)
					throw new ConversionException("Missing required field: %s for record %s".formatted(component.getName(), clazz.getName()));

			Object[] values = Stream.of(components).map(map::get).toArray();
			var constructor = CONSTRUCTORS.computeIfAbsent(clazz, TryFunction
					.wrap(c -> c.getDeclaredConstructor(Stream.of(components)
							.map(RecordComponent::getType).toArray(Class[]::new))));
			return constructor.newInstance(values);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchFieldException ex)
		{
			throw new ConversionException(ex.getMessage());
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void toJson(Deque<Object> stack, JsonWriter writer, Class<T> type, T object) throws ConversionException
	{
		try
		{
			if (object != null)
			{
				writer.write(JsonToken.Type.OPEN_OBJECT, null);
				var components = COMPONENTS.computeIfAbsent(type, Class::getRecordComponents);

				int i = 0;
				for (var recordComponent : components)
				{
					Object value = recordComponent.getAccessor().invoke(object);

					if (value != null)
					{
						if (i++ > 0)
							writer.write(JsonToken.Type.COMMA, null);
						writer.write(JsonToken.Type.STRING, recordComponent.getName());
						writer.write(JsonToken.Type.DOUBLE_DOT, null);
						Converter converter = Converter.getConverter(recordComponent.getType());
						converter.toJson(stack, writer, (Class<Object>) recordComponent.getType(), value);
					}
				}

				writer.write(JsonToken.Type.CLOSE_OBJECT, null);
			} else
				writer.write(JsonToken.Type.NULL, null);
		} catch (IllegalAccessException | InvocationTargetException ex)
		{
			throw new ConversionException(ex.getMessage());
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void toJsonText(Deque<Object> stack, JsonWriter writer, Class<T> type, T object) throws ConversionException
	{
		try
		{
			if (object != null)
			{
				writer.write(JsonToken.Type.OPEN_OBJECT, null);
				var components = COMPONENTS.computeIfAbsent(type, Class::getRecordComponents);

				int i = 0;
				for (var recordComponent : components)
				{
					Object value = recordComponent.getAccessor().invoke(object);

					if (value != null)
					{
						if (i++ > 0)
							writer.write(JsonToken.Type.COMMA, null);
						writer.write(JsonToken.Type.STRING, recordComponent.getName());
						writer.write(JsonToken.Type.DOUBLE_DOT, null);
						Converter converter = Converter.getConverter(recordComponent.getType());
						converter.toJsonText(stack, writer, (Class<Object>) recordComponent.getType(), value);
					}
				}

				writer.write(JsonToken.Type.CLOSE_OBJECT, null);
			} else
				writer.write(JsonToken.Type.NULL, null);
		} catch (IllegalAccessException | InvocationTargetException ex)
		{
			throw new ConversionException(ex.getMessage());
		}
	}
}