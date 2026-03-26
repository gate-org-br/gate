package gate.converter;

import gate.constraint.Constraint;
import gate.error.ConversionException;
import gate.io.Encoder;
import gate.lang.json.JsonScanner;
import gate.lang.json.JsonToken;
import gate.lang.json.JsonWriter;
import gate.lang.property.Attribute;
import gate.lang.property.ConstructionStrategy;
import gate.lang.property.FieldAttribute;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

public class ObjectConverter implements Converter
{
	@Override
	public List<Constraint.Implementation<?>> getConstraints()
	{
		return Collections.emptyList();
	}

	@Override
	public String getMask()
	{
		return null;
	}

	@Override
	public String getDescription()
	{
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object ofString(Class<?> type, String string) throws ConversionException
	{
		return Encoder.of((Class<Object>) type).decode(string);
	}

	@Override
	@SuppressWarnings("unchecked")
	public String toString(Class<?> type, Object object)
	{
		return Encoder.of((Class<Object>) type).encode(object);
	}

	@Override
	public String toText(Class<?> type, Object object)
	{
		return object != null ? object.toString().trim() : "";
	}

	@Override
	public String toText(Class<?> type, Object object, String format)
	{
		return String.format(format, toText(type, object));
	}

	@Override
	public Object readFromResultSet(ResultSet rs, int fields, Class<?> type)
			throws SQLException, ConversionException
	{
		String value = rs.getString(fields);
		return rs.wasNull() ? null : ofString(type, value);
	}

	@Override
	public Object readFromResultSet(ResultSet rs, String fields, Class<?> type)
			throws SQLException, ConversionException
	{
		String value = rs.getString(fields);
		return rs.wasNull() ? null : ofString(type, value);
	}

	@Override
	public int writeToPreparedStatement(PreparedStatement ps, int fields, Object value)
			throws SQLException
	{
		if (value != null)
			ps.setString(fields++, toString(value.getClass(), value));
		else
			ps.setNull(fields++, Types.VARCHAR);
		return fields;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object ofJson(JsonScanner scanner, Type type, Type elementType)
			throws ConversionException
	{
		try
		{
			if (scanner.getCurrent().getType() != JsonToken.Type.OPEN_OBJECT)
				throw new ConversionException(scanner.getCurrent() + " is not a valid JSON object");

			boolean empty = true;

			Class<Object> clazz = (Class<Object>) type;
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
							.ofJson(scanner, attribute.getGenericType(), attribute.getRawType());
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
		writer.write(JsonToken.Type.OPEN_OBJECT, null);

		int i = 0;
		var attributes = FieldAttribute.getAttributes(type);
		for (Attribute attribute : attributes.values())
		{
			Object value = attribute.getValue(object);
			if (value != null && stack.stream().noneMatch(e -> e == value))
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
		writer.write(JsonToken.Type.OPEN_OBJECT, null);

		int i = 0;
		var attributes = FieldAttribute.getAttributes(type);
		for (Attribute attribute : attributes.values())
		{
			Object value = attribute.getValue(object);
			if (value != null && stack.stream().noneMatch(e -> e == value))
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
}