package gate.adapter.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.lang.json.*;
import gate.lang.property.Attribute;
import gate.lang.property.ConstructionStrategy;
import gate.lang.property.FieldAttribute;
import gate.util.Reflection;

import java.lang.reflect.Type;
import java.util.*;

public class ObjectConverter implements Converter
{
	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		Converter converter = Converter.getConverter(type);
		if (converter != this)
			return converter.ofString(type, string);
		return Encoder.of((Class<Object>) type).decode(string);
	}

	@Override
	@SuppressWarnings("unchecked")
	public String toString(Class<?> type, Object object)
	{
		Converter converter = Converter.getConverter(type);
		if (converter != this)
			return converter.toString(type, object);
		return Encoder.of((Class<Object>) type).encode(object);
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
	public Object ofJson(JsonScanner scanner, Type type, Type elementType)
			throws ConversionException
	{
		Class<Object> clazz = (Class<Object>) type;
		var jsonAdapter = JsonAdapter.of(clazz);
		if (jsonAdapter != null)
			return jsonAdapter.fromJson((JsonElement) Converter.getConverter(JsonElement.class)
					.ofJson(scanner, JsonElement.class, JsonElement.class));

		try
		{
			if (scanner.getCurrent().getType() != JsonToken.Type.OPEN_OBJECT)
				throw new ConversionException(scanner.getCurrent() + " is not a valid JSON object");

			boolean empty = true;

			Map<Attribute, Object> values = new HashMap<>();
			var attributes = FieldAttribute.getAttributes(clazz);
			do
			{
				scanner.scan();
				if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_OBJECT)
				{
					empty = false;
					if (scanner.getCurrent().getType() != JsonToken.Type.STRING)
						throw new ConversionException(scanner.getCurrent() + " is not a valid JSON object key");

					var attribute = attributes.get(scanner.getCurrent().toString());
					if (attribute == null)
						throw new ConversionException("%s.%s is not a valid property".formatted(clazz.getName(), scanner.getCurrent()));

					scanner.scan();
					if (scanner.getCurrent().getType() != JsonToken.Type.DOUBLE_DOT)
						throw new ConversionException(scanner.getCurrent() + " is not a valid JSON object");

					scanner.scan();
					Object value = attribute.getConverter()
							.ofJson(scanner, attribute.getGenericType(), Reflection.getElementType(attribute.getGenericType()));
					values.put(attribute, value);
				} else if (!empty)
					throw new ConversionException("the specified JsonElement is not a JsonObject");
			} while (scanner.getCurrent().getType() == JsonToken.Type.COMMA);

			if (scanner.getCurrent().getType() != JsonToken.Type.CLOSE_OBJECT)
				throw new ConversionException(scanner.getCurrent() + " is not a valid JSON object");

			scanner.scan();
			return ConstructionStrategy.newInstance(clazz, values);
		} catch (ReflectiveOperationException ex)
		{
			throw new ConversionException(ex.getMessage());
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void toJson(Deque<Object> stack, JsonWriter writer, Class<T> type, T object) throws ConversionException
	{
		var jsonAdapter = JsonAdapter.of(type);
		if (jsonAdapter != null)
		{
			JsonElement json = jsonAdapter.toJson(object);
			Converter.getConverter(JsonElement.class)
					.toJson(stack, writer, JsonElement.class, json);
			return;
		}

		writer.write(JsonToken.Type.OPEN_OBJECT, null);

		int i = 0;
		var attributes = FieldAttribute.getAttributes(type);
		for (FieldAttribute attribute : attributes.values())
		{
			Object value = attribute.getFieldValue(object);
			if (shouldWrite(value) && stack.stream().noneMatch(e -> e == value))
			{
				if (i++ > 0)
					writer.write(JsonToken.Type.COMMA, null);

				writer.write(JsonToken.Type.STRING, attribute.toString());
				writer.write(JsonToken.Type.DOUBLE_DOT, null);
				stack.push(value);
				attribute.getConverter().toJson(stack, writer, (Class<Object>) attribute.getRawType(), value);
				stack.pop();
			}
		}

		writer.write(JsonToken.Type.CLOSE_OBJECT, null);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void toJsonText(Deque<Object> stack, JsonWriter writer, Class<T> type, T object) throws ConversionException
	{
		var jsonAdapter = JsonAdapter.of(type);
		if (jsonAdapter != null)
		{
			JsonElement json = jsonAdapter.toJsonText(object);
			Converter.getConverter(JsonElement.class).toJsonText(stack, writer, JsonElement.class, json);
			return;
		}

		writer.write(JsonToken.Type.OPEN_OBJECT, null);

		int i = 0;
		var attributes = FieldAttribute.getAttributes(type);
		for (FieldAttribute attribute : attributes.values())
		{
			Object value = attribute.getFieldValue(object);
			if (shouldWrite(value) && stack.stream().noneMatch(e -> e == value))
			{
				if (i++ > 0)
					writer.write(JsonToken.Type.COMMA, null);

				String name = Objects.requireNonNullElse(attribute.getMetadata().name(), attribute.toString());

				writer.write(JsonToken.Type.STRING, name);
				writer.write(JsonToken.Type.DOUBLE_DOT, null);

				stack.push(value);
				attribute.getConverter().toJsonText(stack, writer, (Class<Object>) attribute.getRawType(), value);
				stack.pop();
			}
		}

		writer.write(JsonToken.Type.CLOSE_OBJECT, null);
	}

	private boolean shouldWrite(Object value)
	{
		return value != null
		       && (!(value instanceof Collection<?> collection) || !collection.isEmpty())
		       && (!(value instanceof Map<?, ?> map) || !map.isEmpty())
		       && (!value.getClass().isArray() || java.lang.reflect.Array.getLength(value) > 0);
	}
}